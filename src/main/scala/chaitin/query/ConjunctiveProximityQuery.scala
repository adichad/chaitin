package chaitin.query

import chaitin.index.Index
import chaitin.query.Query.{EmptyPostings, TermDocPosting}
import chaitin.search.ScoreDoc
import chaitin.utils.BoundedPriorityQueue
import scala.collection.JavaConversions._


case class ConjunctiveProximityQuery(termQueries: TermQuery*) extends Query {
  case class ComparableTermDocPostingIterator(
                                               var termDocPosting: TermDocPosting,
                                               iter: Iterator[Seq[TermDocPosting]]
                                             )
    extends Comparable[ComparableTermDocPostingIterator] {
    override def compareTo(o: ComparableTermDocPostingIterator) =
      termDocPosting.docPosting.doc.compareTo(o.termDocPosting.docPosting.doc)
  }

  override def postings(index: Index) = {
    val contributors = termQueries.map(_.postings(index)).filter(_.hasNext)
    if(contributors.length==termQueries.length) {
      new Iterator[Seq[TermDocPosting]] {
        val termPostings = contributors.map(c => ComparableTermDocPostingIterator(c.next().head, c))

        val tQPQ =
          new BoundedPriorityQueue[ComparableTermDocPostingIterator](
            new Array[ComparableTermDocPostingIterator](termQueries.size)
          )
        termPostings.foreach(tQPQ.add)

        override def hasNext: Boolean = {
          while(
            tQPQ.size == termQueries.length &&
              termPostings.map(posting=>posting.termDocPosting.docPosting.doc).toSet.size>1) {
            val termPosting = tQPQ.pluckMin()
            if(termPosting.iter.hasNext) {
              termPosting.termDocPosting = termPosting.iter.next().head
              tQPQ.add(termPosting)
            }
            else
              return false
          }
          tQPQ.size == termQueries.length &&
            termPostings.map(posting=>posting.termDocPosting.docPosting.doc).toSet.size==1
        }

        override def next(): Seq[TermDocPosting] = {
          val postings = termPostings.map(tp=>tp.termDocPosting)
          val termPosting = tQPQ.pluckMin()
          if(termPosting.iter.hasNext) {
            termPosting.termDocPosting = termPosting.iter.next().head
            tQPQ.add(termPosting)
          }
          postings
        }
      }
    }
    else {
      EmptyPostings
    }

  }

  def proximity(termPostings: Seq[TermDocPosting]): Float = {
    val qPos = termPostings.map(tp=>tp.pos)
    val queryVolume = qPos.combinations(2).map(p=>math.abs(p(0)-p(1))).sum

    case class DocPositions(iter: BufferedIterator[Int]) extends Comparable[DocPositions] {
      override def compareTo(o: DocPositions): Int = iter.head.compareTo(o.iter.head)
    }

    val queue = new BoundedPriorityQueue[DocPositions](new Array[DocPositions](termPostings.size))
    val dPos = termPostings.map(tp=>DocPositions(tp.docPosting.posting.positions.iterator().buffered))
    dPos.foreach(queue.add)
    var min = Float.MaxValue
    while(queue.size == termPostings.size) {
      min = math.min(min, dPos.map(dp => dp.iter.head).combinations(2).map(p=>math.abs(p(0)-p(1))).sum)
      val dp = queue.pluckMin()
      dp.iter.next()
      if(dp.iter.hasNext) {
        queue.add(dp)
      }
    }
    queryVolume/min
  }

  override def score(index: Index, alignedTermPostings: Seq[TermDocPosting]): ScoreDoc = {
    new ScoreDoc(
      alignedTermPostings.head.docPosting.doc,
      alignedTermPostings.map(posting=>
        math.log(index.docCount.get().toFloat/posting.df.toFloat).toFloat *
          (1+math.log(posting.docPosting.posting.tf)).toFloat
      ).sum*proximity(alignedTermPostings)
    )
  }
}
