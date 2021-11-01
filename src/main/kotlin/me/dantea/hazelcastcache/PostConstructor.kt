package me.dantea.hazelcastcache

import com.hazelcast.core.HazelcastInstance
import com.hazelcast.jet.config.JobConfig
import com.hazelcast.jet.pipeline.*
import com.hazelcast.jet.pipeline.test.TestSources
import io.github.serpro69.kfaker.Faker
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import java.io.Serializable
import java.security.MessageDigest
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct

@Component
class PostConstructor: Serializable {
    @Autowired
    @Qualifier("hazelcastInstance")
    lateinit var hazelcastInstance: HazelcastInstance

    @PostConstruct
    fun setup() {
        hazelcastInstance.getReliableTopic<Person>("topic")
            .addMessageListener {
                if (it.publishingMember.localMember()) {
                    println("Message: (${it.messageObject})")
                }
            }

        hazelcastInstance.jet
            .newJobIfAbsent(
                tokenHashPipeline(),
                JobConfig().setName("tokenhashJob")
            )

        hazelcastInstance.jet
            .newJobIfAbsent(
                messageProcessJob(),
                JobConfig().setName("messageJob")
            )
    }

    fun tokenHashPipeline(): Pipeline {
        val pipeline = Pipeline.create()
        pipeline.readFrom(
            Sources.mapJournal<String, String>(
                "shared",
                JournalInitialPosition.START_FROM_OLDEST
            )
        )
            .withIngestionTimestamps()
            .window(WindowDefinition.tumbling(TimeUnit.SECONDS.toMillis(1)))
            .distinct()
            .filter { it.result().key === "token" }
            .map { it.result() }
            .map { it.value }
            .map {
                MessageDigest.getInstance("SHA-256")
                    .digest(it.toByteArray())
                    .fold("") { str, b -> str + "%02x".format(b) }
            }
            .writeTo(Sinks.map("shared", { "hashed" }, { it }))

        return  pipeline
    }

    fun messageProcessJob(): Pipeline {
        val pipeline = Pipeline.create()
        pipeline.readFrom(TestSources.itemStream(5))
            .withoutTimestamps()
            .map { it.sequence() }
            .map {
                val faker = Faker()
                return@map Person(
                    it,
                    faker.name.firstName(),
                    faker.name.lastName(),
                    faker.address.city()
                )
            }
            .writeTo(Sinks.reliableTopic("topic"))

        return pipeline
    }
}