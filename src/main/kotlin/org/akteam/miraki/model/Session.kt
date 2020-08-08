package org.akteam.miraki.model

import net.mamoe.mirai.message.FriendMessageEvent
import net.mamoe.mirai.message.GroupMessageEvent
import net.mamoe.mirai.message.MessageEvent
import org.akteam.miraki.BotVariables
import java.time.Instant

data class Session(
        val qq: Long,
        val expireTime: Instant = Instant.now().plusSeconds(BotVariables.cfg.sessionExpireTime),
        val handler: (FriendMessageEvent, BotUser) -> Unit
)

data class GroupSession(
        val qq: Long,
        val group: Long,
        val expireTime: Instant = Instant.now().plusSeconds(BotVariables.cfg.sessionExpireTime),
        val handler: (GroupMessageEvent, BotUser) -> Unit
)
