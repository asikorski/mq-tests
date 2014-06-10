# Isaacloud SDK for Scala

The Isaacloud Scala SDK can be used to access the IsaaCloud API through Scala. The user can make any number of request calls to the API.

## Requirements

The library works on **Java version of 1.7**, **Scala 2.11.0** or **Scala 2.10.x** and **sbt 0.13.x**.

## Basics

This SDK can be used to connect to the Isaacloud v1 REST API on api.isaacloud.com.
The main class in the "isaacloud" package is Isaacloud, which gives the possibility to connect to the public API. It has convenience methods for DELETE, GET, POST, PUT and PATCH methods. In the future it will also contain a wrapper which will offer all the methods defined by the Isaacloud RAML API.

You can also access the [scaladocs](http://isaacloud.github.io/scala-sdk).


It is important to note that **only one client id and one secret should be used by only one instance of Isaacloud SDK. Using mutiple instances will cause token refreshes and a serious decrease in efficiency.**


## How to build

## How to build it

1. Clone the repository:

    ```
    git clone https://github.com/isaacloud/scala-sdk.git
    ```

2. Enter the directory:

    ```
    cd scala-sdk
    ```

3. Run the installation of dependencies:

    ```
    sbt compile
    ```

    You can publish it to your local maven repository by using:

    ```
    sbt publish
    ```

    or you can add it as a dependency with build.scala. For example:

```scala
object Build extends Build {
    lazy val sdk = RootProject(uri("https://github.com/isaacloud/scala-sdk.git#%s".format("0.1.0")))
    lazy val defaultSettings =
        Defaults.defaultSettings ++
            Seq(
                name := "play_example",
                version := "1.0",
                scalaVersion := "2.10.1",
                scalacOptions := Seq(
                    "-feature",
                    "-language:implicitConversions",
                    "-language:postfixOps",
                    "-unchecked",
                    "-deprecation",
                    "-encoding", "utf8",
                    "-Ywarn-adapted-args"))

    lazy val root = Project("root",
        file("."),
        settings = defaultSettings ++ Seq(
        resolvers ++= Seq(
            "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/",
            "Maven repository" at "http://morphia.googlecode.com/svn/mavenrepo/"),
        libraryDependencies ++= Seq(
            "net.minidev" % "json-smart" % "1.1.1"
        )))
        .dependsOn(sdk)
}
```

## Overview

To make request calls, you can use the Isaacloud API class.To make a simple request, we first need to specify the path to a resource using the *path* method, then declare the query parameters and lastly use a specific REST method for acquiring the results.
Examples of calls:

```scala
val isaac = new Isaacloud(config=(":your_client_id:",":your_client_secret:"))
val limit = 11
val offset = 10
//get the list of users
val result = isaac.path("/cache/users").withFields("firstName","lastName").withPaginator(limit,offset).get()
//get one user
val result2 = isaac.path("/cache/users/1").withFields("firstName","lastName").withPaginator(limit,offset).get()
//or
val result3 = isaac.path("/cache/users/{userId}").withParameters(Map("userId" -> 1)).withFields("firstName","lastName").withPaginator(limit,offset).get()
```

The methods that start with the word *with* are responsible for narrowing the result set. Each one changes the way the result will be returned by the method. You can combine multiple methods in order to get the desired effect.
In methods without a certain trait it will be ignored. The list of the methods is presented below:

* withFields - narrows the result set to contain only json fields which are in the list of the method

    ```scala
    isaac.path("/cache/users").withFields("firstName","lastName")  
    //returns the users' first and last name only
    ```

* withPaginator - limits the number and defines the offset for the results, works only with list resources

    ```scala
    isaac.path("/cache/users").withPaginator(10l,5l)  
    //returns 5 elements starting with the tenth
    ```

* withGroups - returns only the resources containing groups' ids in the list

    ```scala
    isaac.path("/cache/users").withGroups(1l,2l,3l)  
    //returns only the users in groups 1 or 2 or 3
    ```

* withIds - returns only the resources containing those ids in the list

    ```scala
    isaac.path("/cache/users").withIds(1l,2l,3l)  //returns only the users with id 1 or 2 or 3
    ```

* withSegments - returns only the resources containing segments' ids in the list

    ```scala
    isaac.path("/cache/users").withSegments(1l,2l,3l)  
    //returns only the users in segments 1 or 2 or 3
    ```

* withOrder - declares the order in which the results in list resources should be returned

    ```scala
    isaac.path("/cache/users").withOrder(("firstName","ASC"),("lastName","DESC"))  
    //returns results sorted first by firstName ascending and then by lastName descending
    ```

* withCreatedAt - returns only the resources created between certain dates given in milliseconds. If one of the parameters is None, the limit is not set.

    ```scala
    isaac.path("/cache/users").withCreatedAt(Some(1398157190540),None)  
    //returns only the users created after Tue Apr 22 2014 8:59:50 AM
    ```

* withUpdatedAt - returns only the resources last updated between certain dates given in milliseconds. If one of the parameters is None, the limit is not set.

    ```scala
    isaac.path("/cache/users").withUpdatedAt(None, Some(1398157190540))  
    //returns only the users last updated before Tue Apr 22 2014 8:59:50 AM
    ```

* withCustom - shows custom fields in the result

    ```scala
    isaac.path("/admin/users").withCustom  
    //returns all custom fields
    ```

* withCustoms - declares exactly which fields in custom fields should be shown.

    ```scala
    isaac.path("/admin/users").withCustoms("shoeSize","weight")
    //returns only custom fields with keys shoeSize and weight
    ```

* withQuery - performs a search with specific field values.
    ```scala
    isaac.path("/admin/users").withQuery("wonGames.amount" -> 12, "wonGames.game" -> 1)  
    //returns only users with game 1 won 12 times
     ```

* withQueryParameters - adds query parameters manually.
    ```scala
    isaac.path("/cache/users").withQueryParameters(Map("fields" -> List("firstName","lastName"))  
    //returns the users' first and last name only
     ```

There are two ways to access the result of the request. Depending on the expected form of the result (a single JSON object or a JSON array), the user can use:

```scala
val obj = result.getJson match{
    case Right(v) => v
}
```

or

```scala
val array = result.getJson match{
    case Left(v) => v
}
```

you also can match the type of the response:

```scala
val obj = result match{
    case SimpleResponse(v) => v
}
```

and

```scala
val array = result match{
    case ListResponse(v, paginator) => v
}
```


If no detailed exception handling is required, you can simply catch the basic IsaacloudConnectionException. If more detailed information about the error is needed, there are several exception classes that extend the general IsaacloudConnectionException case class. Catch the detailed exception before the general one. Check the isaacloud package for more details on available exceptions. Each of these exceptions can return an internal error code and message via the *internalCode* and *message* methods. Reviewing these values will give you further insight on what went wrong.

For detailed information about the possible URI calls, available query parameters and request methods please see our documentation:
https://isaacloud.com/documentation

## Additional examples

### Send events

There is an additional method in the Isaacloud class used for creating events:

```scala

    val body = new JSONObject()
    body.put("destination","JFK")
    body.put("distance","2000")

    val isaac = new Isaacloud(config=(":your_client_id:",":your_client_secret:"))
    isaac.event(1,"USER",1,"NORMAL","PRIORITY_NORMAL",body)
```

The method takes the subject id (the id of the group or user it relates to), the subject type (USER or GROUP), the event type, the priority of the event and body for the event.
Most of this information can be found in scaladocs.

### Get one user from cache

To get one user, use the get method:

```scala
    val isaac = new Isaacloud(config=(":your_client_id:",":your_client_secret:"))
    val id = 1

    val user : JSONObject = isaac.path("cache/users/"+id).get().getJson() match{
        case Left(v) => v
    }
```

### Get the list of users from cache

To get the list of users, use the get method:

```scala
    val isaac = new Isaacloud(config=(":your_client_id:",":your_client_secret:"))

    val users : JSONArray = isaac.path("cache/users").get().getJson() match{
        case Right(v) => v
    }
```

### Create a user

To create a user, use the post method:

```scala
    val user = new JSONObject()
    user.put("birthDate", "1979-10-22")
    user.put("email","dbrown@example.com")
    user.put("password","M0nKey_busine$s")
    user.put("firstName","Dan")
    user.put("lastName","Brown")

    val isaac = new Isaacloud(config=(":your_client_id:",":your_client_secret:"))

    val user : JSONObject = isaac.path("admin/users").post(user).getJson() match{
           case Left(v) => v
    }
```

### Update a user

To update a user, use the put method:

```scala
    val user = new JSONObject()
    user.put("firstName","Bob")
    user.put("lastName","Blue")

    val isaac = new Isaacloud(config=(":your_client_id:",":your_client_secret:"))
    val id = 1

    val user : JSONObject = isaac.path("admin/users/" + id).put(user).getJson() match {
           case Left(v) => v
    }
```

### Delete a user

To delete a user, use the delete method:

```scala
    val isaac = new Isaacloud(config=(":your_client_id:",":your_client_secret:"))
    val id = 1

    isaac.path("admin/users/" + id).delete()
```

