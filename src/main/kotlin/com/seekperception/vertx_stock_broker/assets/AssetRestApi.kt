package com.seekperception.vertx_stock_broker.assets

import io.vertx.ext.web.Router
import io.vertx.sqlclient.Pool

object AssetRestApi {

  val ASSETS = arrayOf<Asset>(
    Asset("AAPL"),
    Asset("AMZN"),
    Asset("NFLX"),
    Asset("TSLA"),
    Asset("FB"),
    Asset("GOOGL"),
    Asset("MSFT")
  )

  fun attach(parent: Router, db: Pool) {
      parent.get("/assets").handler(GetAssetsHandler())
      parent.get("/pg/assets").handler(GetAssetsFromDatabaseHandler(db))
    }
}
