package chaitin.analysis

import org.junit.runner.RunWith
import org.specs2._
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
object LowercaseTokenFilterSpec extends Specification {

  case class transformHelloWorld() {
    val tokenFilter = LowercaseTokenFilter(RegexMatchTokenizer("\\w+".r, 0, "Hello, World!"))
    val tokenInfo = new TokenInfo
    def e1 = {
      tokenFilter.incrementToken(tokenInfo)
      tokenInfo.token.toString mustEqual "hello"
    }

    def e2 = {
      tokenFilter.incrementToken(tokenInfo) mustEqual true
    }

    def e3 = {
      tokenFilter.incrementToken(tokenInfo)
      tokenFilter.incrementToken(tokenInfo)
      tokenInfo.token.toString mustEqual "world"
    }

    def e4 = {
      tokenFilter.incrementToken(tokenInfo)
      tokenFilter.incrementToken(tokenInfo) mustEqual true
    }

    def e5 = {
      tokenFilter.incrementToken(tokenInfo)
      tokenFilter.incrementToken(tokenInfo)
      tokenFilter.incrementToken(tokenInfo) mustEqual false
    }
  }

  case class transformJunk() {
    val tokenFilter = LowercaseTokenFilter(RegexMatchTokenizer("\\w+".r, 0, "  !@#@$!@$ "))
    val tokenInfo = new TokenInfo

    def e1 = {
      tokenFilter.incrementToken(tokenInfo) mustEqual false
    }

  }

  override def is =
    s2"""
          HelloWorld lowercase test should
           set "hello" as the first token         ${transformHelloWorld().e1}
           and return true                        ${transformHelloWorld().e2}
           set "world" as the second token        ${transformHelloWorld().e3}
           and return true                        ${transformHelloWorld().e4}
           then return false                      ${transformHelloWorld().e5}

          Junk lowercase test should
           return false upon first run            ${transformJunk().e1}
    """
}