package com.softwaremill.diffx

trait DiffResult {
  def isIdentical: Boolean
  def show: String = showIndented(5)

  private[diffx] def showIndented(indent: Int): String
}

case class DiffResultObject(name: String, fields: Map[String, DiffResult]) extends DiffResultDifferent {
  override private[diffx] def showIndented(indent: Int): String = {
    val showFields = fields.map(f => s"${i(indent)}${f._1}: ${f._2.showIndented(indent + 5)}")
    s"""$name(
       |${showFields.mkString("\n")})""".stripMargin
  }
}

case class DiffResultSet(diffs: List[DiffResult]) extends DiffResultDifferent {
  override private[diffx] def showIndented(indent: Int): String = {
    val showFields = diffs.map(f => s"${i(indent)}$f")
    s"""Set(
       |${showFields.mkString("\n")})""".stripMargin
  }
}

trait DiffResultDifferent extends DiffResult {
  override def isIdentical: Boolean = false
  protected def i(indent: Int) = " " * indent
}

case class DiffResultValue[T](left: T, right: T) extends DiffResultDifferent {
  override def showIndented(indent: Int): String = showChange(left.toString, right.toString)
}

case class Identical[T](value: T) extends DiffResult {
  override def isIdentical: Boolean = true
  override def showIndented(indent: Int): String = value.toString
}

case class DiffResultMissing[T](value: T) extends DiffResultDifferent {
  override def showIndented(indent: Int): String = {
    red(value.toString)
  }
}
case class DiffResultAdditional[T](value: T) extends DiffResultDifferent {
  override def showIndented(indent: Int): String = {
    green(value.toString)
  }
}
