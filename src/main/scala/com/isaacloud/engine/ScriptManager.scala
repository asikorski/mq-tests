package com.isaacloud.engine
import akka.actor.{PoisonPill, ActorRef, Actor, Props}
import akka.routing.RoundRobinRouter
import com.isaacloud.{Mongo, Event}
import com.mongodb.casbah.commons.{MongoDBList, MongoDBObject}
import com.mongodb.DBObject
import org.bson.types.ObjectId

/**
 * Created by asikorski on 08.06.2014.
 */
class ScriptManager extends Actor{

  val executors = context.actorOf(Props(classOf[ScriptExecutor], self).withRouter(RoundRobinRouter(nrOfInstances = 5)), name = "EventExecutors")
  val scripts = Mongo("scripts")
  val users = Mongo("users")

  def receive = {
    case PrepareEventExecutors(event:Event) =>{

      users.findOne(MongoDBObject("_id" -> new ObjectId(event.subject))) match {
        case Some(subject) =>
          // found subject
          //scripts.find()
        case None =>
          //notfound subject
      }
    }
  }
}
