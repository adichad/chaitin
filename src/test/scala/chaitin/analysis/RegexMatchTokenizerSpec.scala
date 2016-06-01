package chaitin.analysis

import org.junit.runner.RunWith
import org.specs2._
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
object RegexMatchTokenizerSpec extends Specification {

  case class tokenizeHelloWorld() {
    val tokenizer = RegexMatchTokenizer("\\w+".r, 0, "hello, world!")
    val tokenInfo = new TokenInfo
    def e1 = {
      tokenizer.incrementToken(tokenInfo)
      tokenInfo.token.toString mustEqual "hello"
    }

    def e2 = {
      tokenizer.incrementToken(tokenInfo) mustEqual true
    }

    def e3 = {
      tokenizer.incrementToken(tokenInfo)
      tokenizer.incrementToken(tokenInfo)
      tokenInfo.token.toString mustEqual "world"
    }

    def e4 = {
      tokenizer.incrementToken(tokenInfo)
      tokenizer.incrementToken(tokenInfo) mustEqual true
    }

    def e5 = {
      tokenizer.incrementToken(tokenInfo)
      tokenizer.incrementToken(tokenInfo)
      tokenizer.incrementToken(tokenInfo) mustEqual false
    }
  }

  case class tokenizeJunk() {
    val tokenizer = RegexMatchTokenizer("\\w+".r, 0, "  !@#@$!@$ ")
    val tokenInfo = new TokenInfo

    def e1 = {
      tokenizer.incrementToken(tokenInfo) mustEqual false
    }

  }

  override def is =
    s2"""
          HelloWorld tokenization test should
           set "hello" as the first token         ${tokenizeHelloWorld().e1}
           and return true                        ${tokenizeHelloWorld().e2}
           set "world" as the second token        ${tokenizeHelloWorld().e3}
           and return true                        ${tokenizeHelloWorld().e4}
           then return false                      ${tokenizeHelloWorld().e5}

          Junk tokenization test should
           return false upon first run            ${tokenizeJunk().e1}
    """
}