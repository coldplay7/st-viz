package com.stviz.backend.user

import com.stviz.backend.common.exception.ValidationException
import com.stviz.backend.security.JwtService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService
) {
    @Transactional
    fun register(request: RegisterRequest): AuthResponse {
        if (userRepository.existsByEmail(request.email)) {
            throw ValidationException("Email is already in use")
        }
        if (userRepository.existsByUsername(request.username)) {
            throw ValidationException("Username is already taken")
        }

        val user = UserEntity(
            username = request.username,
            email = request.email,
            passwordHash = passwordEncoder.encode(request.password)
        )

        val savedUser = userRepository.save(user)
        val token = jwtService.generateToken(savedUser.id, savedUser.email)

        return AuthResponse(
            token = token,
            username = savedUser.username,
            email = savedUser.email
        )
    }

    fun login(request: LoginRequest): AuthResponse {
        val user = userRepository.findByEmail(request.email)
            ?: throw com.stviz.backend.common.exception.UnauthorizedException("Invalid email or password")

        if (!passwordEncoder.matches(request.password, user.passwordHash)) {
            throw com.stviz.backend.common.exception.UnauthorizedException("Invalid email or password")
        }

        val token = jwtService.generateToken(user.id, user.email)

        return AuthResponse(
            token = token,
            username = user.username,
            email = user.email
        )
    }
}
