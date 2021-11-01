package ai.reactivity.cryptocore.market

import ai.reactivity.cryptocore.Side
import ai.reactivity.cryptocore.instrument.Instrument

import java.util.UUID

case class Order(instrument: Instrument,
                 side: Side,
                 quantity: Decimal,
                 price: Decimal,
                 id: String = UUID.randomUUID().toString)
