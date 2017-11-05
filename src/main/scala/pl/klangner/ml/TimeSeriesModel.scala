package pl.klangner.ml

import java.time.format.DateTimeFormatter
import java.time.{LocalDateTime, ZoneOffset}

import org.slf4j.LoggerFactory

/**
  * Machine Learning model and dataset storage for Time Series
  */
class TimeSeriesModel(fileStorage: FileStorage, modelName: String) {

  private val Log = LoggerFactory.getLogger(getClass.getName)

  /** Last added sample value */
  private var lastValue: Float = 0

  /** Send asynchronously data point to the server */
  def addSample(value: Float): Unit = {
    val nowUTC = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME)
    val data = s"""{"dataset":"$modelName", "data":{"index":"$nowUTC", "value":$value}}"""
    fileStorage.add(modelName, data)
    lastValue = value
  }

  /** Predict value of the next sample */
  def predict(): Float = {
    lastValue
  }
}
