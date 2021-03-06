package org.akteam.miraki.command

import net.mamoe.mirai.message.MessageEvent
import org.akteam.miraki.model.BotUser
import org.akteam.miraki.model.UserLevel

interface UserCommand : SimpleCommand {
    /** 执行命令后的逻辑 */
    suspend fun execute(event: MessageEvent, args: List<String>, user: BotUser)

    val level: UserLevel
    val permission: String?
}