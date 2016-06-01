package chaitin.source

import java.io.File

import chaitin.analysis.WikiBodyTokenStreamFactory
import chaitin.index.Index
import chaitin.index.document.{Document, StringField}

import scala.io.{Codec, Source}


case class WikiDumpIndexer(input: File) {
  def schematize(line: String): Document[StringField] = {
    val cells = line.split("\t")
    Document(
      StringField("id", cells(0), tokenize = false, invert = false, fieldData = true),
      StringField("title", cells(1), tokenize = false, invert = false, fieldData = true),
      StringField("body", cells(2), tokenize = true, invert = true, fieldData = false)
    )
  }

  val tokenStreamFactory = WikiBodyTokenStreamFactory()
  val lines = Source.fromFile(input)(Codec.UTF8).getLines.buffered

  def indexAll(indices: Index*): Unit = {
    val start = System.currentTimeMillis()
    var i = 0
    (lines map schematize) foreach { d =>
      indices.foreach(_.index(d, tokenStreamFactory))
      i+=1
      if(i%1000==0)
        print(".")
    }
    println("\nindexed ["+i+"] docs in ["+(System.currentTimeMillis() - start)+"] msecs")
  }
}
