package io.jenkins.plugins

import com.coravy.hudson.plugins.github.GithubProjectProperty
import com.google.gson.Gson
import hudson.model.AbstractBuild
import hudson.model.BuildListener
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

object WebHookExecutor {
    private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaTypeOrNull()
    private val HTTP_CLIENT = OkHttpClient()
    private val GSON = Gson()

    fun action(listener: BuildListener,
               webhookUrl: String,
               webhookSecret: String,
               failOnError: Boolean,
               build: AbstractBuild<*, *>): Boolean {
        val request = Request.Builder()
            .url(webhookUrl)
            .header("X-Jenkins-Token", webhookSecret)
            .header("X-Jenkins-Event", "Post Build Hook")
            .post(GSON.toJson(build.asBuildData()).toRequestBody(JSON_MEDIA_TYPE))
            .build()

        listener.logger.println("Triggering webhook ${request.url}")
        return HTTP_CLIENT.newCall(request).execute().use { response ->
            listener.logger.println("Webhook replied ${response.code} - ${response.message}")
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
    val resultData = getResult()?.run {
        ResultData(
            type = toExportedObject(),
            color = color.htmlBaseColor
        )
    }
    val myProject = getProject()
    val githubProjectProperty = myProject.getProperty(GithubProjectProperty::class.java)
    return BuildData(
        number = getNumber(),
        displayName = getDisplayName(),
        fullDisplayName = getFullDisplayName(),
        githubProjectUrl = githubProjectProperty?.projectUrl?.baseUrl(),
        changes = sortedChanges,
        result = resultData,
        project = ProjectData(
            fullName = myProject.getFullName(),
            displayName = myProject.getDisplayName(),
            fullDisplayName = myProject.getFullDisplayName()
        )
    )
}
