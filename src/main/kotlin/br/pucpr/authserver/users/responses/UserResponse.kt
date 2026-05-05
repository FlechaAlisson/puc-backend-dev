package br.pucpr.authserver.users.responses

import br.pucpr.authserver.users.User

data class UserResponse(
    val id: Long,
    val email: String,
    val name: String,
    val roles: Set<String>,
) {
    constructor(user: User) : this(
        user.id!!,
        user.email,
        user.name,
        user.roles.map { it.name }.toSortedSet()
    )
}
