package br.pucpr.authserver.tasks.responses

import br.pucpr.authserver.projects.responses.ProjectSummaryResponse
import br.pucpr.authserver.tasks.Task
import br.pucpr.authserver.tasks.TaskPriority
import br.pucpr.authserver.tasks.TaskStatus
import br.pucpr.authserver.users.responses.UserSummaryResponse

data class TaskResponse(
    val id: Long,
    val title: String,
    val description: String,
    val status: TaskStatus,
    val priority: TaskPriority,
    val project: ProjectSummaryResponse?,
    val assignee: UserSummaryResponse?
) {
    constructor(task: Task) : this(
        task.id!!,
        task.title,
        task.description,
        task.status,
        task.priority,
        task.project?.let { ProjectSummaryResponse(it) },
        task.assignee?.let { UserSummaryResponse(it) }
    )
}
