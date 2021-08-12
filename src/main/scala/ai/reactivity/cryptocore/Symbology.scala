package ai.reactivity.cryptocore

object Symbology {
  lazy val BTC = Crypto("BTC")
  lazy val ETH = Crypto("ETH")
  lazy val USD = Fiat("USD")
  lazy val USDT = Stablecoin("USDT")
}
