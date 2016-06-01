package chaitin.index

import java.util

case class Posting(var tf: Int, positions: util.ArrayList[Int])

class PerFieldStringInvertedIndex {
  lazy val idx = collection.mutable.Map[String, util.TreeMap[Int, Posting]]()

  def fold(token: String, position: Int, doc: Int): Unit = {
    val postingList = idx.getOrElseUpdate(token, new util.TreeMap[Int, Posting])
    val posting = postingList.getOrDefault(doc, new Posting(0, new util.ArrayList[Int]))
    postingList.put(doc, posting)
    posting.positions.add(position)
    posting.tf+=1
  }

  override def toString = {
    idx.toString()
  }
}

class PerFieldStringForwardIndex {
  lazy val idx = collection.mutable.Map[Int, collection.mutable.HashSet[String]]()

  def fold(value: String, doc: Int): Unit = {
    val values = idx.getOrElseUpdate(doc, collection.mutable.HashSet[String]())
    values.add(value)
  }

  override def toString = {
    idx.toString()
  }
}