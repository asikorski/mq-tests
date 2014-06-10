import akka.actor.{Props, ActorSystem}
import com.isaacloud.{Logger, HealthProbe}

/**
 * Created by arnold on 10.06.2014.
 */
object Hello {
  def main(args: Array[String]) {
    val system = ActorSystem()
    system.actorOf(Props[HealthProbe], name = "health")
    system.actorOf(Props[Logger], name = "logger")
    println("Hello, world!")
  }
}