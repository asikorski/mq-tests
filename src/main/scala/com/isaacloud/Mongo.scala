package com.isaacloud

import com.mongodb.casbah.MongoClient

object Mongo {

  lazy val host = "localhost"
  lazy val port = 27017
  lazy val client = MongoClient(host, port)

  lazy val db = client("isaacloud")

  lazy val scripts = db("scripts")
  lazy val users = db("users")

  def apply(collection: String) = {

    collection match {
      case "scripts" => scripts
      case "users" => users
    }

  }
}