package ai.reactivity.cryptocore.instrument

import ai.reactivity.cryptocore.Currency

/**
 * A fiat currency, e.g. US Dollar or Euro.
 *
 * @param symbol Accepted currency symbol, e.g. "USD" or "JPY".
 */
case class Fiat(symbol: String) extends Currency
