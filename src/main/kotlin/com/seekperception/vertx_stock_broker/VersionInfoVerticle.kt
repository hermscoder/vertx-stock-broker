package com.seekperception.vertx_stock_broker

import com.seekperception.vertx_stock_broker.config.ConfigLoader
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import org.slf4j.LoggerFactory

class VersionInfoVerticle: AbstractVerticle() {
  companion object {
    val LOGGER = LoggerFactory.getLogger(VersionInfoVerticle::class.java)
  }

  override fun start(startPromise: Promise<Void>) {
    ConfigLoader.load(vertx)
      .onFailure(startPromise::fail)
      .onSuccess { configuration ->
        LOGGER.info("Current Application Version is ${configuration.version}")
        startPromise.complete()
      }
  }
}
