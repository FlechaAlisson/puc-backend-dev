package br.pucpr.authserver.tasks

import br.pucpr.authserver.exception.NotFoundException
import br.pucpr.authserver.exceptions.BadRequestException
import br.pucpr.authserver.projects.ProjectRepository
import br.pucpr.authserver.users.SortDir
import br.pucpr.authserver.users.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class TaskService(
    val repository: TaskRepository,
    val projectRepository: ProjectRepository,
    val userRepository: UserRepository
) {
    fun insert(task: Task, projectId: Long? = null, assigneeId: Long? = null): Task {
        task.project = projectId?.let {
            projectRepository.findByIdOrNull(it) ?: throw NotFoundException("Project not found. id=$it")
        }
        task.assignee = assigneeId?.let {
            userRepository.findByIdOrNull(it) ?: throw NotFoundException("User not found. id=$it")
        }

        val saved = repository.save(task)
        log.info("Task ${saved.id} created successfully")
        return saved
    }

    fun findAll(
        priority: TaskPriority? = null,
        projectId: Long? = null,
        assigneeId: Long? = null,
        sortBy: String = "title",
        dir: SortDir = SortDir.ASC
    ): List<Task> {
        val sort = sortBy.toTaskSort(dir)
        return repository.findAll(sort)
            .filter { priority == null || it.priority == priority }
            .filter { projectId == null || it.project?.id == projectId }
            .filter { assigneeId == null || it.assignee?.id == assigneeId }
    }

    fun findById(id: Long) = repository.findByIdOrNull(id) ?: throw NotFoundException("Task not found. id=$id")

    fun update(
        id: Long,
        title: String,
        description: String,
        status: TaskStatus,
        priority: TaskPriority
    ): Task? {
        val task = findById(id)
        if (
            task.title == title &&
            task.description == description &&
            task.status == status &&
            task.priority == priority
        ) {
            return null
        }

        task.title = title
        task.description = description
        task.status = status
        task.priority = priority
        val saved = repository.save(task)
        log.info("Task $id updated successfully")
        return saved
    }

    fun delete(id: Long) {
        val task = findById(id)
        repository.delete(task)
        log.info("Task $id deleted successfully")
    }

    fun removeProject(taskId: Long): Boolean {
        val task = findById(taskId)
        if (task.project == null) return false

        task.project = null
        repository.save(task)
        log.info("Task $taskId removed from its project")
        return true
    }

    fun assignUser(taskId: Long, userId: Long): Boolean {
        val task = findById(taskId)
        val user = userRepository.findByIdOrNull(userId) ?: throw NotFoundException("User not found. id=$userId")
        if (task.assignee?.id == user.id) return false

        task.assignee = user
        repository.save(task)
        log.info("Task $taskId assigned to user $userId")
        return true
    }

    fun removeAssignee(taskId: Long): Boolean {
        val task = findById(taskId)
        if (task.assignee == null) return false

        task.assignee = null
        repository.save(task)
        log.info("Assignee removed from task $taskId")
        return true
    }

    companion object {
        val log = LoggerFactory.getLogger(TaskService::class.java)

        private val sortFields = mapOf(
            "id" to "id",
            "title" to "title",
            "status" to "status",
            "priority" to "priority",
            "projectId" to "project.id",
            "assigneeId" to "assignee.id"
        )

        private fun String.toTaskSort(dir: SortDir): Sort {
            val field = sortFields[this] ?: throw BadRequestException("Invalid task sort field: $this")
            return when (dir) {
                SortDir.ASC -> Sort.by(field).ascending()
                SortDir.DESC -> Sort.by(field).descending()
            }
        }
    }
}
