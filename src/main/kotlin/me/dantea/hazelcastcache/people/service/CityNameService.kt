package me.dantea.hazelcastcache.people.service

interface CityNameService {
    fun refreshCityNamesFromRemoteServer()
    fun getCityNames(): Array<String>
    fun getCityNameForId(id: Long): String
}