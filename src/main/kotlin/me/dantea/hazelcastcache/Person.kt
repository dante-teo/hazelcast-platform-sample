package me.dantea.hazelcastcache

import java.io.Serializable

data class Person(
    val id: Long,
    val surname: String,
    val givenName: String,
    val city: String
): Serializable