package com.seekperception.vertx_stock_broker.quotes

import com.fasterxml.jackson.annotation.JsonProperty
import io.vertx.core.json.JsonObject
import java.math.BigDecimal

data class QuoteEntity(@JsonProperty("asset") val asset: String,
                       @JsonProperty("bid") val bid: BigDecimal,
                       @JsonProperty("ask") val ask: BigDecimal,
                       @JsonProperty("last_price") val lastPrice: BigDecimal,
                       @JsonProperty("volume") val volume: BigDecimal) {

  fun toJsonObject(): JsonObject {
    return JsonObject.mapFrom(this)
  }
}
