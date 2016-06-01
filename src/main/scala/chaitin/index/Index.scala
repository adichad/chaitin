package chaitin.index


import java.util.concurrent.atomic.AtomicInteger

import chaitin.analysis.{TokenInfo, WikiBodyTokenStreamFactory}
import chaitin.index.document.{Document, StringField}

class Index() {
  lazy val invertedFields = collection.mutable.Map[String, PerFieldStringInvertedIndex]()
  lazy val forwardFields = collection.mutable.Map[String, PerFieldStringForwardIndex]()
  val docCount: AtomicInteger = new AtomicInteger(0)

  def index(doc: Document[StringField], tokenStreamFactory: WikiBodyTokenStreamFactory): Unit = {
    val cdoc = docCount.incrementAndGet()
    doc.foreach{ f =>
      if(f.fieldData) {
        val forwardIndex = forwardFields.getOrElseUpdate(f.name, new PerFieldStringForwardIndex())
        forwardIndex.fold(f.value, cdoc)
      }
      if(f.invert) {
        val invertedIndex = invertedFields.getOrElseUpdate(f.name, new PerFieldStringInvertedIndex())
        if(f.tokenize) {
          val tokenStream = tokenStreamFactory.tokenStream(f)
          val tokenInfo = new TokenInfo()
          while (tokenStream.incrementToken(tokenInfo)) {
            invertedIndex.fold(tokenInfo.token.toString(), tokenInfo.position, cdoc)
          }
        }
        else {
          invertedIndex.fold(f.value, position = 1, cdoc)
        }
      }
    }
  }


}
