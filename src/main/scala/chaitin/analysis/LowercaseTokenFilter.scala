package chaitin.analysis

/**
  * Created by adichad on 30/04/16.
  */
case class LowercaseTokenFilter(tokenStream: TokenStream) extends TokenStream {

  implicit class StringBuilderPimp(sb: StringBuilder) {
    def toLower(): Unit = {
      sb.indices.foreach { i=>
        sb.setCharAt(i, sb.charAt(i).toLower)
      }
    }
  }

  override def incrementToken(tokenInfo: TokenInfo): Boolean = {
    if(tokenStream.incrementToken(tokenInfo)) {
      tokenInfo.token.toLower()
      true
    }
    else
      false
  }
}
