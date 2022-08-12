package com.seekperception.vertx_stock_broker.watchlist

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import org.slf4j.LoggerFactory
import java.util.*

class GetWatchListHandler(val watchListPerAccount: Map<UUID, WatchList>): Handler<RoutingContext> {
  companion object {
    val LOGGER = LoggerFactory.getLogger(GetWatchListHandler::class.java)
  }
  override fun handle(context: RoutingContext) {
    val accountIdPathParam = WatchListRestApi.getAccountId(context)
    LOGGER.debug("${context.normalizedPath()} for account $accountIdPathParam")
    val watchList = watchListPerAccount[UUID.fromString(accountIdPathParam)]
    if(watchList == null) {
      context.response()
        .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
        .end(
          JsonObject()
            .put("message", "Watchlist for account $accountIdPathParam not found!")
            .put("path", context.normalizedPath())
            .toBuffer())
      return
    }
    context.response().end(watchList.toJsonObject().toBuffer())
  }
}
