package com.seekperception.vertx_stock_broker.quotes

import com.seekperception.vertx_stock_broker.MainVerticle
import com.seekperception.vertx_stock_broker.assets.Asset
import com.seekperception.vertx_stock_broker.assets.AssetRestApi
import com.seekperception.vertx_stock_broker.db.DbResponse
import io.netty.handler.codec.http.HttpHeaderValues
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.Handler
import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import org.slf4j.LoggerFactory

class GetQuoteHandler(val cachedQuotes: Map<String, Quote>): Handler<RoutingContext> {

  companion object {
    val LOGGER = LoggerFactory.getLogger(GetQuoteHandler::class.java)
  }

  override fun handle(context: RoutingContext) {
    val assetPathParam = context.pathParam("asset")
    QuotesRestApi.LOGGER.debug("Asset parameter: $assetPathParam")

    val quote: Quote? = cachedQuotes.get(assetPathParam)
    if(quote == null) {
      DbResponse.notFoundResponse(context,"Quote for asset $assetPathParam not available!")
      return
    }
    val response = quote.toJsonObject()
    MainVerticle.LOGGER.info("Path ${context.normalizedPath()} responds with $response")
    context.response().end(response.toBuffer())
  }
}
