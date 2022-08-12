package com.seekperception.vertx_stock_broker.quotes

import com.seekperception.vertx_stock_broker.assets.Asset
import io.vertx.core.json.JsonObject
import java.math.BigDecimal

class Quote(val asset: Asset, val bid: BigDecimal, val ask: BigDecimal, val lastPrice: BigDecimal, val volume: BigDecimal) {

  fun toJsonObject(): JsonObject {
    return JsonObject.mapFrom(this)
  }
}
