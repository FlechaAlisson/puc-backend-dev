package br.pucpr.authserver.projects

import br.pucpr.authserver.tasks.Task
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "ProjectTable")
class Project(
    @Id @GeneratedValue
    var id: Long? = null,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var description: String = "",

    @OneToMany(mappedBy = "project", cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    var tasks: MutableSet<Task> = mutableSetOf()
)
