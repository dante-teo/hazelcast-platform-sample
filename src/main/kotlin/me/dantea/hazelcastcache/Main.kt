package me.dantea.hazelcastcache

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class Main

fun main(args: Array<String>) {
    runApplication<Main>(*args)
}