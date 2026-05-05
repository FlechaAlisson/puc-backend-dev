package br.pucpr.authserver.projects.responses

import br.pucpr.authserver.projects.Project

data class ProjectSummaryResponse(
    val id: Long,
    val name: String
) {
    constructor(project: Project) : this(project.id!!, project.name)
}
