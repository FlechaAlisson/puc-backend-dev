package br.pucpr.authserver.projects.responses

import br.pucpr.authserver.projects.Project
import br.pucpr.authserver.tasks.responses.TaskSummaryResponse

data class ProjectResponse(
    val id: Long,
    val name: String,
    val description: String,
    val tasks: List<TaskSummaryResponse>
) {
    constructor(project: Project) : this(
        project.id!!,
        project.name,
        project.description,
        project.tasks
            .sortedBy { it.title }
            .map { TaskSummaryResponse(it) }
    )
}
