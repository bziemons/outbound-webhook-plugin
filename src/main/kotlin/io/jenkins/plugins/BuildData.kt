package io.jenkins.plugins

data class BuildData(
    val number: Int,
    val displayName: String,
    val fullDisplayName: String,
    val changes: List<ChangeData>,
    val result: ResultData?,
    val project: ProjectData
)

data class ProjectData(
    val fullName: String,
    val displayName: String,
    val fullDisplayName: String
)

data class ResultData(
    val type: String,
    val color: String
)

data class ChangeData(
    val message: String,
    val author: String,
    val timestamp: Long,
    val commitId: String
)
