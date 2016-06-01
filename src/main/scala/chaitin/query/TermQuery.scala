package chaitin.query

import chaitin.index.Index
import chaitin.query.Query.{DocPosting, EmptyPostings, TermDocPosting}
import chaitin.search.ScoreDoc

import scala.collection.JavaConversions._

case class TermQuery(field: String, token: String, pos: Int) extends Query {


  override def postings(index: Index): Iterator[Seq[TermDocPosting]] = {
    if(index.invertedFields.contains(field)) {
      if(index.invertedFields(field).idx.contains(token)) {
        val postings = index.invertedFields(field).idx(token).map(e=>DocPosting(e._1, e._2))
        val df = postings.size
        postings.map(p=>Seq(TermDocPosting(pos, df, p))).iterator
      }
      else
        EmptyPostings
    }
    else
      EmptyPostings
  }

  override def score(index: Index, alignedTermPostings: Seq[TermDocPosting]): ScoreDoc = {
    val posting = alignedTermPostings.head
    val idf = math.log(index.docCount.get().toFloat/posting.df.toFloat).toFloat
    ScoreDoc(posting.docPosting.doc, (1+math.log(posting.docPosting.posting.tf)).toFloat * idf)
  }
}
