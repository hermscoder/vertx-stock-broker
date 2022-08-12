package com.seekperception.vertx_stock_broker.watchlist

import com.seekperception.vertx_stock_broker.assets.Asset
import io.vertx.core.json.JsonObject

data class WatchList(var assets: List<Asset>? = listOf()) {

  fun toJsonObject(): JsonObject {
    return JsonObject.mapFrom(this)
  }
}
