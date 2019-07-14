package asserts.diff

sealed trait NewDiffFor[T] {
  def compare(l: T, r: T): DiffResult
}

case class ObjNewDiffFor[T](name: String, fields: List[FieldDiff[T, _]], strategy: Strategy) extends NewDiffFor[T] {
  override def compare(l: T, r: T): DiffResult = {
    val partResults = fields.map { case FieldDiff(fName, accessor, differ) => fName -> differ.compare(accessor(l), accessor(r)) }.toMap
    if (partResults.values.forall(_.isIdentical)) {
      Identical(l)
    } else {
      DiffResultObject(name, partResults)
    }
  }
}

//fields.map{ f=> f.newDiffFor.compare(f.dereference(f.f(l)),f.f(r)) }

case class FieldDiff[T, U](name: String, accessChild: T => U, newDiffFor: NewDiffFor[U])

case class ValueDiff[T](strategy: Strategy) extends NewDiffFor[T] {
  def compare(l: T, r: T): DiffResult = {
    if (l.toString == r.toString) {
      Identical(l)
    } else {
      DiffResultValue(l, r)
    }
  }
}

sealed trait Strategy

object Strategy {

  case object Compare extends Strategy

  case object Ignore extends Strategy

}

//ObjDiff(person, Map("age" ->Accessor(_.age, valueDiff))

//ObjDiff(person, Map("age" ->Accessor(_.parent, objDiff))

//valueDiff(Compare)

object TTT {
  val person = Person2("kasper")
  val a =
    ObjNewDiffFor[Person2]("person", List(FieldDiff("name", _.name, ValueDiff(Strategy.Compare))), Strategy.Compare)

}

case class Person2(name: String)
