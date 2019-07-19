package io.jenkins.plugins

import com.google.gson.Gson
import hudson.model.AbstractBuild
import hudson.model.BuildListener
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

object WebHookExecutor {
    private val JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8")
    private val HTTP_CLIENT = OkHttpClient()
    private val GSON = Gson()

    fun action(listener: BuildListener,
               webhookUrl: String,
               webhookSecret: String,
               failOnError: Boolean,
               build: AbstractBuild<*, *>): Boolean {
        build.asBuildData()

        val request = Request.Builder()
            .url(webhookUrl)
            .header("X-Jenkins-Token", webhookSecret)
            .header("X-Jenkins-Event", "Post Build Hook")
            .post(RequestBody.create(JSON_MEDIA_TYPE, GSON.toJson(build.asBuildData())))
            .build()
        listener.logger.println("Triggering webhook ${request.url()}")

        return HTTP_CLIENT.newCall(request).execute().use { response ->
            listener.logger.println("Webhook replied ${response.code()} - ${response.message()}")
            return@use !failOnError || response.isSuccessful
        }
    }
}

private fun AbstractBuild<*, *>.asBuildData(): BuildData {
    val sortedChanges = getChangeSet()
        .sortedBy { it.timestamp }
        .map {
            ChangeData(
                message = it.msgEscaped,
                author = it.author.fullName,
                timestamp = it.timestamp,
                commitId = it.commitId
            )
        }
    val result = getResult()?.run {
        ResultData(
            type = toExportedObject(),
            color = color.htmlBaseColor
        )
    }
    val project = getProject()
    return BuildData(
        number = number,
        displayName = getDisplayName(),
        fullDisplayName = getFullDisplayName(),
        changes = sortedChanges,
        result = result,
        project = ProjectData(
            fullName = project.getFullName(),
            displayName = project.getDisplayName(),
            fullDisplayName = project.getFullDisplayName()
        )
    )
}
