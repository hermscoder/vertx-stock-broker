package com.seekperception.vertx_stock_broker

import com.seekperception.vertx_stock_broker.config.ConfigLoader
import com.seekperception.vertx_stock_broker.migration.FlywayMigration
import io.vertx.core.*
import org.slf4j.LoggerFactory


class MainVerticle : AbstractVerticle() {
  companion object {
    val LOGGER = LoggerFactory.getLogger(MainVerticle::class.java)
    val PORT = 8888
    @JvmStatic
    fun main(args: Array<String>) {
      var vertx = Vertx.vertx()
      vertx.exceptionHandler { error -> LOGGER.error("Unhandled: $error"); error.printStackTrace()}
      vertx.deployVerticle(MainVerticle())
        .onFailure { err -> LOGGER.error("Failed to deploy: $err"); err.printStackTrace() }
        .onSuccess { id -> LOGGER.info("Deployed ${javaClass.enclosingClass.simpleName} with id $id") }
    }
  }
  override fun start(startPromise: Promise<Void>) {
    vertx.deployVerticle(VersionInfoVerticle::class.java.name)
      .onFailure(startPromise::fail)
      .onSuccess { id -> LOGGER.info("Deployed ${VersionInfoVerticle::class.java.name} with id $id") }
      .compose { migrateDatabase() }
      .onFailure { err -> LOGGER.error("Failed to migrate: $err"); startPromise.fail(err)}
      .onSuccess { id -> LOGGER.info("Migrated DB schema to latest version!") }
      .compose { deployRestApiVerticle(startPromise) }

  }

  private fun migrateDatabase(): Future<Unit> {
    return ConfigLoader.load(vertx)
      .compose { config ->
        FlywayMigration.migrate(vertx, config.dbConfig)
      }
  }

  private fun deployRestApiVerticle(startPromise: Promise<Void>) =
    vertx.deployVerticle(
      RestApiVerticle::class.java.name,
      DeploymentOptions().setInstances(processors())
    )
      .onFailure(startPromise::fail)
      .onSuccess { id ->
        LOGGER.info("Deployed ${RestApiVerticle::class.java.name} with id $id")
        startPromise.complete()
      }

  private fun processors() = 1.coerceAtLeast(Runtime.getRuntime().availableProcessors()/2)

}
