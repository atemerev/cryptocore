package ai.reactivity.cryptocore

/**
 * The Decimal type is a decimal-normalized Double, suitable for financial calculations.
 * It works nearly as fast as Double, but is automatically normalized for 8 digits of decimal precision.
 *
 * The idea is adopted from OpenHFT:
 * https://github.com/OpenHFT/Chronicle-Core/blob/ea/src/main/java/net/openhft/chronicle/core/Maths.java
 */

// Back to value types. I couldn't use opaque types, as toString could not be modified.

class Decimal(private val dbl: Double) extends AnyVal with Ordered[Decimal] {
  def value: Double = dbl

  def /(that: Decimal): Decimal = Decimal.apply(this.dbl / that.dbl)

  def +(that: Decimal): Decimal = Decimal.apply(this.dbl + that.dbl)

  def -(that: Decimal): Decimal = Decimal.apply(this.dbl - that.dbl)

  def *(that: Decimal): Decimal = Decimal.apply(this.dbl * that.dbl)

  def unary_-: : Decimal = Decimal.apply(-this.dbl)

  def signum: Int = dbl.sign.toInt

  def toInt: Int = dbl.toInt

  def toLong: Long = dbl.toLong

  def toFloat: Float = dbl.toFloat

  def toDouble: Double = dbl

  // Yes, we can do that!
  override def compare(that: Decimal): Int = java.lang.Double.compare(this.dbl, that.dbl)



  // And that!
  def ==(that: Decimal): Boolean = this.dbl == that.dbl
  def ==(that: Int): Boolean = this.dbl == that.toDouble
  def ==(that: Long): Boolean = this.dbl == that.toDouble  

  override def toString: String = if (dbl == dbl.toInt) dbl.toInt.toString else dbl.toString

  def canEqual(other: Any): Boolean = other.isInstanceOf[Decimal]

  // yeah, this is a hack, the dreaded universal equality. Sorry, couldn't make it work otherwise.

  override def equals(other: Any): Boolean = other match {
    case that: Decimal =>
      (that canEqual this) &&
        dbl == that.dbl
    case that: Double =>
      dbl == that
    case that: Int =>
      dbl == that.toDouble
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(dbl)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}

object Decimal {

  val DEFAULT_SCALE_FACTOR: Double = 1e8

  def apply(value: Double)(implicit factor: Double = DEFAULT_SCALE_FACTOR): Decimal = {
    if (value > Long.MaxValue / factor || value < -Long.MaxValue / factor) new Decimal(value)
    else new Decimal((if (value < 0) value * factor - 0.5 else value * factor + 0.5).toLong / factor)
  }

  given Conversion[Double, Decimal] = Decimal(_)
  given Conversion[Int, Decimal] = Decimal(_)

  given CanEqual[Decimal, Double] = CanEqual.derived
  given CanEqual[Double, Decimal] = CanEqual.derived
  given CanEqual[Decimal, Int] = CanEqual.derived
  given CanEqual[Int, Decimal] = CanEqual.derived
}