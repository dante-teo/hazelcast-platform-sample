package me.dantea.hazelcastcache.token.entity

import java.io.Serializable
import java.util.*

data class DToken(
    val value: String,
    val createdDate: Date
): Serializable