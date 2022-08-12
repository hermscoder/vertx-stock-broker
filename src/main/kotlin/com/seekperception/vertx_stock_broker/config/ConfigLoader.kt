package com.seekperception.vertx_stock_broker.config

import com.seekperception.vertx_stock_broker.watchlist.WatchListRestApi
import io.vertx.config.ConfigRetriever
import io.vertx.config.ConfigRetrieverOptions
import io.vertx.config.ConfigStoreOptions
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import org.slf4j.LoggerFactory


private const val CONFIGURATION_FILE = "application.yml"

object ConfigLoader {
  val LOGGER = LoggerFactory.getLogger(ConfigLoader::class.java)
  const val SERVER_PORT = "SERVER_PORT"
  const val APPLICATION_VERSION = "version"
  const val DB_HOST = "DB_HOST"
  const val DB_PORT = "DB_PORT"
  const val DB_DATABASE = "DB_DATABASE"
  const val DB_USER = "DB_USER"
  const val DB_PASSWORD = "DB_PASSWORD"
  val EXPOSED_ENVIRONMENT_VARIABLES = arrayOf<String>(SERVER_PORT, DB_HOST, DB_PORT, DB_DATABASE, DB_USER, DB_PASSWORD)

  fun load(vertx: Vertx):Future<BrokerConfig> {
    var exposedKeys = JsonArray()
    EXPOSED_ENVIRONMENT_VARIABLES.forEach { exposedKeys.add(it) }
    LOGGER.debug("Fetch configuration for ${exposedKeys.encode()}")
    val envStore = ConfigStoreOptions().setType("env").setConfig(JsonObject().put("keys", exposedKeys))

    val propertyStore = ConfigStoreOptions().setType("sys").setConfig(JsonObject().put("cache", false))

    val yamlStore = ConfigStoreOptions().setType("file").setFormat("yaml").setConfig(JsonObject().put("path",
      CONFIGURATION_FILE
    ))
    val retriever = ConfigRetriever.create(vertx,
      ConfigRetrieverOptions()
        .addStore(yamlStore)
        .addStore(propertyStore)   // Order of the stores matter. If SERVER_PORT is in envStore and in propertyStore,
        .addStore(envStore)        //the last one will override the other ones
    )
    return retriever.config.map(BrokerConfig::from)
  }
}
