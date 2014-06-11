import akka.actor.{Props, ActorSystem}

/**
 * Created by arnold on 10.06.2014.
 */
object Hello {
  def main(args: Array[String]) {
    val system = ActorSystem()

    println("Hello, world!")
  }
}