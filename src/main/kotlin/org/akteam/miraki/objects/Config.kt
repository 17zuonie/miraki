package org.akteam.miraki.objects

import kotlinx.serialization.Serializable

@Serializable
data class Config(
    val akiQQ: Long,
    val password: String,
    val rootUser: Long
) {
    val netEaseCookie = ""
    val netEaseApi = "localhost:3000"
    val databaseUrl = "jdbc:pgsql://localhost/test"
    val databaseUser = "miraki"
    val chunHuiUrl = "http://10.181.200.3/home/index/"
    val jinrishiciToken = "jLiBz0S2lSVPODeBTwnKT5B5Cxz8t5G6" // it is persistent no need to change
    val fetchNoticeDelay = 60 * 1000L

    val antiRevokeGroups = listOf<Long>(187410654)
    val longwangLookupGroups = listOf<Long>(187410654)
    val noticeBroadcastGroups = listOf<Long>(187410654)

    val commandPrefix = listOf("-", ".", "!", "#", "/", "。")
    val filterWords: List<String> = listOf()
    val coolDownTime: Int = 7
}