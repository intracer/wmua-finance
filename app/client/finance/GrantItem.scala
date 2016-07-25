package client.finance

import org.intracer.finance.HasName

case class GrantItem(id: Option[Int] = None,
                     number: String,
                     category: String,
                     description: String,
                     unit: String,
                     qty: String,
                     costPerUnit: String,
                     totalCost: BigDecimal,
                     wmfContrib: Option[BigDecimal] = None,
                     otherSources: Option[BigDecimal] = None,
                     notes: Option[String]
                    ) extends HasName {

  //  Number	Item description	Unit	Qty	Cost per unit UAH / USD	Total cost USD	WMF contribution (USD)	Other sources (USD)	Notes
  override def toString: String = s"$number $description - $wmfContrib"

  override def name = number + " " + description
}

object GrantItemFactory {

  //  def detailed()

  //Number	Category	Item description	Unit	Number of units	Cost per unit	Total cost	Currency	Notes
  def simple(v: Seq[String]): GrantItem = {
    new GrantItem(None,
      number = v(0),
      category = v(1),
      description = v(2),
      unit = v(3),
      qty = v(4),
      costPerUnit = v(5),
      totalCost = toUSD(v(6)).get,
      notes = Option(v(8)).filter(_.nonEmpty)
    )

  }

  def apply(v: Seq[String]): GrantItem = {
    if (v.size == 9) {
      new GrantItem(None, v(0), "", v(1), v(2), v(3), v(4), toUSD(v(5)).get, toUSD(v(6)), toUSD(v(7))
        , Option(v(8)).filter(_.nonEmpty))
    } else {
      val parts = v(0).split(" ")
      new GrantItem(None, parts.head, "", parts.tail.mkString(" "), "", "", "", toUSD(v(1)).get, toUSD(v(2)), toUSD(v(3)),
        Option(v(4)).filter(_.nonEmpty)
      )
    }
  }

  def toUSD(s: String): Option[BigDecimal] = {
    if (s.isEmpty) None else Some(
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