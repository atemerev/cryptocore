package ai.reactivity.cryptocore.market

import ai.reactivity.cryptocore.instrument.Instrument

case class OrderKey(party: Party, instrument: Instrument, side: Side, id: String, meta: Option[String] = None) {
  override def toString: String = {
    val idString = id.substring(0, math.min(3, id.length))
    val suffix = if (id.length > 3) ".." + id.takeRight(math.min(3, id.length - 3)) else ""
    "%s %s %s (%s)".format(party, instrument, side, idString + suffix)
  }
}
