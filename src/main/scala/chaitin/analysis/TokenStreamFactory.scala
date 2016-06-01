package chaitin.analysis

import chaitin.index.document.Field


trait TokenStreamFactory {
  def tokenStream(field: Field[_]): TokenStream
}
