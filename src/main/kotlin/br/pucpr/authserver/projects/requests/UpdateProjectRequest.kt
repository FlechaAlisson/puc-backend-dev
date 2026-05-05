package br.pucpr.authserver.projects.requests

import jakarta.validation.constraints.NotBlank

data class UpdateProjectRequest(
    @NotBlank
    val name: String?,

    @NotBlank
    val description: String?
)
