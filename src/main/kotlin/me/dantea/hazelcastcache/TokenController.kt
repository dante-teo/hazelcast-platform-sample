package me.dantea.hazelcastcache

import com.hazelcast.core.HazelcastInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/api/v1/token")
class TokenController {
    @Autowired
    @Qualifier("hazelcastInstance")
    lateinit var hazelcast: HazelcastInstance

    @PostMapping
    fun create(): Mono<String> {
        val newToken = UUID.randomUUID().toString()
        val map = hazelcast.getMap<String, String>("shared")
        map.put("token", newToken)
        return Mono.just(newToken)
    }

    @GetMapping
    fun index(): Mono<String> {
        val map = hazelcast.getMap<String, String>("shared")
        val token = map.get("token")
        return if (token.isNullOrBlank()) Mono.just("Not Found!") else Mono.just(token)
    }

    @GetMapping("/hash")
    fun getHashValue(): Mono<String> {
        val map = hazelcast.getMap<String, String>("shared")
        val hashed = map.get("hashed")
        return if (hashed.isNullOrBlank()) Mono.just("Not Found!") else Mono.just(hashed)
    }
}