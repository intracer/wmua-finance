package client.finance

import org.intracer.finance.HasName

case class GrantItem(id: Option[Int] = None,
                     grantId: Option[Int] = None,
                     number: String,
                     description: String,
                     totalCost: BigDecimal,
                     notes: Option[String],
                     category: Option[String] = None,
                     unit: Option[String] = None,
                     qty: Option[String] = None,
                     costPerUnit: Option[String] = None,
                     wmfContrib: Option[BigDecimal] = None,
                     otherSources: Option[BigDecimal] = None
                    ) extends HasName {

  //  Number	Item description	Unit	Qty	Cost per unit UAH / USD	Total cost USD	WMF contribution (USD)	Other sources (USD)	Notes
  override def toString: String = s"$number $description $$$totalCost"

  override def name = number + " " + description

}

object GrantItemFactory {

  //  def detailed()

  //Number	Category	Item description	Unit	Number of units	Cost per unit	Total cost	Currency	Notes
  def simple(v: Seq[String]): Option[GrantItem] = {
    Some(new GrantItem(None,
      number = v(0),
      category = Some(v(1)),
      description = v(2),
      unit = Some(v(3)),
      qty = Some(v(4)),
      costPerUnit = Some(v(5)),
      totalCost = toUSD(v(6)).get,
      notes = Option(v(8)).filter(_.nonEmpty)
    )
    )
  }

  def apg(v: Seq[String]): Option[GrantItem] = {
    val firstParts = v(0).split(" ").toSeq
    if (firstParts(0).charAt(0).isDigit && firstParts(0).charAt(1) == '.') {
      Some(new GrantItem(None,
        number = firstParts(0),
        category = None,
        description = firstParts.slice(1, firstParts.size).mkString(" "),
        unit = None,
        qty = None,
        costPerUnit = None,
        totalCost = toUSD(v(1)).get,
        notes = Option(v(2)).filter(_.nonEmpty)
      )
      )
    } else {
      None
    }

  }

  def apply(v: Seq[String]): Option[GrantItem] = {
    if (v.size == 9) {
      Some(new GrantItem(None, None,
        number = v(0),
        category = None,
        description = v(1),
        unit = Some(v(2)),
        qty = Some(v(3)),
        costPerUnit = Some(v(4)),
        totalCost = toUSD(v(5)).get,
        wmfContrib = toUSD(v(6)),
        otherSources = toUSD(v(7)),
        notes = Option(v(8)).filter(_.nonEmpty)))
    } else {
      val parts = v(0).split(" ")
      Some(new GrantItem(None, None,
        number = parts.head,
        category = None,
        description = parts.tail.mkString(" "),
        unit = None,
        qty = None,
        costPerUnit = None,
        totalCost = toUSD(v(1)).get,
        wmfContrib = toUSD(v(2)),
        otherSources = toUSD(v(3)),
        notes = Option(v(4)).filter(_.nonEmpty)
      ))
    }
  }

  def toUSD(s: String): Option[BigDecimal] = {
    if (s.isEmpty) None
    else Some(
      s
        .replace(",", "")
        .replace("&nbsp;", "")
        .toDouble
    )
  }

  //  def toUSD(s: String): Money = {
  //    if (s.isEmpty)
  //      USD(0)
  //    else
  //      USD(s.toDouble)
  //  }
}