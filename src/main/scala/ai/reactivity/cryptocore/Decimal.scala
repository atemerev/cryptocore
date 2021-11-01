package ai.reactivity.cryptocore
import scala.language.implicitConversions

/**
 * The Decimal type is a decimal-normalized Double, suitable for financial calculations.
 * It works nearly as fast as Double, but is automatically normalized for 8 digits of decimal precision.
 *
 * The idea is adopted from OpenHFT:
 * https://github.com/OpenHFT/Chronicle-Core/blob/ea/src/main/java/net/openhft/chronicle/core/Maths.java
 */
opaque type Decimal = Double

object Decimal:

  inline val SCALE_FACTOR = 1e8d
  inline val SCALE_LIMIT = Long.MaxValue / SCALE_FACTOR

  def apply(value: Double): Decimal = {

    if (value > SCALE_LIMIT || value < -SCALE_LIMIT)
      value
    else
      val scaled = if (value < 0)
        value * SCALE_FACTOR - 0.5 else
        value * SCALE_FACTOR + 0.5
      val truncated = scaled.toLong
      truncated / SCALE_FACTOR
  }

  def apply(value: Int): Decimal = Decimal(value.toDouble)

  extension (x: Decimal)
    def toDouble: Double = x

given Conversion[Double, Decimal] = Decimal(_)
given Conversion[Int, Decimal] = Decimal(_)