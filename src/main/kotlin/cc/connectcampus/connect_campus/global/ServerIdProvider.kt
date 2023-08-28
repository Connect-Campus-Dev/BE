package cc.connectcampus.connect_campus.global

import java.util.*

object ServerIdProvider {
    val serverInstanceId: String = UUID.randomUUID().toString()
}

fun fetchServerInstanceId(): String {
    return ServerIdProvider.serverInstanceId
}