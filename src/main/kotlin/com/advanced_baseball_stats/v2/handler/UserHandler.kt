package com.advanced_baseball_stats.v2.handler

import com.advanced_baseball_stats.v2.exception.DuplicateEmailException
import com.advanced_baseball_stats.v2.exception.InvalidEmailException
import com.advanced_baseball_stats.v2.model.users.AuthorizedUserInfo
import com.advanced_baseball_stats.v2.model.users.UserIdentifiers
import com.advanced_baseball_stats.v2.repository.users.UserSql

import io.ktor.util.toLowerCasePreservingASCIIRules

import org.mindrot.jbcrypt.BCrypt

import java.util.UUID

class UserHandler
{
    fun createUser(userIdentifiers: UserIdentifiers)
    {
        val normalizedEmail = normalizeEmail(userIdentifiers.email)

        val emailInTable = UserSql.checkIfEmailAlreadyExists(normalizedEmail)

        if (emailInTable != null)
        {
            throw DuplicateEmailException("Email already exists")
        }

        val salt = BCrypt.gensalt()

        val passwordHash = generatePasswordHash(userIdentifiers.password, salt)

        val userId = generateUserId()

        UserSql.addUser(userId, normalizedEmail, passwordHash, salt)
    }

    fun validateUser(userIdentifiers: UserIdentifiers): AuthorizedUserInfo?
    {
        val normalizedEmail = normalizeEmail(userIdentifiers.email)

        val userInfo = UserSql.getUserByEmail(normalizedEmail) ?: throw InvalidEmailException("Email does not exist")

        return if (validatePassword(userIdentifiers.password, userInfo.hashedPassword)) AuthorizedUserInfo(userInfo.userId, userInfo.userName) else null
    }

    private fun validatePassword(providedPassword: String, storedPassword: String): Boolean
    {
        return BCrypt.checkpw(providedPassword, storedPassword)
    }

    private fun generatePasswordHash(password: String, salt: String): String
    {
        return BCrypt.hashpw(password, salt)
    }

    private fun generateUserId(): String
    {
        val uuid: UUID = UUID.randomUUID()

        return uuid.toString()
    }

    private fun normalizeEmail(email: String): String
    {
        return email.toLowerCasePreservingASCIIRules()
    }
}