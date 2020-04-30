package io.jenkins.plugins

import hudson.Extension
import hudson.Launcher
import hudson.model.AbstractBuild
import hudson.model.AbstractProject
import hudson.model.BuildListener
import hudson.tasks.BuildStepDescriptor
import hudson.tasks.BuildStepMonitor
import hudson.tasks.Notifier
import hudson.tasks.Publisher
import org.kohsuke.stapler.DataBoundConstructor
import java.io.IOException
import java.lang.RuntimeException

@Suppress("unused")
class WebHookPublisher @DataBoundConstructor
constructor(
    private val webhookUrl: String,
    private val webhookSecret: String,
    private val failOnError: Boolean) : Notifier() {

    fun getWebhookUrl(): String {
        return webhookUrl
    }

    fun getWebhookSecret(): String {
        return webhookSecret
    }

    fun getFailOnError(): Boolean {
        return failOnError
    }

    override fun getRequiredMonitorService(): BuildStepMonitor {
        return BuildStepMonitor.NONE
    }

    override fun perform(build: AbstractBuild<*, *>, launcher: Launcher, listener: BuildListener): Boolean {
        try {
            return WebHookExecutor.action(listener, webhookUrl, webhookSecret, failOnError, build)
        } catch (error: RuntimeException) {
            if (failOnError) {
                throw error
            } else {
                listener.logger.println("Suppressed RuntimeException on WebHookExecutor.action")
                error.printStackTrace(listener.logger)
            }
        } catch (error: IOException) {
            if (failOnError) {
                throw error
            } else {
                listener.logger.println("Suppressed IOException on WebHookExecutor.action")
                error.printStackTrace(listener.logger)
            }
        }
        return true
    }

    override fun getDescriptor(): WebHookPublisherDescriptor {
        return super.getDescriptor() as WebHookPublisherDescriptor
    }

    @Extension
    class WebHookPublisherDescriptor : BuildStepDescriptor<Publisher>() {

        override fun isApplicable(jobType: Class<out AbstractProject<*, *>>): Boolean {
            return true
        }

        override fun getDisplayName(): String {
            return "Outbound WebHook notification"
        }
    }
}
