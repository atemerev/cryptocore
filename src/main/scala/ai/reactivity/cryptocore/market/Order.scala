package ai.reactivity.cryptocore.market

import ai.reactivity.cryptocore.instrument.Instrument
import ai.reactivity.cryptocore.Decimal

import java.util.UUID

case class Order(key: OrderKey,
                 quantity: Decimal,
                 price: Decimal)

object Order {
  def apply(party: Party, instrument: Instrument, side: Side, id: String, quantity: Decimal, price: Decimal): Order =
    Order(OrderKey(party, instrument, side, id), quantity, price)
}