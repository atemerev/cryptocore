package ai.reactivity.cryptocore.instrument

import ai.reactivity.cryptocore.{Crypto, Fiat}

object Symbology {
  lazy val BTC = Crypto("BTC")
  lazy val ETH = Crypto("ETH")
  lazy val USD = Fiat("USD")
  lazy val USDT = Stablecoin("USDT")
}
