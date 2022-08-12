package com.seekperception.vertx_stock_broker.db

import com.seekperception.vertx_stock_broker.assets.GetAssetsFromDatabaseHandler
import io.netty.handler.codec.http.HttpHeaderValues
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.Handler
import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import org.slf4j.LoggerFactory

class DbResponse {
  companion object {
    val LOGGER = LoggerFactory.getLogger(DbResponse::class.java)

    fun errorHandler(context: RoutingContext, errorMsg: String): Handler<Throwable> {
      return Handler<Throwable> { error ->
        LOGGER.error("Failure: ${error}")
        context.response()
          .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .end(
            JsonObject()
              .put("message", errorMsg)
              .put("path", context.normalizedPath())
              .toBuffer()
          )}
    }

    fun notFoundResponse(context: RoutingContext, errorMsg: String) {
      context.response()
        .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
        .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
        .end(
          JsonObject()
            .put("message", errorMsg)
            .put("path", context.normalizedPath())
            .toBuffer()
        )
    }
  }



}
