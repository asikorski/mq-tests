import akka.actor.{Props, ActorSystem}

/**
 * Created by arnold on 10.06.2014.
 */
import com.github.sstone.amqp._
import com.github.sstone.amqp.Amqp._
import com.rabbitmq.client.ConnectionFactory
import com.github.sstone.amqp.Amqp.Publish
import java.util.concurrent.TimeUnit
import scala.concurrent.duration._
import akka.actor.{Props, Actor, ActorSystem}

object Hello {
  def main(args: Array[String]) {
    val system = ActorSystem("consumer")


    val connFactory1 = new ConnectionFactory()
    connFactory1.setUri("amqp://guest:guest@localhost/%2F")
    val conn1 = system.actorOf(ConnectionOwner.props(connFactory1, 1 second))

    // create an actor that will receive AMQP deliveries
    val listener = system.actorOf(Props(new Actor {
      def receive = {
        case Delivery(consumerTag, envelope, properties, body) => {
         // println("got a message: " + new String(body))
          sender ! Ack(envelope.getDeliveryTag)
        }
      }
    }))

    // create a consumer that will route incoming AMQP messages to our listener
    // it starts with an empty list of queues to consume from
    val consumer = ConnectionOwner.createChildActor(conn1, Consumer.props(listener, channelParams = None, autoack = false))

    // wait till everyone is actually connected to the broker
    Amqp.waitForConnection(system, consumer).await()

    // create a queue, bind it to a routing key and consume from it
    // here we don't wrap our requests inside a Record message, so they won't replayed when if the connection to
    // the broker is lost: queue and binding will be gone

    // create a queue
    val queueParams = QueueParameters("my_queue", passive = false, durable = false, exclusive = false, autodelete = true)
    consumer ! DeclareQueue(queueParams)
    // bind it
    consumer ! QueueBind(queue = "my_queue", exchange = "amq.direct", routing_key = "my_key")
    // tell our consumer to consume from it
    consumer ! AddQueue(QueueParameters(name = "my_queue", passive = false))

    // run the Producer sample now and see what happens

    //system.shutdown()

    //----
    val system1 = ActorSystem("sender")
    // create an AMQP connection
    val connFactory = new ConnectionFactory()
    connFactory.setUri("amqp://guest:guest@localhost/%2F")
    val conn = system1.actorOf(ConnectionOwner.props(connFactory, 1 second))
    val producer = ConnectionOwner.createChildActor(conn, ChannelOwner.props())

    // wait till everyone is actually connected to the broker
    waitForConnection(system, conn, producer).await(5, TimeUnit.SECONDS)

    // send a message
    while (true){
      producer ! Publish("amq.direct", "my_key", "yo!!".getBytes, properties = None, mandatory = true, immediate = false)
    }


    println("test")
    // give it some time before shutting everything down
    //Thread.sleep(1000000)
    system1.shutdown()

  }
}
