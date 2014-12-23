package com.spingo.tsv_serializer

import language.experimental.macros
import scala.reflect.macros.Context
import scala.reflect.runtime.{ universe => ru }

trait TSVWriter[T] {
  def write(value: T): String
}

object TSVSerializer {
  implicit def writer[T]: TSVWriter[T] = macro writer_impl[T]
  def writer_impl[T](c: Context)(implicit wtt: c.WeakTypeTag[T]): c.Expr[TSVWriter[T]] = {
    import c.universe._
    val tpe = wtt.tpe

    if((!tpe.typeSymbol.isClass) || (!tpe.typeSymbol.asClass.isCaseClass))
      throw new Exception("The type of the object provided to TSVWriter.writer[T] must be a case class")

    val companion = tpe.typeSymbol.companionSymbol
    val applyMethodSymbol = companion.typeSignature.member(newTermName("apply")).asTerm.alternatives(0).asMethod

    val properties = applyMethodSymbol.paramss.head.zipWithIndex map { case (p, n) =>
      val runtime = c.reifyType(treeBuild.mkRuntimeUniverseRef, EmptyTree, p.typeSignature)
      val t = newTermName(s"writer${n}")

      (
        t,
        q"""implicitly[TSVWriter[${p.typeSignature}]]""",
        p
      )
    }

    val name = tpe.typeSymbol.name.toString

    val assignments = properties.map { case(t, writer, _) =>
      q"val ${t} = ${writer}"
    }

    val writerCalls = properties.map { case(t, _, property) =>
      q"output.write(${t}.write(v.${property.name.toTermName}))"
    }
    val outputDelimiter =
      q"output.append('\t')"

    def stagger[T](input: List[T], delimiter: T, output: List[T] = List.empty[T]): List[T] = input match {
      case Nil => // only encountered if empty list provided
        Nil
      case head :: Nil =>
        (head :: output).reverse
      case head :: tail =>
        stagger(tail, delimiter, delimiter :: head :: output)
    }
    val expressions = stagger(writerCalls, outputDelimiter)

    c.Expr[TSVWriter[T]](q"""
      new TSVWriter[$tpe] {
        ..${assignments}
        def write(v: $tpe) = {
          val output = new java.io.StringWriter
          ..${expressions}
          output.toString
        }
      }
""")
  }
}
