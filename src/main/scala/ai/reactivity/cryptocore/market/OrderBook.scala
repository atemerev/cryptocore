package ai.reactivity.cryptocore.market

import scala.collection.immutable.ListMap
import ai.reactivity.cryptocore.Decimal
import ai.reactivity.cryptocore.instrument.Instrument
import ai.reactivity.cryptocore.market.OrderBook.Aggregate

import scala.collection.immutable.SortedMap
import scala.language.implicitConversions
import scala.math.Ordering.Double.TotalOrdering

class OrderBook private(val instrument: Instrument,
                        val bids: SortedMap[Decimal, Aggregate],
                        val offers: SortedMap[Decimal, Aggregate],
                        val byKey: Map[OrderKey, Order]) {

  def add(order: Order): OrderBook = {
    require(instrument == order.key.instrument, "Order instrument should match order book instrument")
    val line = if (order.key.side == Side.Bid) bids else offers
    byKey.get(order.key) match {
      // order with same key exists, need to be replaced
      case Some(existingOrder) =>
        val oldPrice = existingOrder.price
        val newPrice = order.price
        val removedOldPrice = line(oldPrice) - existingOrder.key
        val addedNewPrice = if (line.contains(newPrice)) line(newPrice) + order else Aggregate(order.price, order.key.side, order)
        val tmpLine = if (removedOldPrice.isEmpty) line - oldPrice else line + (oldPrice -> removedOldPrice)
        val newLine = tmpLine + (newPrice -> addedNewPrice)
        val newByKey = byKey + (order.key -> order)
        if (order.key.side == Side.Bid) new OrderBook(instrument, newLine, offers, newByKey)
        else new OrderBook(instrument, bids, newLine, newByKey)
      // no order with same key; adding new order to the book
      case None =>
        val newLine = line.get(order.price) match {
          case Some(orders) => line + (order.price -> (orders + order))
          case None => line + (order.price -> Aggregate(order.price, order.key.side, order))
        }
        val newByKey = byKey + (order.key -> order)
        if (order.key.side == Side.Bid) new OrderBook(instrument, newLine, offers, newByKey)
        else new OrderBook(instrument, bids, newLine, newByKey)
    }
  }

  def remove(key: OrderKey): OrderBook = byKey.get(key) match {
    case Some(order: Order) =>
      val line = if (order.key.side == Side.Bid) bids else offers
      val removedOld = line(order.price) - key
      val newLine = if (removedOld.isEmpty) line - order.price else line + (order.price -> removedOld)
      val newByKey = byKey - key
      if (order.key.side == Side.Bid) new OrderBook(instrument, newLine, offers, newByKey)
      else new OrderBook(instrument, bids, newLine, newByKey)
    case None => this
  }

  def removeById(orderId: String): OrderBook = {
    val toRemove = byKey.keys.filter(_.id == orderId)
    toRemove.foldLeft(this)((b: OrderBook, k: OrderKey) => b.remove(k))
  }

  def matchWith(order: Order, matched: Seq[Match]): (OrderBook, Seq[Match]) = {
    // assuming order is aggressive, "match only"
    val oppositeLine = if (order.key.side == Side.Bid) this.offers else this.bids
    if (oppositeLine.isEmpty || order.quantity == 0) {
      // nothing to match
      (this, matched)
    } else {
      val (bestPrice, bestAgg) = oppositeLine.head
      if (order.key.side == Side.Bid && bestPrice > order.price || order.key.side == Side.Offer && bestPrice < order.price) {
        // best opposite aggregate does not match with the current order
        (this, matched)
      } else {
        val oppOrder = bestAgg.orders.head
        val commonAmount: Decimal = math.min(order.quantity.toDouble, oppOrder.quantity.toDouble)
        val newOrder = order.copy(quantity = order.quantity - commonAmount)
        val newBook = if (oppOrder.quantity <= commonAmount) this.remove(oppOrder.key) else {
          val bookWithRemoved = this.remove(oppOrder.key)
          val newOppOrder = oppOrder.copy(quantity = oppOrder.quantity - commonAmount)
          bookWithRemoved.add(newOppOrder)
        }
        val matchEvent = Match(order.key.id, order.key.id, commonAmount, oppOrder.price)
        this.matchWith(newOrder, matched :+ matchEvent)
      }
    }
  }
}

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

  object Aggregate {
    def apply(price: Decimal, side: Side, orders: Order*): Aggregate = {
      val empty = new Aggregate(price, side, 0, ListMap.empty)
      orders.foldLeft(empty)((agg: Aggregate, ord: Order) => agg + ord)
    }
  }

  private val ASCENDING = TotalOrdering.on((x: Decimal) => x.toDouble)
  private val DESCENDING = ASCENDING.reverse

  def empty(instrument: Instrument): OrderBook = new OrderBook(instrument, bids = SortedMap.empty(DESCENDING), offers = SortedMap.empty(ASCENDING), byKey = ListMap.empty)

  def apply(orders: Iterable[Order]): OrderBook = orders.headOption match {
    case Some(order) =>
      val start: OrderBook = empty(order.key.instrument)
      orders.foldLeft(start)((a: OrderBook, b: Order) => a.add(b))
    case None => throw new IllegalArgumentException("Can't make an order book from an empty list")
  }

  def apply(orders: Order*): OrderBook = apply(orders.toList)
}
