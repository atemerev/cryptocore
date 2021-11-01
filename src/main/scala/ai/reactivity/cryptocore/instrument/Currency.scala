package ai.reactivity.cryptocore.instrument

/**
 * A representation of currency, fiat or crypto. E.g. USD for US Dollar, or ETH for Ethereum.
 * Currencies using the same symbol are considered to be identical.
 */
trait Currency extends Asset {
  def /(quote: Currency) = CurrencyPair(this, quote)
}
