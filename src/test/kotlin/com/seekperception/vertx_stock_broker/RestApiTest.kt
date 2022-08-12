package com.seekperception.vertx_stock_broker

import com.seekperception.vertx_stock_broker.config.ConfigLoader
import io.vertx.core.Vertx
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.BeforeEach
import org.slf4j.LoggerFactory


abstract class RestApiTest {
  companion object {
    val LOGGER = LoggerFactory.getLogger(RestApiTest::class.java)
    const val TEST_SERVER_PORT = 9000
  }
  @BeforeEach
  fun deploy_verticle(vertx: Vertx, testContext: VertxTestContext) {
    System.setProperty("${ConfigLoader.SERVER_PORT}", "$TEST_SERVER_PORT")
    System.setProperty("${ConfigLoader.DB_HOST}", "localhost")
    System.setProperty("${ConfigLoader.DB_PORT}", "5432")
    System.setProperty("${ConfigLoader.DB_DATABASE}", "vertx-stock-broker")
    System.setProperty("${ConfigLoader.DB_USER}", "postgres")
    System.setProperty("${ConfigLoader.DB_PASSWORD}", "secret")
    LOGGER.warn("!!! Tests ar using local database !!!")
    vertx.deployVerticle(MainVerticle(), testContext.succeeding<String> { testContext.completeNow() })
  }
}
