package org.akteam.miraki.command

import net.mamoe.mirai.message.MessageEvent
import org.akteam.miraki.model.BotUser
import org.akteam.miraki.model.UserLevel

data class Intent(

    // 0..100
    val confidence: Int,

    // 推荐执行命令，可以不是自己
    val advice: NaturalCommand
)

interface NaturalCommand {
    suspend fun entry(event: MessageEvent, user: BotUser)

    suspend fun intent(event: MessageEvent, user: BotUser): Intent

    val name: String
    val userLevel: UserLevel
}