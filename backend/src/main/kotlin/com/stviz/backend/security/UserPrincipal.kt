package com.stviz.backend.security

import com.stviz.backend.user.UserEntity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserPrincipal(
    val id: Long,
    val email: String,
    private val pass: String,
    private val authorities: Collection<GrantedAuthority>
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> = authorities
    override fun getPassword(): String = pass
    override fun getUsername(): String = email

    companion object {
        fun create(user: UserEntity): UserPrincipal {
            return UserPrincipal(
                id = user.id,
                email = user.email,
                pass = user.passwordHash,
                authorities = emptyList()
            )
        }
    }
}
