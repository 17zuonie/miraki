package org.akteam.miraki.model

import kotlinx.serialization.Serializable
import java.util.UUID.randomUUID

@Serializable
data class Config(
    val akiQQ: Long,
    val password: String
) {
    val rootUser = 10000L
    val newFriendGroup = 10000L
    val netEaseCookie = ""
    val netEaseApi = "localhost:3000"
    val databaseUrl = "jdbc:pgsql://localhost/miraki"
    val databaseUser = "miraki"
    val databasePassword = "miraki"
    val chunHuiUrl = "http://10.181.200.3/home/index/"
    val jinrishiciToken = "jLiBz0S2lSVPODeBTwnKT5B5Cxz8t5G6" // it is persistent no need to change
    val jwtSecret = randomUUID()!!.toString() // 随机一个 secret 不一定要是 UUID
    val httpApiUrl = "http://localhost:8080/"
    val fetchNoticeDelay = 60L
    val sessionExpireTime = 15 * 60L

    val commandPrefix = listOf("-", ".", "!", "#", "/")
}