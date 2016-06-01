package chaitin.search.collector

import chaitin.index.Index
import chaitin.index.document.{Document, StringField}
import chaitin.search.ScoreDoc
import chaitin.utils.BoundedPriorityQueue


class TopDocCollector(offset: Int, limit: Int, fs: Array[String], index: Index) extends Collector {
  val topScoreDocs = new BoundedPriorityQueue[ScoreDoc](new Array[ScoreDoc](offset+limit))
  var count = 0
  override def collect(scoreDoc: ScoreDoc): Unit = {
    count+=1
    topScoreDocs.add(scoreDoc)
  }

  def get(): Array[Document[StringField]] = {
    val sortedDocs = topScoreDocs.sortAndFetch(offset, limit)
    sortedDocs.map { sd =>
      Document(
        fs.map { f =>
          val ff = index.forwardFields(f)
          val strs = ff.idx(sd.doc)
          val fin = strs.iterator.next()
          StringField(f, fin, false, false, false)
        } :_*
      )
    }
  }

  def totalCount = count
}
