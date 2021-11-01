package ai.reactivity.cryptocore.market
import scala.collection.immutable.ListMap

import ai.reactivity.cryptocore.Decimal
import ai.reactivity.cryptocore.instrument.Instrument
import scala.language.implicitConversions

class OrderBook private(val party: Party, val instrument: Instrument)

object OrderBook {
  class Aggregate private(val price: Decimal, val side: Side, val totalQty: Decimal, entries: ListMap[OrderKey, Order]):
    lazy val orders: Iterable[Order] = entries.values

    def +(newEntry: Order): Aggregate =
      require(this.price == newEntry.price)
      ???
}
