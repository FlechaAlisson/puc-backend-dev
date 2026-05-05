package br.pucpr.authserver.projects

import br.pucpr.authserver.exception.NotFoundException
import br.pucpr.authserver.exceptions.BadRequestException
import br.pucpr.authserver.tasks.Task
import br.pucpr.authserver.tasks.TaskRepository
import br.pucpr.authserver.users.SortDir
import br.pucpr.authserver.users.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ProjectService(
    val repository: ProjectRepository,
    val taskRepository: TaskRepository,
    val userRepository: UserRepository
) {
    fun insert(project: Project): Project {
        val saved = repository.save(project)
        log.info("Project ${saved.id} created successfully")
        return saved
    }

    fun findAll(
        name: String? = null,
        sortBy: String = "name",
        dir: SortDir = SortDir.ASC
    ): List<Project> {
        val sort = sortBy.toProjectSort(dir)
        return repository.findAll(sort)
            .filter { name == null || it.name.contains(name, ignoreCase = true) }
    }

    fun findById(id: Long) = repository.findByIdOrNull(id) ?: throw NotFoundException("Project not found. id=$id")

    fun update(id: Long, name: String, description: String): Project? {
        val project = findById(id)
        if (project.name == name && project.description == description) {
            return null
        }

        project.name = name
        project.description = description
        val saved = repository.save(project)
        log.info("Project $id updated successfully")
        return saved
    }

    fun delete(id: Long) {
        val project = findById(id)
        project.tasks.forEach { it.project = null }
        taskRepository.saveAll(project.tasks)
        repository.delete(project)
        log.info("Project $id deleted successfully")
    }

    fun createTask(projectId: Long, task: Task, assigneeId: Long? = null): Task {
        val project = findById(projectId)
        task.project = project
        task.assignee = assigneeId?.let {
            userRepository.findByIdOrNull(it) ?: throw NotFoundException("User not found. id=$it")
        }
        val saved = taskRepository.save(task)
        log.info("Task ${saved.id} created in project $projectId")
        return saved
    }

    fun addTask(projectId: Long, taskId: Long): Boolean {
        val project = findById(projectId)
        val task = taskRepository.findByIdOrNull(taskId) ?: throw NotFoundException("Task not found. id=$taskId")
        if (task.project?.id == project.id) return false

        task.project = project
        taskRepository.save(task)
        log.info("Task $taskId associated with project $projectId")
        return true
    }

    fun removeTask(projectId: Long, taskId: Long): Boolean {
        val task = taskRepository.findByIdOrNull(taskId) ?: throw NotFoundException("Task not found. id=$taskId")
        if (task.project?.id != projectId) return false

        task.project = null
        taskRepository.save(task)
        log.info("Task $taskId removed from project $projectId")
        return true
    }

    companion object {
        val log = LoggerFactory.getLogger(ProjectService::class.java)

        private val sortFields = mapOf(
            "id" to "id",
            "name" to "name",
            "description" to "description"
        )

        private fun String.toProjectSort(dir: SortDir): Sort {
            val field = sortFields[this] ?: throw BadRequestException("Invalid project sort field: $this")
            return when (dir) {
                SortDir.ASC -> Sort.by(field).ascending()
                SortDir.DESC -> Sort.by(field).descending()
            }
        }
    }
}
