package com.seekperception.vertx_stock_broker.watchlist

import com.seekperception.vertx_stock_broker.db.DbResponse
import com.seekperception.vertx_stock_broker.quotes.GetQuoteFromDatabaseHandler
import com.seekperception.vertx_stock_broker.quotes.QuoteEntity
import io.netty.handler.codec.http.HttpHeaderValues
import io.vertx.core.Handler
import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.JsonArray
import io.vertx.ext.web.RoutingContext
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.templates.SqlTemplate
import org.slf4j.LoggerFactory

class GetWatchListFromDatabaseHandler(val db: Pool) : Handler<RoutingContext> {

  companion object {
    val LOGGER = LoggerFactory.getLogger(GetWatchListFromDatabaseHandler::class.java)
  }

  override fun handle(context: RoutingContext) {
    val accountIdPathParam = WatchListRestApi.getAccountId(context)
    LOGGER.debug("${context.normalizedPath()} for account $accountIdPathParam")

    SqlTemplate.forQuery(db,
      "SELECT w.account_id, w.asset FROM broker.watchlist w WHERE w.account_id=#{account_id}")
      .mapTo(Row::toJson)
      .execute(mapOf(Pair("account_id", accountIdPathParam)))
      .onFailure(DbResponse.errorHandler(context, "Failed to fetch watchlist for account id $accountIdPathParam!"))
      .onSuccess { assets ->
        if(!assets.iterator().hasNext()) {
          DbResponse.notFoundResponse(context, "Watchlist for account $accountIdPathParam is not available!")
          return@onSuccess
        }
        var response = JsonArray()
        assets.forEach(response::add)
        LOGGER.info("Path ${context.normalizedPath()} responds with ${response.encode()}")
        context.response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .end(response.toBuffer())
      }
  }

}
