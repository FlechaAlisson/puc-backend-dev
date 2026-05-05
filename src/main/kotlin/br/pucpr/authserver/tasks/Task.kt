package br.pucpr.authserver.tasks

import br.pucpr.authserver.projects.Project
import br.pucpr.authserver.users.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "TaskTable")
class Task(
    @Id @GeneratedValue
    var id: Long? = null,

    @Column(nullable = false)
    var title: String,

    @Column(nullable = false)
    var description: String = "",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: TaskStatus = TaskStatus.OPEN,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var priority: TaskPriority = TaskPriority.MEDIUM,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    var project: Project? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    var assignee: User? = null
)
