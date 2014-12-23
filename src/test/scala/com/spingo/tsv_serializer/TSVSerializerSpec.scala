package com.spingo.tsv_serializer

import org.scalatest.{FunSpec, Matchers}
case class Thing(a: String, b: Int, c: Double, d: Option[String])

class TSVSerializerSpec extends FunSpec with Matchers {
  implicit val stringWriter = new TSVWriter[String] {
    def write(v: String) =
      v.
        replace("\t", " ").
        replace("()", "\\()")
  }
  implicit val intWriter = new TSVWriter[Int] {
    def write(v: Int) = v.toString
  }
  implicit val doubleWriter = new TSVWriter[Double] {
    def write(v: Double) = v.toString
  }
  class OptionWriter[T](implicit writer: TSVWriter[T]) extends TSVWriter[Option[T]] {
    def write(v: Option[T]) = v map (writer.write) getOrElse "()"
  }
  implicit def optionWriter[T](implicit writer: TSVWriter[T]): TSVWriter[Option[T]] = new OptionWriter[T]
  val thingWriter = TSVSerializer.writer[Thing]

  describe("writing TSV") {
    it("does it") {
      thingWriter.write(Thing("string\tvalue", 2, 5.5, None)) should be ("string value\t2\t5.5\t()")
    }
  }
}
