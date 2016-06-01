package chaitin.analysis

import scala.util.matching.Regex


case class RegexMatchTokenizer(regex: Regex, group: Int, input: IndexedSeq[Char]) extends TokenStream {
  val matches = regex.findAllIn(input).matchData.buffered

  override def incrementToken(tokenInfo: TokenInfo): Boolean = {
    if(matches.hasNext) {
      val m = matches.next().group(group)
      tokenInfo.token.setLength(0)
      if(m!=null && m.length>0) {
        tokenInfo.position += 1
        tokenInfo.token.append(m)
      }
      true
    }
    else
      false
  }
}
