package client.finance

import org.scalawiki.MwBot
import org.scalawiki.wikitext.SwebleParser
import org.sweble.wikitext.engine.config.WikiConfig
import org.sweble.wikitext.engine.utils.DefaultConfigEnWp
import org.sweble.wikitext.parser.nodes._

import scala.collection.JavaConverters._


object GrantReader extends SwebleParser {

  val config: WikiConfig = DefaultConfigEnWp.generate

  val bot = MwBot.fromHost("meta.wikimedia.org")

  def main(args: Array[String]) {

    val title = "Grants:PEG/WM_UA/Programs_in_Ukraine_2014"
    val items = grantItems(title)

    println(items)
  }

  def sectionByTitle(title: String): PartialFunction[WtNode, WtNode] = {
    case node: WtNode if Section(node).exists(_.heading.toLowerCase == title.toLowerCase) => node
  }

  def tableNode: PartialFunction[WtNode, WtTable] = {
    case t: WtTable => t
  }

  val tableHeader =
    """{| class="wikitable"
      ||-
      |! Number
      |! Category
      |! Item description
      |! Unit
      |! Number of units
      |! Cost per unit
      |! Total cost
      |! Currency
      |! Notes
      ||-
      | """.stripMargin

  def grantItems(title: String): Seq[GrantItem] = {
    val text = bot.await(bot.pageText(title))

    val simple = text.contains("{{Grants budget table simple}}")
    val replaced = text.replace("{{Grants budget table simple}}", tableHeader)

    val cp = parsePage(title, replaced)

    val budgetSection = findNode(cp.getPage, sectionByTitle("Detailed Breakdown")).orElse {
      findNode(cp.getPage, sectionByTitle("Budget and resources"))
        .orElse(findNode(cp.getPage, sectionByTitle("Budget")))
    }.get

    val table = findNode(budgetSection, tableNode).get

    val rows = table.getBody.get(0).asInstanceOf[WtTableImplicitTableBody].getBody.asScala.map(_.asInstanceOf[WtTableRow])
    //sections.find()
    val headers = rows.head.getBody.asScala.map(_.asInstanceOf[WtTableHeader])

    val titles = headers.map(getText)

    //    println(titles.toString())

    val items = for (row <- rows.tail if row.getBody.size() > 0) yield {

      val cells = collectNodes(row, { case c: WtTableCell => c })

      val values = cells.map(c => getText(c.getBody).trim)

      if (simple)
        GrantItemFactory.simple(values)
      else
        GrantItemFactory(values)
    }
    items
  }
}
