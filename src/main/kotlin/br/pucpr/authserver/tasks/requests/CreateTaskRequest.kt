package br.pucpr.authserver.tasks.requests

import br.pucpr.authserver.tasks.Task
import br.pucpr.authserver.tasks.TaskPriority
import br.pucpr.authserver.tasks.TaskStatus
import jakarta.validation.constraints.NotBlank

data class CreateTaskRequest(
    @NotBlank
    val title: String?,

    @NotBlank
    val description: String?,

    val status: TaskStatus? = null,
    val priority: TaskPriority? = null
) {
    fun toTask() = Task(
        title = title!!,
        description = description!!,
        status = status ?: TaskStatus.OPEN,
        priority = priority ?: TaskPriority.MEDIUM
    )
}
