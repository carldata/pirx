package pl.klangner.dss

import java.time.format.DateTimeFormatter
import java.time.{LocalDateTime, ZoneOffset}

import org.slf4j.LoggerFactory

/**
  * Dataset Server client
  */
object DatasetStorage {

  private val Log = LoggerFactory.getLogger(getClass.getName)

  var fileStorage: FileStorage = _

  def init(dataPath: String): Unit = {
    fileStorage = new FileStorage(dataPath)
  }

  /** Send asynchronously data point to the server */
  def saveTimeSeriesPoint(dataset: String, dataPoint: Float): Unit = {
    val nowUTC = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME)
    val data = s"""{"dataset":"$dataset", "data":{"index":"$nowUTC", "value":$dataPoint}}"""
    fileStorage.add(dataset, data)
  }
}
