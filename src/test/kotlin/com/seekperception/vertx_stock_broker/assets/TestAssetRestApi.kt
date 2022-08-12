package com.seekperception.vertx_stock_broker.assets

import com.seekperception.vertx_stock_broker.MainVerticle
import com.seekperception.vertx_stock_broker.RestApiTest
import io.netty.handler.codec.http.HttpHeaderValues
import io.vertx.core.Vertx
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.WebClientOptions
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.LoggerFactory



@ExtendWith(VertxExtension::class)
class TestAssetRestApi: RestApiTest() {

  companion object {
    val LOGGER = LoggerFactory.getLogger(MainVerticle::class.java)
  }

  @Test
  fun returns_all_assets(vertx: Vertx, testContext: VertxTestContext) {
    val client = WebClient.create(vertx, WebClientOptions().setDefaultPort(TEST_SERVER_PORT))
    client.get("/assets")
      .send()
      .onComplete(testContext.succeeding { response ->
        val json = response.bodyAsJsonArray()
        LOGGER.info("Response: $json")
        assertEquals("""[{"name":"AAPL"},{"name":"AMZN"},{"name":"NFLX"},{"name":"TSLA"},{"name":"FB"},{"name":"GOOGL"},{"name":"MSFT"}]""", json.encode())
        assertEquals(200, response.statusCode())
        assertEquals(HttpHeaderValues.APPLICATION_JSON.toString(), response.getHeader(io.vertx.core.http.HttpHeaders.CONTENT_TYPE.toString()))
        assertEquals("my-value", response.getHeader("my-header"))
        testContext.completeNow()
      })
  }
}
