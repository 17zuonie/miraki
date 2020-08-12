package org.akteam.miraki.model

import net.mamoe.mirai.message.FriendMessageEvent
import net.mamoe.mirai.message.GroupMessageEvent
import net.mamoe.mirai.message.MessageEvent
import org.akteam.miraki.BotVariables
import java.time.Instant

data class Session(
        val qq: Long,
        val group: Long?,
        val expireTime: Instant,
        val handler: (MessageEvent) -> Unit
)
