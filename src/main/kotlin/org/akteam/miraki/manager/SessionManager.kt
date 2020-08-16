package org.akteam.miraki.manager

import net.mamoe.mirai.message.FriendMessageEvent
import net.mamoe.mirai.message.GroupMessageEvent
import net.mamoe.mirai.message.MessageEvent
import org.akteam.miraki.model.BotUser
import org.akteam.miraki.model.Session
import java.time.Instant

object SessionManager {
    private val sessions: MutableMap<Long, Session> = mutableMapOf()
    // Pair<groupId, senderId>
    private val groupSessions: MutableMap<Pair<Long, Long>, Session> = mutableMapOf()

    fun remove(event: MessageEvent): Session? {
        val senderId = event.sender.id
        return when(event) {
            is GroupMessageEvent -> {
                val groupId = event.group.id
                groupSessions.remove(Pair(groupId, senderId))
            }
            is FriendMessageEvent -> {
                sessions.remove(senderId)
            }
            else -> {
                null
            }
        }
    }

    fun executeSession(event: MessageEvent, user: BotUser?): Boolean {
        if (user == null) return false
        val senderId = event.sender.id
        val session = when(event) {
            is GroupMessageEvent -> {
                val groupId = event.group.id
                groupSessions.remove(Pair(groupId, senderId))
            }
            is FriendMessageEvent -> {
                sessions.remove(senderId)
            }
            else -> {
                null
            }
        }
        if (session != null) session.handler(event)
        else return false
        return true
    }

    fun set(event: MessageEvent, expireTime: Instant, handler: (MessageEvent) -> Unit) {
        val senderId = event.sender.id
        when(event) {
            is FriendMessageEvent -> {
                val session = Session(senderId, null, expireTime, handler)
                sessions[senderId] = session
            }
            is GroupMessageEvent -> {
                val groupId = event.group.id
                val session = Session(senderId, groupId, expireTime, handler)
                groupSessions[Pair(groupId, senderId)] = session
            }
        }
    }
}