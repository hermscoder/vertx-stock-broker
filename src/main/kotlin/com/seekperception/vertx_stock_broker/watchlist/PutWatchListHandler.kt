package com.seekperception.vertx_stock_broker.watchlist

import com.seekperception.vertx_stock_broker.quotes.GetQuoteHandler
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import org.slf4j.LoggerFactory
import java.util.*

class PutWatchListHandler(val watchListPerAccount: MutableMap<UUID, WatchList>): Handler<RoutingContext> {
  companion object {
    val LOGGER = LoggerFactory.getLogger(PutWatchListHandler::class.java)
  }
  override fun handle(context: RoutingContext) {
    val accountIdPathParam = WatchListRestApi.getAccountId(context)
    LOGGER.debug("${context.normalizedPath()} for account $accountIdPathParam")
    val jsonObject = context.body().asJsonObject()
    val watchList = jsonObject.mapTo(WatchList::class.java)
    watchListPerAccount[UUID.fromString(accountIdPathParam)] = watchList
    context.response().end(jsonObject.toBuffer())
  }
}
