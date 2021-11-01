package me.dantea.hazelcastcache.people.service

import com.hazelcast.core.Hazelcast
import io.github.serpro69.kfaker.Faker
import me.dantea.hazelcastcache.people.common.CITIES_QUANTITY
import me.dantea.hazelcastcache.people.common.PEOPLE_MAP_CITY_KEY
import me.dantea.hazelcastcache.people.common.PEOPLE_MAP_NAME
import org.springframework.stereotype.Service
import java.io.Serializable

@Service
class CityNameServiceImpl: CityNameService, Serializable {
    override fun refreshCityNamesFromRemoteServer() {
        val faker = Faker()
        val hazelcast = Hazelcast.getHazelcastInstanceByName("mr_hazel")
        hazelcast
            .getMap<String, Array<String>>(PEOPLE_MAP_NAME)
            .put(PEOPLE_MAP_CITY_KEY, Array(CITIES_QUANTITY) { faker.address.city() })
    }

    override fun getCityNames() = Hazelcast.getHazelcastInstanceByName("mr_hazel")
        .getMap<String, Array<String>>(PEOPLE_MAP_NAME)
        .get(PEOPLE_MAP_CITY_KEY)!!

    override fun getCityNameForId(id: Long) = getCityNames()[(id % CITIES_QUANTITY).toInt()]
}