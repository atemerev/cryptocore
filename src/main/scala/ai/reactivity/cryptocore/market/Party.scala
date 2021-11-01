package ai.reactivity.cryptocore.market

case class Party(id: String)

object Party:
  val Me: Party = Party("Me")

  given Conversion[String, Party] = Party(_)