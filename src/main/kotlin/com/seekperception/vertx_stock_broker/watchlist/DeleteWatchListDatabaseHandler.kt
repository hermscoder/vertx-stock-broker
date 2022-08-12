package com.seekperception.vertx_stock_broker.watchlist

import com.seekperception.vertx_stock_broker.db.DbResponse
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.Handler
import io.vertx.ext.web.RoutingContext
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.templates.SqlTemplate
import org.slf4j.LoggerFactory
import java.util.stream.Collectors

class DeleteWatchListDatabaseHandler(val db: Pool) : Handler<RoutingContext> {

  companion object {
    val LOGGER = LoggerFactory.getLogger(DeleteWatchListDatabaseHandler::class.java)
  }

  override fun handle(context: RoutingContext) {
    val accountIdPathParam = WatchListRestApi.getAccountId(context)
    LOGGER.debug("${context.normalizedPath()} for account $accountIdPathParam")
    val jsonObject = context.body().asJsonObject()
    var watchList = jsonObject.mapTo(WatchList::class.java)

    val parameterBatch = watchList.assets?.stream()?.map { asset ->
      val parameters = mutableMapOf<String, Any>()
      parameters.put("account_id", accountIdPathParam)
      parameters.put("asset", asset.name)
      parameters
    }?.collect(Collectors.toList())
    //Only adding is possible
    SqlTemplate.forUpdate(db,
      "INSERT INTO broker.watchlist VALUES (#{account_id}, #{asset})" +
        "ON CONFLICT (account_id, asset) DO NOTHING")
      .executeBatch(parameterBatch)
      .onFailure(DbResponse.errorHandler(context, "Failed to insert into watchlist for account id $accountIdPathParam!"))
      .onSuccess { result ->
        context.response()
          .setStatusCode(HttpResponseStatus.NO_CONTENT.code())
          .end()
      }
  }
}
