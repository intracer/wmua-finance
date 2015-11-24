package org.intracer.finance

object AlphaNumOrdering extends Ordering[String] {
  val comparator = new AlphanumComparator()
  def compare(a: String, b: String) = comparator.compare(a, b)
}

