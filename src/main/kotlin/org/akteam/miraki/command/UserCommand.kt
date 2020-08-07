package org.akteam.miraki.command

import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.MessageChain
import org.akteam.miraki.model.BotUser
import org.akteam.miraki.model.UserLevel

interface UserCommand : SimpleCommand {
    /** 执行命令后的逻辑 */
    suspend fun execute(event: MessageEvent, args: List<String>, user: BotUser): MessageChain

    val level: UserLevel
    val permission: String?
}