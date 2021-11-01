package ai.reactivity.cryptocore.market

case class Party(id: String)

object Party:
  lazy val Me = Party("Me")
