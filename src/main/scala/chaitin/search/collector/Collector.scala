package chaitin.search.collector

import chaitin.search.ScoreDoc

trait Collector {
  def collect(scoreDoc: ScoreDoc)

}
