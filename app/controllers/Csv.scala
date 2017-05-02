package controllers

import java.io.{StringWriter, Writer}

import com.github.tototoshi.csv.CSVWriter
import org.intracer.finance.{Dictionary, Operation}

object Csv {

  def writeStringBuffer(data: Seq[Seq[Any]]): StringBuffer = {
    val writer = new StringWriter()
    write(data, writer)
    writer.getBuffer
  }

  def writeRow(data: Seq[Any]): String = {
    val writer = new StringWriter()
    val csv = CSVWriter.open(writer)
    csv.writeRow(data)
    csv.close()
    writer.getBuffer.toString
  }

  def write(data: Seq[Seq[Any]], writer: Writer): Unit = {
    val csv = CSVWriter.open(writer)
    csv.writeAll(data)
    csv.close()
  }

  def exportOperations(operations: Seq[Operation]): Seq[Seq[String]] = {
    val header = Seq("Date", "Project", "Category", "Grant", "GrantItem", "Amount", "Account", "Description")

    val rows = operations.map { o =>
      Seq(
        o.date.toString.substring(0, 10),
        o.to.projectName,
        o.to.categoryName,
        o.to.grantName,
        o.to.grantItem.map(_.name).getOrElse(""),
        o.amount.map(_.toString).getOrElse(""),
        o.to.account.name,
        o.to.description
      )
    }

    header +: rows
  }

  def addBom(data: Seq[Seq[String]]): Seq[Seq[String]] = {
    val BOM = "\ufeff"
    val header = data.head
    val headerWithBom = (BOM + header.head) +: header.tail
    headerWithBom +: data.tail
  }
}