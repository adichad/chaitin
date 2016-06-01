package chaitin.server

import java.io.Closeable


trait Server extends Closeable {
  def bind(): Unit
}
