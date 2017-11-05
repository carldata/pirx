package pl.klangner.dss

import java.time.format.DateTimeFormatter
import java.time.{LocalDateTime, ZoneOffset}

import org.slf4j.LoggerFactory

/**
  * Dataset Server client
  */
object DssClient {

  private val Log = LoggerFactory.getLogger(DssClient.getClass.getName)

  /** Initialize dataset server connection */
  def init(host: String): Unit = {

  }

  /** Send asynchronously data point to the server */
  def sendTimeSeriesPoint(dataset: String, dataPoint: Float): Unit = {
    val nowUTC = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME)
    val data = s"""{"dataset":"$dataset", "data":{"index":"$nowUTC", "value":$dataPoint}}"""
    Log.info(data)
  }
}
