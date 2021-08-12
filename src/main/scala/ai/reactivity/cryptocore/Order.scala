package ai.reactivity.cryptocore

import java.util.UUID

case class Order(instrument: Instrument, 
                 side: Side, 
                 quantity: Decimal, 
                 price: Decimal, 
                 id: String = UUID.randomUUID().toString)
