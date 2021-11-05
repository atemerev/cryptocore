package ai.reactivity.cryptocore

import org.scalatest.funsuite.AnyFunSuite

class DecimalSuite extends AnyFunSuite {
  test("Decimal rounding") {
    val d1: Decimal = 0.2 * 0.2
    assert(d1 === 0.04, "0.2 * 0.2 should be 0.04")
    val d2: Decimal = 2.1 - 0.2
    assert(d2 === 1.9, "2.1 - 0.2 should be 1.9")
    val d3: Decimal = 1.0 / 3
    assert(d3 === 0.33333333, "1 / 3 should round exactly to 0.33333333")
  }
}
