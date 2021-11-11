package ai.reactivity.cryptocore

import ai.reactivity.cryptocore.OrderBookSuite.*
import ai.reactivity.cryptocore.instrument.*
import ai.reactivity.cryptocore.market.*
import ai.reactivity.cryptocore.market.Party.Me
import org.scalatest.funsuite.AnyFunSuite
import Symbology.*

class OrderBookSuite extends AnyFunSuite {
  val ts = 1273787999996L
  private val orderBook = fromCsv("1273787999996,BTC/USD,BIDS,1.25208,1000000,1.25212,2000000,1.25213,1000000,1.2522,1000000,1.2523,1000000,OFFERS,1.2524,1000000,1.25246,1000000")

  test("remove best bid from order book") {
    val newBook = orderBook remove OrderKey(Me, BTC/USD, Side.Bid, "0") 
    assert("1273787999996,BTC/USD,BIDS,1.25208,1000000,1.25212,2000000,1.25213,1000000,1.2522,1000000,OFFERS,1.2524,1000000,1.25246,1000000" === toCsv(newBook, ts))
  }

  test("remove mid bid from order book") {
    val newBook = orderBook remove OrderKey(Me, BTC/USD, Side.Bid, "2")
    "1273787999996,BTC/USD,BIDS,1.25208,1000000,1.25212,2000000,1.2522,1000000,1.2523,1000000,OFFERS,1.2524,1000000,1.25246,1000000" === toCsv(newBook, ts)
  }

  test("remove best Offer from order book") {
    val newBook = orderBook remove OrderKey(Me, BTC/USD, Side.Offer, "0")
    "1273787999996,BTC/USD,BIDS,1.25208,1000000,1.25212,2000000,1.25213,1000000,1.2522,1000000,1.2523,1000000,OFFERS,1.25246,1000000" === toCsv(newBook, ts)
  }

  test("remove far Offer from order book") {
    val newBook = orderBook remove OrderKey(Me, BTC/USD, Side.Offer, "1")
    "1273787999996,BTC/USD,BIDS,1.25208,1000000,1.25212,2000000,1.25213,1000000,1.2522,1000000,1.2523,1000000,OFFERS,1.2524,1000000" === toCsv(newBook, ts)
  }

  test("add mid bid to order book") {
    val newBook = orderBook add Order(OrderKey(Me, BTC/USD, Side.Bid, "*"), Decimal(500000), Decimal(1.25214))
    "1273787999996,BTC/USD,BIDS,1.25208,1000000,1.25212,2000000,1.25213,1000000,1.25214,500000,1.2522,1000000,1.2523,1000000,OFFERS,1.2524,1000000,1.25246,1000000" === toCsv(newBook, ts)
    val newBook2 = newBook add Order(OrderKey(Me, BTC/USD, Side.Bid, "*"), Decimal(1000000), Decimal(1.25214))
    "1273787999996,BTC/USD,BIDS,1.25208,1000000,1.25212,2000000,1.25213,1000000,1.25214,1000000,1.2522,1000000,1.2523,1000000,OFFERS,1.2524,1000000,1.25246,1000000" === toCsv(newBook2, ts)
    val newBook3 = newBook2 add Order(OrderKey(Me, BTC/USD, Side.Bid, "*2"), Decimal(1000), Decimal(1.25214))
    "1273787999996,BTC/USD,BIDS,1.25208,1000000,1.25212,2000000,1.25213,1000000,1.25214,1000,1.25214,1000000,1.2522,1000000,1.2523,1000000,OFFERS,1.2524,1000000,1.25246,1000000" === toCsv(newBook3, ts)
  }
}

object OrderBookSuite {
  def fromCsv(csv: String): OrderBook = {
    def pair[A](l: List[A]): List[(A, A)] = l.grouped(2).collect { case List(a, b) => (a, b) }.toList
    val tokens = csv.split(",")
    val ccys = tokens(1).split("/")
    val instrument = CurrencyPair(Crypto(ccys(0)), Fiat(ccys(1)))
    val OffersIndex = tokens.indexOf("OFFERS")
    val bidS: List[(String, String)] = pair(tokens.slice(3, OffersIndex).toList)
    val OFFERS: List[(String, String)] = pair(tokens.slice(OffersIndex + 1, tokens.length).toList)
    val bidSize = bidS.size
    val orders = bidS.zipWithIndex.map(
      n => Order(Party.Me, instrument, Side.Bid, (bidSize - n._2 - 1).toString, n._1._2.toDouble, n._1._1.toDouble)) ++
      OFFERS.zipWithIndex.map(n => Order(Party.Me, instrument, Side.Offer, n._2.toString, n._1._2.toDouble, n._1._1.toDouble))
    OrderBook(orders)
  }

  def toCsv(orderBook: OrderBook, timestamp: Long): String = {
    "" + timestamp + "," + orderBook.instrument.toString + ",BIDS," +
      orderBook.bids.values.flatMap(_.orders).toSeq.reverse.map(o => o.price.toString + "," + o.quantity.toString).mkString(",") +
      ",OFFERS," +
      orderBook.offers.values.flatMap(_.orders).map(o => o.price.toString + "," + o.quantity.toString).mkString(",")
  }
}
