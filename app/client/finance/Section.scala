package client.finance

import org.sweble.wikitext.parser.nodes.{WtNode, WtText, WtSection}

class Section(val wtSection: WtSection) {
  def heading =
    wtSection.getHeading.get(0).asInstanceOf[WtText].getContent.trim
}

object Section {
  def apply(wtNode: WtNode) = Option(wtNode) collect {
    case wtSection: WtSection => new Section(wtSection)
  }

  def fromTraversable(nodes: TraversableOnce[WtNode]) = nodes.flatMap(Section.apply)

  def isSection(wtNode: WtNode) = wtNode.getNodeType == WtNode.NT_SECTION
}