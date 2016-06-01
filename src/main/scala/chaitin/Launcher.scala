package chaitin

import java.io.{Closeable, File, PrintWriter}
import java.lang.management.ManagementFactory

import chaitin.server.{RootServer, Server}

object Launcher extends App {

  try {
    writePID("/tmp/chaitin.pid")
    
    val server = new RootServer(args(0))

    closeOnExit(server)
    server.bind()
  } catch {
    case e: Throwable =>
      throw e
  }

  private[this] def writePID(destPath: String) = {
    def pid(fallback: String) = {
      val jvmName = ManagementFactory.getRuntimeMXBean.getName
      val index = jvmName indexOf '@'
      if (index > 0) {
        try {
          jvmName.substring(0, index).toLong.toString
        } catch {
          case e: NumberFormatException ⇒ fallback
        }
      } else fallback
    }

    val pidFile = new File(destPath)
    if (pidFile.createNewFile) {
      (new PrintWriter(pidFile) append pid("<Unknown-PID>")).close()
      pidFile.deleteOnExit()
      println("pid file: " + destPath)
      true
    } else {
      println("unable to write pid file, exiting.")
      System exit 1
      false
    }
  }

  private[this] def closeOnExit(closeable: Closeable) {
    Runtime.getRuntime addShutdownHook new Thread {
      override def run() = {
        try {
          closeable.close
        }
        finally {
          println
        }
      }
    }
  }

}

