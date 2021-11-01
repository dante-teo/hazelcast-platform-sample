package me.dantea.hazelcastcache

import com.hazelcast.core.HazelcastInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/api/v1/people")
class PeopleController {
    @Autowired
    @Qualifier("hazelcastInstance")
    lateinit var hazelcast: HazelcastInstance

    @GetMapping
    fun index(): Flux<Person> {
        val people = hazelcast.getList<Person>("people")
        return Flux.fromArray(people.toTypedArray())
    }
}