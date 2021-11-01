package me.dantea.hazelcastcache.token

import com.hazelcast.core.HazelcastInstance
import com.hazelcast.jet.config.JobConfig
import com.hazelcast.jet.pipeline.*
import me.dantea.hazelcastcache.token.common.HASHED_MAP_NAME
import me.dantea.hazelcastcache.token.common.HASHED_MAP_VALUE_KEY
import me.dantea.hazelcastcache.token.common.SHARED_MAP_NAME
import me.dantea.hazelcastcache.token.common.SHARED_MAP_TOKEN_KEY
import me.dantea.hazelcastcache.token.entity.DToken
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import java.security.MessageDigest
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct

@Component
class DTokenConstructor {
    @Autowired
    @Qualifier("hazelcastInstance")
    lateinit var hazelcast: HazelcastInstance

    @PostConstruct
    fun setup() {
        hazelcast.jet
            .newJobIfAbsent(tokenHashingPipeline(), JobConfig().setName("tokenHashingJob"))
    }

    fun tokenHashingPipeline(): Pipeline {
        val pipeline = Pipeline.create()
        pipeline.readFrom(
            Sources.mapJournal<String, DToken>(
                SHARED_MAP_NAME,
                JournalInitialPosition.START_FROM_OLDEST
            )
        )
            .withIngestionTimestamps()
            .window(WindowDefinition.tumbling(TimeUnit.SECONDS.toMillis(1)))
            .distinct()
            .filter { it.result().key == SHARED_MAP_TOKEN_KEY }
            .map { it.result() }
            .map { it.value }
            .map {
                MessageDigest.getInstance("SHA-256")
                    .digest(it.value.toByteArray())
                    .fold("") { str, b -> str + "%02x".format(b) }
            }
            .writeTo(Sinks.map(HASHED_MAP_NAME, { HASHED_MAP_VALUE_KEY }, { it }))

        return pipeline
    }
}

//    fun messageProcessJob(): Pipeline {
//        val cityNameServiceFactory = ServiceFactories
//            .sharedService<CityNameService> { CityNameServiceImpl() }
//            .toNonCooperative()
//
//        val pipeline = Pipeline.create()
//        pipeline.readFrom(TestSources.itemStream(5))
//            .withoutTimestamps()
//            .map { it.sequence() }
//            .map {
//                val faker = Faker()
//                return@map Person(
//                    it,
//                    faker.name.firstName(),
//                    faker.name.lastName(),
//                    ""
//                )
//            }
//            .mapUsingService(cityNameServiceFactory) { service, it ->
//                Person(
//                    it.id,
//                    it.givenName,
//                    it.surname,
//                    service.cityNameForId(it.id)
//                )
//            }
//            .writeTo(Sinks.logger())
//
//        return pipeline
//    }
//}