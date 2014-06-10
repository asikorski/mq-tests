package com.isaacloud

import java.lang.management.ManagementFactory

import akka.util.ByteString

import java.lang.System
import scala.concurrent.duration._
import akka.zeromq._
import akka.actor.Actor
import akka.actor.Props
import akka.actor.ActorLogging
import akka.serialization.SerializationExtension
import java.text._
import java.util.Date

case object Tick
final case class Heap(timestamp: Long, used: Long, max: Long)
final case class Load(timestamp: Long, loadAverage: Double)

class HealthProbe extends Actor {

  val pubSocket = ZeroMQExtension(context.system).newSocket(SocketType.Pub,
    Bind("tcp://127.0.0.1:1235"))
  val memory = ManagementFactory.getMemoryMXBean
  val os = ManagementFactory.getOperatingSystemMXBean
  val ser = SerializationExtension(context.system)
  import context.dispatcher

  override def preStart() {
    context.system.scheduler.schedule(1 second, 1 second, self, Tick)
  }

  override def postRestart(reason: Throwable) {
    // don't call preStart, only schedule once
  }

  def receive: Receive = {
    case Tick =>
      val currentHeap = memory.getHeapMemoryUsage
      val timestamp = System.currentTimeMillis

      // use akka SerializationExtension to convert to bytes
      val heapPayload = ser.serialize(Heap(timestamp, currentHeap.getUsed,
        currentHeap.getMax)).get
      // the first frame is the topic, second is the message
      pubSocket ! ZMQMessage(ByteString("health.heap"), ByteString(heapPayload))

      // use akka SerializationExtension to convert to bytes
      val loadPayload = ser.serialize(Load(timestamp, os.getSystemLoadAverage)).get
      // the first frame is the topic, second is the message
      pubSocket ! ZMQMessage(ByteString("health.load"), ByteString(loadPayload))
  }
}
class Logger extends Actor with ActorLogging {

  ZeroMQExtension(context.system).newSocket(SocketType.Sub, Listener(self),
    Connect("tcp://127.0.0.1:1235"), Subscribe("health"))
  val ser = SerializationExtension(context.system)
  val timestampFormat = new SimpleDateFormat("HH:mm:ss.SSS")

  def receive = {
    // the first frame is the topic, second is the message
    case m: ZMQMessage if m.frames(0).utf8String == "health.heap" =>
      val Heap(timestamp, used, max) = ser.deserialize(m.frames(1).toArray,
        classOf[Heap]).get
      log.info("Used heap {} bytes, at {}", used,
        timestampFormat.format(new Date(timestamp)))

    case m: ZMQMessage if m.frames(0).utf8String == "health.load" =>
      val Load(timestamp, loadAverage) = ser.deserialize(m.frames(1).toArray,
        classOf[Load]).get
      log.info("Load average {}, at {}", loadAverage,
        timestampFormat.format(new Date(timestamp)))
  }
}