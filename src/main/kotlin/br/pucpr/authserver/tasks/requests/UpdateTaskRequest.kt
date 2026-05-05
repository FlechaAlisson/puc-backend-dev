package br.pucpr.authserver.tasks.requests

import br.pucpr.authserver.tasks.TaskPriority
import br.pucpr.authserver.tasks.TaskStatus
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class UpdateTaskRequest(
    @NotBlank
    val title: String?,

    @NotBlank
    val description: String?,

    @NotNull
    val status: TaskStatus?,

    @NotNull
    val priority: TaskPriority?
)
