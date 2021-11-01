package me.dantea.hazelcastcache.token.common

import java.io.Serializable

data class APIError(
    val code: String,
    val message: String
): Serializable