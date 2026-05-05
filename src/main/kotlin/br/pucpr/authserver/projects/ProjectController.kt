package br.pucpr.authserver.projects

import br.pucpr.authserver.projects.requests.CreateProjectRequest
import br.pucpr.authserver.projects.requests.UpdateProjectRequest
import br.pucpr.authserver.projects.responses.ProjectResponse
import br.pucpr.authserver.tasks.requests.CreateTaskRequest
import br.pucpr.authserver.tasks.responses.TaskResponse
import br.pucpr.authserver.users.SortDir
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/projects")
class ProjectController(val service: ProjectService) {
    @GetMapping
    fun list(
        @RequestParam name: String? = null,
        @RequestParam sortBy: String? = null,
        @RequestParam sortDir: String? = null
    ) = service.findAll(
        name = name,
        sortBy = sortBy ?: "name",
        dir = SortDir.find(sortDir ?: "ASC")
    )
        .map { ProjectResponse(it) }
        .let { ResponseEntity.ok(it) }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long) = service.findById(id)
        .let { ProjectResponse(it) }
        .let { ResponseEntity.ok(it) }

    @SecurityRequirement(name = "jwt-auth")
    @PostMapping
    fun insert(
        @Valid @RequestBody project: CreateProjectRequest
    ) = service.insert(project.toProject())
        .let { ProjectResponse(it) }
        .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }

    @SecurityRequirement(name = "jwt-auth")
    @PatchMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody project: UpdateProjectRequest
    ): ResponseEntity<ProjectResponse> =
        service.update(id, project.name!!, project.description!!)
            ?.let { ProjectResponse(it) }
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.noContent().build()

    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "jwt-auth")
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = service.delete(id)

    @SecurityRequirement(name = "jwt-auth")
    @PostMapping("/{projectId}/tasks")
    fun createTask(
        @PathVariable projectId: Long,
        @RequestParam assigneeId: Long? = null,
        @Valid @RequestBody task: CreateTaskRequest
    ) = service.createTask(projectId, task.toTask(), assigneeId)
        .let { TaskResponse(it) }
        .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }

    @SecurityRequirement(name = "jwt-auth")
    @PutMapping("/{projectId}/tasks/{taskId}")
    fun addTask(
        @PathVariable projectId: Long,
        @PathVariable taskId: Long
    ): ResponseEntity<Void> = service.addTask(projectId, taskId)
        .let {
            if (it) ResponseEntity.ok().build()
            else ResponseEntity.noContent().build()
        }

    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "jwt-auth")
    @DeleteMapping("/{projectId}/tasks/{taskId}")
    fun removeTask(
        @PathVariable projectId: Long,
        @PathVariable taskId: Long
    ): ResponseEntity<Void> = service.removeTask(projectId, taskId)
        .let {
            if (it) ResponseEntity.ok().build()
            else ResponseEntity.noContent().build()
        }
}
