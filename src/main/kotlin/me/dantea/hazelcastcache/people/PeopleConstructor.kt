package me.dantea.hazelcastcache.people

import com.hazelcast.core.HazelcastInstance
import com.hazelcast.jet.config.JobConfig
import com.hazelcast.jet.pipeline.Pipeline
import com.hazelcast.jet.pipeline.ServiceFactories
import com.hazelcast.jet.pipeline.Sinks
import com.hazelcast.jet.pipeline.test.TestSources
import io.github.serpro69.kfaker.Faker
import me.dantea.hazelcastcache.people.entity.Person
import me.dantea.hazelcastcache.people.service.CityNameService
import me.dantea.hazelcastcache.people.service.CityNameServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class PeopleConstructor {
    @Autowired
    @Qualifier("hazelcastInstance")
    lateinit var hazelcast: HazelcastInstance

    @Autowired
    lateinit var service: CityNameService

    @Scheduled(fixedRate = 5000)
    fun cityDataRefresh() {
        service.refreshCityNamesFromRemoteServer()
    }

    @PostConstruct
    fun setup() {
        service.refreshCityNamesFromRemoteServer()
        hazelcast.jet
            .newJobIfAbsent(peopleEnrichPipeline(), JobConfig().setName("peopleEnrichJob"))
    }

    fun peopleEnrichPipeline(): Pipeline {
        val cityNameServiceFactory = ServiceFactories
            .sharedService<CityNameService> { CityNameServiceImpl() }
            .toNonCooperative()

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
                    ""
                )
            }
            .mapUsingService(cityNameServiceFactory) { service, it ->
                Person(
                    it.id,
                    it.givenName,
                    it.surname,
                    service.getCityNameForId(it.id)
                )
            }
            .writeTo(Sinks.logger())

        return pipeline
    }
}