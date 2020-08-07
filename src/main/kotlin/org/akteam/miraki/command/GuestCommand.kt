package org.akteam.miraki.command

import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.MessageChain

interface GuestCommand : SimpleCommand {
    /** 执行命令后的逻辑 */
    suspend fun execute(event: MessageEvent, args: List<String>): MessageChain
}