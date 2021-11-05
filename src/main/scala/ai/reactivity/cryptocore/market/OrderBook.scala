package ai.reactivity.cryptocore.market
import scala.collection.immutable.ListMap
import ai.reactivity.cryptocore.Decimal
import ai.reactivity.cryptocore.instrument.Instrument
import ai.reactivity.cryptocore.market.OrderBook.Aggregate

import scala.collection.SortedMap
import scala.language.implicitConversions

class OrderBook private(val instrument: Instrument,
                        val bids: SortedMap[Decimal, Aggregate],
                        val offers: SortedMap[Decimal, Aggregate],
                        val byKey: Map[OrderKey, Order])

object OrderBook {
  case class Aggregate private(price: Decimal, side: Side, totalQty: Decimal, entries: ListMap[OrderKey, Order]) {
    lazy val orders: Iterable[Order] = entries.values

    def +(newEntry: Order): Aggregate =

      require(this.price == newEntry.price)
      require(this.side == newEntry.key.side)

      val newTotal = entries.get(newEntry.key) match {
        case Some(existing) => totalQty - existing.quantity + newEntry.quantity
        case None => totalQty + newEntry.quantity
      }
      Aggregate(price, side, newTotal, entries + (newEntry.key -> newEntry))

    def -(orderKey: OrderKey): Aggregate =
      val newEntries = entries - orderKey
      if (newEntries.isEmpty) Aggregate(price, side, 0, ListMap.empty) else {
        val newQty = entries.get(orderKey) match {
          case Some(existing) => totalQty - existing.quantity
          case None => totalQty
        }
        Aggregate(price, side, newQty, newEntries)
      }

    def isEmpty: Boolean = entries.isEmpty

    def size: Int = entries.size

    override def toString: String = "(" + entries.values.map(_.quantity).mkString(",") + ")"

    override def equals(obj: scala.Any): Boolean = obj match {
      case that: Aggregate => this.orders equals that.orders
      case _ => false
    }

    override def hashCode(): Int = this.entries.values.hashCode()
  }

}
