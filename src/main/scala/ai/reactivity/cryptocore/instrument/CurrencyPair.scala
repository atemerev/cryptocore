package ai.reactivity.cryptocore.instrument

case class CurrencyPair(base: Currency, quote: Currency) extends Instrument {
  override val symbol: String = s"$base/$quote"

  override def toString: String = symbol
}