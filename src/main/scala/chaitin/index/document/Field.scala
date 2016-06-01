package chaitin.index.document


trait Field[T] {
  val name: String
  val value: T
  val invert: Boolean
  val fieldData: Boolean
}

case class StringField(
                      name: String,
                      value: String,
                      tokenize: Boolean,
                      invert: Boolean,
                      fieldData: Boolean
                    ) extends Field[String] {

}