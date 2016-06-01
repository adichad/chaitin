package chaitin.analysis


import chaitin.index.document.Field

case class WikiBodyTokenStreamFactory() extends TokenStreamFactory {
  override def tokenStream(field: Field[_]): TokenStream =
    LowercaseTokenFilter(RegexMatchTokenizer("\\w+".r, 0, field.asInstanceOf[Field[String]].value))
}

