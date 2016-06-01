package chaitin.index.document


case class Document[T <: Field[_]](fields: T*) extends Iterable[T] {
  override def iterator: Iterator[T] = fields.iterator
}
