package carldata.pirx

import java.net.InetAddress
import java.time.{LocalDateTime, ZoneOffset}
import java.util.Properties

import com.datastax.driver.core.querybuilder.QueryBuilder
import com.datastax.driver.core.{Cluster, Session}
import com.timgroup.statsd.{NonBlockingStatsDClient, ServiceCheck, StatsDClient}
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecords, KafkaConsumer}
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._


/**
  * Connect to 'data' kafka topics, listen to the messages and try to predict future number of records
  * on this topic
  */
object MainApp {

  /** How long to wait for new batch of data. In milliseconds */
  val POLL_TIMEOUT = 1000
  /** Data topic name */
  val DATA_TOPIC = "data"
  /** Database table */
  val TABLE_NAME = "data"


  private val Log = LoggerFactory.getLogger(MainApp.getClass.getName)

  case class Params(kafkaBroker: String, keyspace: String, cassandraUrls: Seq[InetAddress],
                    cassandraPort: Int, user: String, pass: String, statsDHost: String)

  def stringArg(args: Array[String], key: String, default: String): String = {
    val name = "--" + key + "="
    args.find(_.contains(name)).map(_.substring(name.length)).getOrElse(default).trim
  }

  /** Command line parser */
  def parseArgs(args: Array[String]): Params = {
    val kafka = stringArg(args, "kafka", "localhost:9092")
    val user = stringArg(args, "user", "")
    val pass = stringArg(args, "pass", "")
    val keyspace = stringArg(args, "keyspace", "production")
    val cassandraUrls = stringArg(args, "db", "localhost").split(",").map(InetAddress.getByName)
    val cassandraPort = stringArg(args, "dbPort", "9042").toInt
    val statsDHost = stringArg(args, "statsDHost", "none")
    Params(kafka, keyspace, cassandraUrls, cassandraPort, user, pass, statsDHost)
  }

  /** Kafka configuration */
  def buildConfig(brokers: String): Properties = {
    val strDeserializer = (new StringDeserializer).getClass.getName
    val props = new Properties()
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers)
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "dumper")
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, strDeserializer)
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, strDeserializer)
    props
  }

  /** Init connection to the database */
  def initDB(params: Params): Session = {
    val builder = Cluster.builder()
      .addContactPoints(params.cassandraUrls.asJava)
      .withPort(params.cassandraPort)

    if (params.user != "" && params.pass != "") {
      builder.withCredentials(params.user, params.pass)
    }

    builder.build().connect()
  }

  /** StatsD configuration */
  def initStatsD(host: String): Option[StatsDClient] = {
    try {
      val sc: ServiceCheck = ServiceCheck.builder.withName("service.check").withStatus(ServiceCheck.Status.OK).build
      val client = new NonBlockingStatsDClient("dumper", host, 8125)
      client.serviceCheck(sc)
      Some(client)
    }
    catch {
      case e: Exception => Log.warn(e.getMessage)
        None
    }
  }

  /** Listen to Kafka topics and execute all processing pipelines */
  def main(args: Array[String]): Unit = {
    val params = parseArgs(args)
    val kafkaConsumer = new KafkaConsumer[String, String](buildConfig(params.kafkaBroker))
    val session = initDB(params)
    StatsD.init(params.statsDHost)

    Log.info("Application started")
    run(kafkaConsumer, session)
    Log.info("Application Stopped")
    session.close()
    kafkaConsumer.close()
  }

  /**
    * Predict 0 records in the next batch
    */
  def run(kafkaConsumer: KafkaConsumer[String, String], session: Session): Unit = {
    kafkaConsumer.subscribe(List(DATA_TOPIC).asJava)

    while (true) {
      val batch: ConsumerRecords[String, String] = kafkaConsumer.poll(POLL_TIMEOUT)
      val records = batch.records(DATA_TOPIC).asScala
      val v = records.size
      saveData(session, "pirx-data-count", v)
      saveData(session, "pirx-data-count-prediction", 0)
      StatsD.increment("records", v)
    }
  }

  /** Save data point to the database. */
  def saveData(session: Session, id: String, value: Float): Unit = {
    val stmt = QueryBuilder.insertInto(TABLE_NAME)
      .value("channel", id)
      .value("timestamp", LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli)
      .value("value", value)
    session.execute(stmt)
  }

}
