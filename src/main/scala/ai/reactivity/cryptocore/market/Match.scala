package ai.reactivity.cryptocore.market

import ai.reactivity.cryptocore.Decimal

case class Match(aggressiveOrderId: String, passiveOrderId: String, quantity: Decimal, price: Decimal)
