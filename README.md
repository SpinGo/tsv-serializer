# TSV Serializer

A macro-based, extensible TSV Serializer library.

Assuming you've already decided you need to write TSV, why this library:

- FAST. Serialization code is generated at compile-time. No run-time reflection.
- Reliable. Because macros are used, if the library doesn't know how to serialize something, you'll know at compile time.
- Extensible. Define implicit formatters for any of your types which are automatically used by the case-class macro generators.

Example usage:

```scala
package demo

import com.spingo.tsv_serializer._
import org.joda.time.format.ISODateTimeFormat
import org.joda.time.DateTime

case class Row(
  name: String,
  age: Option[Int],
  createdAt: Option[DateTime],
  active: Boolean
)
object TSVFormats {
  implicit val stringTSVWriter = new TSVWriter[String] {
    def write(v: String) =
      v.
        replace("\t", " ").
        replace("()", "\\()")
  }
  implicit val intTSVWriter = new TSVWriter[Int] {
    def write(v: Int) = v.toString
  }
  implicit val doubleTSVWriter = new TSVWriter[Double] {
    def write(v: Double) = v.toString
  }
  implicit val dateTimeTSVWriter = new TSVWriter[DateTime] {
    def write(d: DateTime) = d.toString(ISODateTimeFormat.dateTime)
  }
  implicit val booleanTSVWriter = new TSVWriter[Boolean] {
    def write(b: Boolean) = b.toString
  }

  class OptionWriter[T](implicit writer: TSVWriter[T]) extends TSVWriter[Option[T]] {
    def write(v: Option[T]) = v map (writer.write) getOrElse "()"
  }
  implicit def optionTSVWriter[T](implicit writer: TSVWriter[T]): TSVWriter[Option[T]] = new OptionWriter[T]

  implicit val longTSVWriter = new TSVWriter[Long] {
    def write(v: Long) = v.toString
  }
  implicit val rowWriter = TSVSerializer.writer[Row]
}

object Main extends App {
  import TSVFormats._
  val data = Row("Bob", Some(30), Some(DateTime.now), true)
  val output = rowWriter.write(data)
  println(output) // le TSV !
}
```
