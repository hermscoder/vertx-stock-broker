package com.seekperception.vertx_stock_broker.migration

import com.seekperception.vertx_stock_broker.MainVerticle
import com.seekperception.vertx_stock_broker.config.DbConfig
import io.vertx.core.Future
import io.vertx.core.Vertx
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.MigrationInfo
import org.slf4j.LoggerFactory
import java.util.stream.Collectors

class FlywayMigration {
  companion object {
    val LOGGER = LoggerFactory.getLogger(FlywayMigration::class.java)

    fun migrate(vertx: Vertx, dbConfig: DbConfig): Future<Unit> {
      LOGGER.debug("DB config $dbConfig")
      return vertx.executeBlocking<Unit?> { promise ->
        execute(dbConfig)
        promise.complete()
      }.onFailure { error -> LOGGER.error("Failed to migrate db schema with error: $error") }
    }

    private fun execute(dbConfig: DbConfig) {
      val jdbcUrl: String = "jdbc:postgresql://%s:%d/%s".format(dbConfig.host, dbConfig.port, dbConfig.database)
      LOGGER.debug("Migrating DB schema using jdbc url: $jdbcUrl")

      val flyway = Flyway.configure()
            .dataSource(jdbcUrl, dbConfig.user, dbConfig.password)
            .schemas("broker")
            .defaultSchema("broker")
            .load()

      val current = flyway.info().current()
      if (current != null) {
            LOGGER.info("db schema is at version: $current")
      }

      val pending = flyway.info().pending()
      if (current != null) {
            LOGGER.info("Pending migrations are: ${printPendingMigrations(pending)}")
      }

      flyway.migrate()
    }

    private fun printPendingMigrations(pending: Array<MigrationInfo>): String {
      if(pending == null) {
        return "[]"
      }

      return pending.map { each -> "${each.version} - ${each.description}" }
        .stream()
        .collect(Collectors.joining(",", "[", "]"))
    }
  }
}
