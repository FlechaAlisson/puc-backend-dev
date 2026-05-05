package br.pucpr.authserver.tasks

import br.pucpr.authserver.projects.ProjectService
import br.pucpr.authserver.tasks.requests.CreateTaskRequest
import br.pucpr.authserver.tasks.requests.UpdateTaskRequest
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
@RequestMapping("/tasks")
class TaskController(
    val service: TaskService,
    val projectService: ProjectService
) {
    @GetMapping
    fun list(
        @RequestParam priority: TaskPriority? = null,
        @RequestParam projectId: Long? = null,
        @RequestParam assigneeId: Long? = null,
        @RequestParam sortBy: String? = null,
        @RequestParam sortDir: String? = null
    ) = service.findAll(
        priority = priority,
        projectId = projectId,
        assigneeId = assigneeId,
        sortBy = sortBy ?: "title",
        dir = SortDir.find(sortDir ?: "ASC")
    )
        .map { TaskResponse(it) }
        .let { ResponseEntity.ok(it) }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long) = service.findById(id)
        .let { TaskResponse(it) }
        .let { ResponseEntity.ok(it) }

    @SecurityRequirement(name = "jwt-auth")
    @PostMapping
    fun insert(
        @RequestParam projectId: Long? = null,
        @RequestParam assigneeId: Long? = null,
        @Valid @RequestBody task: CreateTaskRequest
    ) = service.insert(task.toTask(), projectId, assigneeId)
        .let { TaskResponse(it) }
        .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }

    @SecurityRequirement(name = "jwt-auth")
    @PatchMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody task: UpdateTaskRequest
    ): ResponseEntity<TaskResponse> =
        service.update(id, task.title!!, task.description!!, task.status!!, task.priority!!)
            ?.let { TaskResponse(it) }
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.noContent().build()

    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "jwt-auth")
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = service.delete(id)

    @SecurityRequirement(name = "jwt-auth")
    @PutMapping("/{taskId}/project/{projectId}")
    fun moveToProject(
        @PathVariable taskId: Long,
        @PathVariable projectId: Long
    ): ResponseEntity<Void> = projectService.addTask(projectId, taskId)
        .let {
            if (it) ResponseEntity.ok().build()
            else ResponseEntity.noContent().build()
        }

    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "jwt-auth")
    @DeleteMapping("/{taskId}/project")
    fun removeProject(@PathVariable taskId: Long): ResponseEntity<Void> =
        service.removeProject(taskId)
            .let {
                if (it) ResponseEntity.ok().build()
                else ResponseEntity.noContent().build()
            }

    @SecurityRequirement(name = "jwt-auth")
    @PutMapping("/{taskId}/assignee/{userId}")
    fun assignUser(
        @PathVariable taskId: Long,
        @PathVariable userId: Long
    ): ResponseEntity<Void> = service.assignUser(taskId, userId)
        .let {
            if (it) ResponseEntity.ok().build()
            else ResponseEntity.noContent().build()
        }

    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "jwt-auth")
    @DeleteMapping("/{taskId}/assignee")
    fun removeAssignee(@PathVariable taskId: Long): ResponseEntity<Void> =
        service.removeAssignee(taskId)
            .let {
                if (it) ResponseEntity.ok().build()
                else ResponseEntity.noContent().build()
            }
}
