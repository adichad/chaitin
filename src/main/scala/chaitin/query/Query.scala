package chaitin.query

import chaitin.index.{Index, Posting}
import chaitin.search.ScoreDoc

object Query {
  case class DocPosting(doc: Int, posting: Posting) extends Comparable[DocPosting] {
    override def compareTo(o: DocPosting): Int = doc.compareTo(o.doc)
  }

  case class TermDocPosting(pos: Int, df: Int, docPosting: DocPosting)

  object EmptyPostings extends Iterator[Seq[TermDocPosting]] {
    override def hasNext: Boolean = false

    override def next(): Seq[TermDocPosting] = null
  }
}
trait Query {

  import Query._

  def postings(index: Index): Iterator[Seq[TermDocPosting]]

  def score(index: Index, alignedTermDocPostings: Seq[TermDocPosting]): ScoreDoc

}
