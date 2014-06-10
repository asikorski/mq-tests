package com.isaacloud.engine
import net.minidev.json.JSONObject
import com.isaacloud.Event
/**
 * Created by asikorski on 07.06.2014.
 */
private[engine] case class ExecuteEventScript(subject: SubjectCtx, event:EventCtx, script: String)
private[engine] case class PrepareEventExecutors(event:Event)

private[engine] case class ScriptException(message: String, operationId: Long)