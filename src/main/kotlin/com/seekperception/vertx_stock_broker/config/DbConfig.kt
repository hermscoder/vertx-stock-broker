package com.seekperception.vertx_stock_broker.config

data class DbConfig(val host: String = "localhost",
               val port: Int = 5432,
               val database: String = "vertx-stock-broker",
               val user:String = "postgres",
               val password: String = "secret") {
  override fun toString(): String {
    return "DbConfig(host='$host', port=$port, database='$database', user='$user', password='****')"
  }
}
