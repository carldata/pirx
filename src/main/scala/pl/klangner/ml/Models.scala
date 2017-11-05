package pl.klangner.ml

/**
  * Create Machine Learning models.
  * Those models can be used to predict values and build datasets
  */
object Models {

  var fileStorage: FileStorage = _

  def init(dataPath: String): Unit = {
    fileStorage = new FileStorage(dataPath)
  }

  /** Send asynchronously data point to the server */
  def createTimeSeriesModel(name: String): TimeSeriesModel = new TimeSeriesModel(fileStorage, name)
}
