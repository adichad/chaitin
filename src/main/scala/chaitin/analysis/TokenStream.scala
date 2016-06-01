package chaitin.analysis


trait TokenStream {
  def incrementToken(tokenInfo: TokenInfo): Boolean
}
