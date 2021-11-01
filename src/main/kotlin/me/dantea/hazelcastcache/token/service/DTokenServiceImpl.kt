package me.dantea.hazelcastcache.token.service

import com.hazelcast.core.HazelcastInstance
import me.dantea.hazelcastcache.token.common.HASHED_MAP_NAME
import me.dantea.hazelcastcache.token.common.HASHED_MAP_VALUE_KEY
import me.dantea.hazelcastcache.token.common.SHARED_MAP_NAME
import me.dantea.hazelcastcache.token.common.SHARED_MAP_TOKEN_KEY
import me.dantea.hazelcastcache.token.entity.DToken
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import java.util.*

@Service
class DTokenServiceImpl(
    @Qualifier("hazelcastInstance") val hazelcast: HazelcastInstance
): DTokenService {
    override fun generateToken(): DToken {
        val newToken = DToken(UUID.randomUUID().toString(), Date())
        hazelcast
            .getMap<String, DToken>(SHARED_MAP_NAME)
            .put(SHARED_MAP_TOKEN_KEY, newToken)

        return newToken
    }

    override fun currentToken(): Optional<DToken> {
        val token = hazelcast
            .getMap<String, DToken>(SHARED_MAP_NAME)
            .get(SHARED_MAP_TOKEN_KEY)

        return Optional.ofNullable(token)
    }

    override fun hashedToken(): Optional<String> {
        val hashed = hazelcast
            .getMap<String, String>(HASHED_MAP_NAME)
            .get(HASHED_MAP_VALUE_KEY)

        return Optional.ofNullable(hashed)
    }
}