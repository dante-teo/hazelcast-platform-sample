package me.dantea.hazelcastcache.token

import me.dantea.hazelcastcache.token.common.APIResponse
import me.dantea.hazelcastcache.token.entity.DToken
import me.dantea.hazelcastcache.token.service.DTokenService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/token")
class DTokenController(
    val service: DTokenService
) {
    @GetMapping
    fun index() = service
        .currentToken()
        .map { APIResponse.success(it) }
        .orElse(APIResponse.failure<DToken>("404", "Token not found!"))

    @PostMapping
    fun create() = service.generateToken()

    @GetMapping("/hash")
    fun getHashedValue() = service
        .hashedToken()
        .map { APIResponse.success(it) }
        .orElse(APIResponse.failure("404", "Hashed value not found!"))
}