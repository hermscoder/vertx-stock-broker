package com.seekperception.vertx_stock_broker.assets

import io.netty.handler.codec.http.HttpHeaderValues
import io.vertx.core.Handler
import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.JsonArray
import io.vertx.ext.web.RoutingContext
import org.slf4j.LoggerFactory

class GetAssetsHandler: Handler<RoutingContext> {
  companion object {
    val LOGGER = LoggerFactory.getLogger(GetAssetsHandler::class.java)
  }

  override fun handle(context: RoutingContext) {
    val response = JsonArray()
    AssetRestApi.ASSETS.forEach { response.add(it) }
    LOGGER.info("Path ${context.normalizedPath()} responds with $response")
    context.response()
      .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
      .putHeader("my-header", "my-value")
      .end(response.toBuffer())
  }
}
