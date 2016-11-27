package client.finance

import org.intracer.finance.Grant
import org.scalawiki.MwBot
import org.scalawiki.wikitext.SwebleParser
import org.sweble.wikitext.engine.config.WikiConfig
import org.sweble.wikitext.engine.utils.DefaultConfigEnWp
import org.sweble.wikitext.parser.nodes._

import scala.collection.JavaConverters._


object GrantReader extends SwebleParser {

  val config: WikiConfig = DefaultConfigEnWp.generate

  val bot = MwBot.fromHost("meta.wikimedia.org")

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

  def grantItems(grant: Grant): Seq[GrantItem] = {
    val title = grant.name

    val budgetPage = if (title.startsWith("Grants:APG")) {
      "Grants:APG/Proposals/2015-2016 round1/Wikimedia Ukraine/Proposal form/Detailed budget"
    } else {
      title
    }

    val text = bot.await(bot.pageText(budgetPage))

    val simple = text.contains("{{Grants budget table simple}}")
    val replaced = text
      .replace("{{Grants budget table simple}}", tableHeader)
      .replace("<translate>", "")
      .replace("</translate>", "")
      .replace("-->\n", "-->")

    val cp = parsePage(title, replaced)

    val budgetSection = findNode(cp.getPage, sectionByTitle("Detailed Breakdown")).orElse {
      findNode(cp.getPage, sectionByTitle("Budget and resources"))
        .orElse(findNode(cp.getPage, sectionByTitle("Budget")))
    }.getOrElse(cp.getPage)

    val table = findNode(budgetSection, tableNode).get

    val rows = table.getBody.get(0).asInstanceOf[WtTableImplicitTableBody].getBody.asScala.map(_.asInstanceOf[WtTableRow])
    //sections.find()
    //    val headers = rows.head.getBody.asScala.map(_.asInstanceOf[WtTableHeader])
    //    val titles = headers.map(getText)

    //    println(titles.toString())

    val items = rows.tail.filter(_.getBody.size() > 0).flatMap { row =>

      val cells = collectNodes(row, { case c: WtTableCell => c })

      val values = cells.map(c => getText(c.getBody).trim).toIndexedSeq

      if (simple)
        GrantItemFactory.simple(values)
      else if (title.startsWith("Grants:APG"))
        GrantItemFactory.apg(values)
      else
        GrantItemFactory(values)
    }

    items.map(_.copy(grantId = grant.id))
  }
}
