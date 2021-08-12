package ai.reactivity.cryptocore

case class Party(id: String)

object Party:
  lazy val Me = Party("Me")
