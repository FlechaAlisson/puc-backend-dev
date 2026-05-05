package br.pucpr.authserver.tasks.responses

import br.pucpr.authserver.tasks.Task
import br.pucpr.authserver.tasks.TaskPriority
import br.pucpr.authserver.tasks.TaskStatus
import br.pucpr.authserver.users.responses.UserSummaryResponse

data class TaskSummaryResponse(
    val id: Long,
    val title: String,
    val status: TaskStatus,
    val priority: TaskPriority,
    val assignee: UserSummaryResponse?
) {
    constructor(task: Task) : this(
        task.id!!,
        task.title,
        task.status,
        task.priority,
        task.assignee?.let { UserSummaryResponse(it) }
    )
}
