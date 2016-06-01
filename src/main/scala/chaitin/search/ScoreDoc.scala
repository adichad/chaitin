package chaitin.search


case class ScoreDoc(doc: Int, score: Float) extends Comparable[ScoreDoc] {
  override def compareTo(o: ScoreDoc): Int = {
    if(score < o.score)
      -1
    else if(score > o.score)
      1
    else if(doc < o.doc)
      -1
    else if(doc > o.doc)
      1
    else
      0

  }
}
