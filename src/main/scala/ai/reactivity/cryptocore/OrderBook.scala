package ai.reactivity.cryptocore

class OrderBook private(val party: Party, val instrument: Instrument)

object OrderBook {
  class Aggregate private(val price: Decimal, val side: Side, val totalQty: Decimal, entries: Map[String, Order]):
    lazy val orders: Iterable[Order] = entries.values
    lazy val collapsed: Option[Order] = if (totalQty.toDouble == 0.0) None else None
}
