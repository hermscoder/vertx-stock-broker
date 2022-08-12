package com.seekperception.vertx_stock_broker.watchlist

import com.seekperception.vertx_stock_broker.MainVerticle
import com.seekperception.vertx_stock_broker.RestApiTest
import com.seekperception.vertx_stock_broker.assets.Asset
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.WebClientOptions
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.LoggerFactory
import java.nio.Buffer
import java.util.UUID

@ExtendWith(VertxExtension::class)
class TestWatchListRestApi: RestApiTest() {

  companion object {
    val LOGGER = LoggerFactory.getLogger(TestWatchListRestApi::class.java)
  }

  @Test
  fun add_watchlist_for_account(vertx: Vertx, testContext: VertxTestContext) {
    val client = createWebClient(vertx)
    val accountId = UUID.randomUUID()
    client.put("${WatchListRestApi.PATH}/${accountId}")
      .sendJsonObject(body())
      .onComplete(testContext.succeeding { response ->
        val json = response.bodyAsJsonObject()
        LOGGER.info("Response: $json")
        Assertions.assertEquals("""{"assets":[{"name":"AMZN"},{"name":"TSLA"}]}""", json.encode())
        Assertions.assertEquals(200, response.statusCode())
        testContext.completeNow()
      })
  }

  private fun body() = WatchList(listOf(Asset("AMZN"), Asset("TSLA"))).toJsonObject()

  @Test
  fun returns_watchlist_for_account(vertx: Vertx, testContext: VertxTestContext) {
    val client = createWebClient(vertx)
    val accountId = UUID.randomUUID()
    client.get("${WatchListRestApi.PATH}/${accountId}")
      .send()
      .onComplete(testContext.succeeding { response ->
        val json = response.bodyAsJsonObject()
        LOGGER.info("Response: $json")
        Assertions.assertEquals("""{"message":"Watchlist for account $accountId not found!","path":"/account/watchlist/$accountId"}""", json.encode())
        Assertions.assertEquals(404, response.statusCode())
        testContext.completeNow()
      })
  }

  @Test
  fun adds_and_returns_watchlist_for_account(vertx: Vertx, testContext: VertxTestContext) {
    val client = createWebClient(vertx)
    val accountId = UUID.randomUUID()
    client.put("${WatchListRestApi.PATH}/${accountId}")
      .sendJsonObject(body())
      .onComplete(testContext.succeeding { response ->
        val json = response.bodyAsJsonObject()
        LOGGER.info("Response PUT: $json")
        Assertions.assertEquals("""{"assets":[{"name":"AMZN"},{"name":"TSLA"}]}""", json.encode())
        Assertions.assertEquals(200, response.statusCode())
      }).compose { next ->
        client.get("${WatchListRestApi.PATH}/${accountId}")
          .send()
          .onComplete(testContext.succeeding { response ->
            val json = response.bodyAsJsonObject()
            LOGGER.info("Response GET: $json")
            Assertions.assertEquals("""{"assets":[{"name":"AMZN"},{"name":"TSLA"}]}""", json.encode())
            Assertions.assertEquals(200, response.statusCode())
            testContext.completeNow()
          })
        Future.succeededFuture<Buffer>()
      }
  }

  @Test
  fun adds_deletes_and_return_watchlist_for_account(vertx: Vertx, testContext: VertxTestContext) {
    val client = createWebClient(vertx)
    val accountId = UUID.randomUUID()
    client.put("${WatchListRestApi.PATH}/${accountId}")
      .sendJsonObject(body())
      .onComplete(testContext.succeeding { response ->
        val json = response.bodyAsJsonObject()
        LOGGER.info("Response PUT: $json")
        Assertions.assertEquals("""{"assets":[{"name":"AMZN"},{"name":"TSLA"}]}""", json.encode())
        Assertions.assertEquals(200, response.statusCode())
      }).compose { next ->
        client.delete("${WatchListRestApi.PATH}/${accountId}")
          .send()
          .onComplete(testContext.succeeding { response ->
            val json = response.bodyAsJsonObject()
            LOGGER.info("Response DELETE: $json")
            Assertions.assertEquals("""{"assets":[{"name":"AMZN"},{"name":"TSLA"}]}""", json.encode())
            Assertions.assertEquals(200, response.statusCode())

          })
        Future.succeededFuture<Buffer>()
      }.compose {
        client.get("${WatchListRestApi.PATH}/${accountId}")
          .send()
          .onComplete(testContext.succeeding { response ->
            val json = response.bodyAsJsonObject()
            LOGGER.info("Response GET: $json")
            Assertions.assertEquals("""{"message":"Watchlist for account $accountId not found!","path":"/account/watchlist/$accountId"}""", json.encode())
            Assertions.assertEquals(404, response.statusCode())
            testContext.completeNow()
          })
        Future.succeededFuture<Buffer>()
      }
  }

  private fun createWebClient(vertx: Vertx) =
    WebClient.create(vertx, WebClientOptions().setDefaultPort(TEST_SERVER_PORT))
}
