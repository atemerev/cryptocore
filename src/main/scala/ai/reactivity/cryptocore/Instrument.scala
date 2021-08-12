package ai.reactivity.cryptocore

/**
 * A trading instrument (e.g. a spot currency pair, or a perpetual futures contract, or
 * anything else that can be traded).
 */
trait Instrument:
  def symbol: String
