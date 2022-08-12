package com.seekperception.vertx_stock_broker.quotes

import com.seekperception.vertx_stock_broker.MainVerticle
import com.seekperception.vertx_stock_broker.RestApiTest
import io.vertx.core.Vertx
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.WebClientOptions
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.LoggerFactory

@ExtendWith(VertxExtension::class)
class TestQuoteRestApi: RestApiTest() {

  companion object {
    val LOGGER = LoggerFactory.getLogger(MainVerticle::class.java)
  }

  @Test
  fun returns_quote_for_assets(vertx: Vertx, testContext: VertxTestContext) {
    val client = createWebClient(vertx)
    client.get("/quotes/AMZN")
      .send()
      .onComplete(testContext.succeeding { response ->
        val json = response.bodyAsJsonObject()
        LOGGER.info("Response: $json")
        Assertions.assertEquals("""{"name":"AMZN"}""", json.getJsonObject("asset").encode())
        Assertions.assertEquals(200, response.statusCode())
        testContext.completeNow()
      })
  }


  @Test
  fun returns_not_found_for_unknown_asset(vertx: Vertx, testContext: VertxTestContext) {
    val client = createWebClient(vertx)
    client.get("/quotes/UNKNOWN")
      .send()
      .onComplete(testContext.succeeding { response ->
        val json = response.bodyAsJsonObject()
        LOGGER.info("Response: $json")
        Assertions.assertEquals("""{"message":"Quote for asset UNKNOWN not available!","path":"/quotes/UNKNOWN"}""", json.encode())
        Assertions.assertEquals(404, response.statusCode())
        testContext.completeNow()
      })
  }

  private fun createWebClient(vertx: Vertx) =
    WebClient.create(vertx, WebClientOptions().setDefaultPort(TEST_SERVER_PORT))
}
