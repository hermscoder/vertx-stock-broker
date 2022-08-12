package com.seekperception.vertx_stock_broker.quotes

import com.seekperception.vertx_stock_broker.assets.Asset
import com.seekperception.vertx_stock_broker.assets.AssetRestApi
import io.vertx.ext.web.Router
import io.vertx.sqlclient.Pool
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.util.concurrent.ThreadLocalRandom

object QuotesRestApi {

  val LOGGER = LoggerFactory.getLogger(QuotesRestApi::class.java)

  fun attach(parent: Router, db: Pool) {
    val cachedQuotes = mutableMapOf<String, Quote>()
    AssetRestApi.ASSETS.forEach { symbol ->
      cachedQuotes[symbol.name] = initRandomQuote(symbol.name)
    }

    parent.get("/quotes/:asset").handler(GetQuoteHandler(cachedQuotes))
    parent.get("/pg/quotes/:asset").handler(GetQuoteFromDatabaseHandler(db))
  }


  private fun initRandomQuote(assetPathParam: String): Quote {
    return Quote(Asset(assetPathParam), randomValue(), randomValue(), randomValue(), randomValue())
  }
  private fun randomValue(): BigDecimal {
    return BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(1.0, 100.0))
  }
}
