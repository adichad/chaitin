package chaitin.server

import java.io.File
import java.util

import chaitin.analysis.{TokenInfo, WikiBodyTokenStreamFactory}
import chaitin.index.Index
import chaitin.index.document.{Document, StringField}
import chaitin.query.{ConjunctiveProximityQuery, TermQuery}
import chaitin.search.Searcher
import chaitin.search.collector.TopDocCollector
import chaitin.source.WikiDumpIndexer
import scala.collection.JavaConversions._


class RootServer(indexPath: String) extends Server {
  lazy val index: Index = new Index()

  println("indexing: "+indexPath)
  WikiDumpIndexer(new File(indexPath)).indexAll(index)

  var alive: Boolean = true
  override def bind(): Unit = {
    val searcher = new Searcher(index)
    while(alive) {
      print("search> ")
      val line = io.Source.stdin.getLines().next()
      val start = System.currentTimeMillis()
      val tokenStream = WikiBodyTokenStreamFactory().tokenStream(StringField("body", line, true, false, false))
      val tokenInfo = new TokenInfo
      val collector = new TopDocCollector(0, 10, Array("id", "title"), index)
      val termQueries = new util.ArrayList[TermQuery]
      while(tokenStream.incrementToken(tokenInfo)) {
        termQueries.add(new TermQuery("body", tokenInfo.token.toString(), tokenInfo.position))
      }
      val query =
        if(termQueries.size == 1) termQueries.get(0)
        else ConjunctiveProximityQuery(termQueries:_*)
      val hits =
        if(termQueries.size()>0) {
          searcher.search(query, collector)
        }
        else
          Array[Document[StringField]]()
      hits.foreach(d=>println(d.map(_.value).mkString("\t")))
      println("ranked ["+collector.totalCount+"] docs in ["+(System.currentTimeMillis() - start)+"] msecs")
      println
    }
  }

  override def close(): Unit = {
    alive = false
  }

}
