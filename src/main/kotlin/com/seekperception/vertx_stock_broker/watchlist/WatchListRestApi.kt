package com.seekperception.vertx_stock_broker.watchlist

import com.seekperception.vertx_stock_broker.quotes.QuotesRestApi
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.pgclient.PgPool
import org.slf4j.LoggerFactory
import java.util.UUID

object WatchListRestApi {
  val LOGGER = LoggerFactory.getLogger(WatchListRestApi::class.java)
  val PATH = "/account/watchlist"
  private val PATH_WITH_PATH_VARIABLE = "$PATH/:accountId"

  private val watchListPerAccount = mutableMapOf<UUID, WatchList>()

  fun attach(parent: Router, db: PgPool) {
    parent.get(PATH_WITH_PATH_VARIABLE).handler(GetWatchListHandler(watchListPerAccount))
    parent.put(PATH_WITH_PATH_VARIABLE).handler(PutWatchListHandler(watchListPerAccount))
    parent.delete(PATH_WITH_PATH_VARIABLE).handler(DeleteWatchListHandler(watchListPerAccount))

    val pgPath = "/pg$PATH_WITH_PATH_VARIABLE"
    parent.get(pgPath).handler(GetWatchListFromDatabaseHandler(db))
    parent.put(pgPath).handler(PutWatchListDatabaseHandler(db))
    parent.delete(pgPath).handler(DeleteWatchListDatabaseHandler(db))
  }

  fun getAccountId(context: RoutingContext): String {
    val accountIdPathParam = context.pathParam("accountId")
    QuotesRestApi.LOGGER.debug("${context.normalizedPath()}  for ${context.pathParam("accountId")}: $accountIdPathParam")
    return accountIdPathParam
  }
}
