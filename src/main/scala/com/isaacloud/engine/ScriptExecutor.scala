package com.isaacloud.engine

import net.minidev.json.{JSONArray, JSONObject}

import akka.actor.{ActorRef, Actor}
import scala.collection.JavaConverters._
import scala.util.{Try, Failure, Success}

import scala.util.Failure
import scala.Some
import scala.util.Success

private[engine] class ScriptExecutor(val manager: ActorRef) extends Actor {

  def receive = {
    case ExecuteEventScript(subject: SubjectCtx, event:EventCtx, script: String) =>

  }
}
