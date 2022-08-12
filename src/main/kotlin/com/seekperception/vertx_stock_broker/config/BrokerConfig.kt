package com.seekperception.vertx_stock_broker.config

import io.vertx.core.json.JsonObject

class BrokerConfig(val serverPort: Int, val version: String, val dbConfig: DbConfig) {

  companion object {
    fun from(jsonObject: JsonObject): BrokerConfig {
      val serverPort = jsonObject.getInteger(ConfigLoader.SERVER_PORT)
        ?: throw java.lang.RuntimeException("${ConfigLoader.SERVER_PORT} not configured")
      val version = jsonObject.getString(ConfigLoader.APPLICATION_VERSION)
        ?: throw java.lang.RuntimeException("${ConfigLoader.APPLICATION_VERSION} not configured")


      return BrokerConfig(serverPort, version, parseDbConfig(jsonObject))
    }

    fun parseDbConfig(config: JsonObject):DbConfig {
      return DbConfig(
        config.getString(ConfigLoader.DB_HOST),
        config.getInteger(ConfigLoader.DB_PORT),
        config.getString(ConfigLoader.DB_DATABASE),
        config.getString(ConfigLoader.DB_USER),
        config.getString(ConfigLoader.DB_PASSWORD))
    }
  }
}
