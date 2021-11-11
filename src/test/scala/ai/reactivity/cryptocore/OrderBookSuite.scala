package ai.reactivity.cryptocore

import ai.reactivity.cryptocore.OrderBookSuite.*
import ai.reactivity.cryptocore.instrument.*
import ai.reactivity.cryptocore.market.*
import ai.reactivity.cryptocore.market.Party.Me
import org.scalatest.funsuite.AnyFunSuite
import Symbology.*

class OrderBookSuite extends AnyFunSuite {
  val ts = 1273787999996L
  private val orderBook = fromCsv("1273787999996,EUR/USD,BIDS,1.25208,1000000,1.25212,2000000,1.25213,1000000,1.2522,1000000,1.2523,1000000,ASKS,1.2524,1000000,1.25246,1000000")

  test("remove best bid from order book") {
    val newBook = orderBook.remove(OrderKey(Me, BTC/USD, Side.Bid, "0"))
    assert("1273787999996,BTC/USD,BIDS,1.25208,1000000,1.25212,2000000,1.25213,1000000,1.2522,1000000,ASKS,1.2524,1000000,1.25246,1000000" === toCsv(newBook, ts))
  }
}

object OrderBookSuite {
  def fromCsv(csv: String): OrderBook = {
    def pair[A](l: List[A]): List[(A, A)] = l.grouped(2).collect { case List(a, b) => (a, b) }.toList
    val tokens = csv.split(",")
    val ccys = tokens(1).split("/")
    val instrument = CurrencyPair(Crypto(ccys(0)), Fiat(ccys(1)))
    val asksIndex = tokens.indexOf("ASKS")
    val bidS: List[(String, String)] = pair(tokens.slice(3, asksIndex).toList)
    val askS: List[(String, String)] = pair(tokens.slice(asksIndex + 1, tokens.length).toList)
    val bidSize = bidS.size
    val orders = bidS.zipWithIndex.map(
      n => Order(Party.Me, instrument, Side.Bid, (bidSize - n._2 - 1).toString, n._1._2.toDouble, n._1._1.toDouble)) ++
      askS.zipWithIndex.map(n => Order(Party.Me, instrument, Side.Offer, n._2.toString, n._1._2.toDouble, n._1._1.toDouble))
    OrderBook(orders)
  }

  def toCsv(orderBook: OrderBook, timestamp: Long): String = {
    "" + timestamp + "," + orderBook.instrument.toString + ",BIDS," +
      orderBook.bids.values.flatMap(_.orders).toSeq.reverse.map(o => o.price.toString + "," + o.quantity.toString).mkString(",") +
      ",ASKS," +
      orderBook.offers.values.flatMap(_.orders).map(o => o.price.toString + "," + o.quantity.toString).mkString(",")
  }
}
