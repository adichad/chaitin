package chaitin.search

import chaitin.index.Index
import chaitin.index.document.{Document, StringField}
import chaitin.query.Query
import chaitin.search.collector.TopDocCollector

class Searcher(index: Index) {
  def search(query: Query, collector: TopDocCollector): Array[Document[StringField]] = {
    val postings = query.postings(index)
    while(postings.hasNext)
      collector.collect(query.score(index, postings.next()))

    collector.get()
  }
}
