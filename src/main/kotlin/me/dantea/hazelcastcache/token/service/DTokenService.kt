package me.dantea.hazelcastcache.token.service

import me.dantea.hazelcastcache.token.entity.DToken
import reactor.core.publisher.Mono
import java.util.*

interface DTokenService {
    fun generateToken(): DToken
    fun currentToken(): Optional<DToken>
    fun hashedToken(): Optional<String>
}