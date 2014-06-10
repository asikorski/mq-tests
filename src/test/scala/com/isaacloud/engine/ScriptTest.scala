package com.isaacloud.engine

import com.isaacloud.{Event, Mongo}
import net.minidev.json.JSONObject
import org.scalatest.FunSuite
import org.scalatest.mock.MockitoSugar
import org.mozilla.javascript._
import com.mongodb.casbah.commons.MongoDBObject


import com.mongodb.casbah.Imports._
import java.util.Date
import com.mongodb.DBObject
import org.bson.types.ObjectId

class ScriptTest extends FunSuite with MockitoSugar {


  test("test") {
    assert(true)
  }
  test("rihno basic test") {
    val ctx= Context.enter
    val scope = ctx.initStandardObjects()
    val subject = Context.javaToJS(new SubjectCtx, scope)

    ScriptableObject.putProperty(scope, "subject", subject);
    val t = System.currentTimeMillis();

    val result = ctx.evaluateString(scope, "subject.test()", "<cmd>", 1, null)
    //
    println((System.currentTimeMillis()-t))
    println(Context.toString(result))
    assert(true)
  }
  test("mongo connection"){
    val scripts = Mongo("scripts")
    scripts.findOne(MongoDBObject("_id" -> new ObjectId("53933d4be48ecd4dd1aea4f6"))) match {
      case Some(member) =>
        member.get("configuration").asInstanceOf[DBObject]
      case None =>
        val now = new Date().getTime
        val newConfig = MongoDBObject()
        //scripts.insert(MongoDBObject("datastore" -> MongoDBList(), "configuration" -> newConfig, "id" -> 10, "updatedAt" -> now, "createdAt" -> now))
    }

  }
  test("getting subjects"){
    val event = Event("53942ad4e48ecd4dd1aea4f6")
    val users = Mongo("users")
    val scripts = Mongo("scripts")

    users.findOne(MongoDBObject("_id" -> new ObjectId(event.subject))) match {
      case Some(subject) =>
        val subjectCtx = new SubjectCtx()
        val eventCtx = new EventCtx()

        val scrs = scripts.find().toList
        scrs.foreach { scr =>
          val eventMsg = ExecuteEventScript(subjectCtx,eventCtx,scr.get("code").toString)
            println(scr.get("code").toString)
        }
        println(subject)
      //scripts.find()
      case None =>
        println("test")
      //notfound subject
    }
  }
}
