package com.seekperception.vertx_stock_broker.quotes

import com.seekperception.vertx_stock_broker.MainVerticle
import com.seekperception.vertx_stock_broker.assets.GetAssetsFromDatabaseHandler
import com.seekperception.vertx_stock_broker.db.DbResponse
import io.netty.handler.codec.http.HttpHeaderValues
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.Handler
import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.templates.SqlTemplate
import org.slf4j.LoggerFactory

class GetQuoteFromDatabaseHandler(val db: Pool) : Handler<RoutingContext> {

  companion object {
    val LOGGER = LoggerFactory.getLogger(GetQuoteFromDatabaseHandler::class.java)
  }

  override fun handle(context: RoutingContext) {
    val assetPathParam = context.pathParam("asset")
    LOGGER.debug("Asset parameter: $assetPathParam")

    SqlTemplate.forQuery(db,
      "SELECT q.asset, q.bid, q.ask, q.last_price, q.volume FROM broker.quotes q WHERE q.asset=#{asset}")
      .mapTo(QuoteEntity::class.java)
      .execute(mapOf(Pair("asset", assetPathParam)))
      .onFailure(DbResponse.errorHandler(context, "Failed to get quote for $assetPathParam from database!"))
      .onSuccess { quotes ->
        if(!quotes.iterator().hasNext()) {
          DbResponse.notFoundResponse(context, "Quote for asset $assetPathParam not available!")
          return@onSuccess
        }
        var response = quotes.iterator().next().toJsonObject()
        LOGGER.info("Path ${context.normalizedPath()} responds with $response")
        context.response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .end(response.toBuffer())
      }
  }

}
