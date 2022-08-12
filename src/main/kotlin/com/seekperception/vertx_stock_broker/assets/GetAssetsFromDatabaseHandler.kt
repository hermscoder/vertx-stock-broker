package com.seekperception.vertx_stock_broker.assets

import com.seekperception.vertx_stock_broker.db.DbResponse
import io.netty.handler.codec.http.HttpHeaderValues
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.Handler
import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import io.vertx.sqlclient.Pool
import org.slf4j.LoggerFactory

class GetAssetsFromDatabaseHandler(val db: Pool): Handler<RoutingContext> {
  companion object {
    val LOGGER = LoggerFactory.getLogger(GetAssetsFromDatabaseHandler::class.java)
  }

  override fun handle(context: RoutingContext) {
    db.query("SELECT a.value FROM broker.assets a").execute()
      .onFailure(DbResponse.errorHandler(context, "Failed to get assets from database!"))
      .onSuccess { result ->
        val response = JsonArray()
        result.forEach { asset -> response.add(asset.getValue("value"))}

        LOGGER.info("Path ${context.normalizedPath()} responds with ${response.encode()}")
        context.response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .putHeader("my-header", "my-value")
          .end(response.toBuffer())
      }
  }


}
