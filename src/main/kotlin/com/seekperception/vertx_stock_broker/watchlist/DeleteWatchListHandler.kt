package com.seekperception.vertx_stock_broker.watchlist

import com.seekperception.vertx_stock_broker.quotes.GetQuoteHandler
import io.netty.handler.codec.http.HttpHeaderValues
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.Handler
import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import org.slf4j.LoggerFactory
import java.util.*

class DeleteWatchListHandler(val watchListPerAccount: MutableMap<UUID, WatchList>): Handler<RoutingContext> {
  companion object {
    val LOGGER = LoggerFactory.getLogger(DeleteWatchListHandler::class.java)
  }
  override fun handle(context: RoutingContext) {
    val accountIdPathParam = WatchListRestApi.getAccountId(context)
    val removedFromWatchList = watchListPerAccount.remove(UUID.fromString(accountIdPathParam))
    if(removedFromWatchList == null) {
      context.response()
        .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
        .end(
          JsonObject()
            .put("message", "Watchlist for account $accountIdPathParam not found!")
            .put("path", context.normalizedPath())
            .toBuffer())
      return
    }
    context.response()
      .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
      .end(removedFromWatchList.toJsonObject().toBuffer())
  }
}
