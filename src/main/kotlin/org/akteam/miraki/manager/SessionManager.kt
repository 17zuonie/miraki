package org.akteam.miraki.manager

import net.mamoe.mirai.message.FriendMessageEvent
import net.mamoe.mirai.message.GroupMessageEvent
import net.mamoe.mirai.message.MessageEvent
import org.akteam.miraki.model.BotUser
import org.akteam.miraki.model.GroupSession
import org.akteam.miraki.model.Session
import java.time.Instant

object SessionManager {
    private val sessions: MutableMap<Long, Session> = mutableMapOf()
    // Pair<groupId, senderId>
    private val groupSessions: MutableMap<Pair<Long, Long>, GroupSession> = mutableMapOf()

    private fun maintain(groupId: Long, senderId: Long): GroupSession? {
        val session = groupSessions[Pair(groupId, senderId)]
        return if (session != null) {
            if (session.expireTime < Instant.now()) {
                groupSessions.remove(Pair(groupId, senderId))
                null
            } else {
                session
            }
        } else null
    }

    private fun maintain(senderId: Long): Session? {
        val session = sessions[senderId]
        return if (session != null) {
            if (session.expireTime < Instant.now()) {
                sessions.remove(senderId)
                null
            } else {
                session
            }
        } else null
    }

    fun executeSession(event: MessageEvent, user: BotUser?): Boolean {
        if (user == null) return false
        val senderId = event.sender.id
        when(event) {
            is GroupMessageEvent -> {
                val groupId = event.group.id
                val session = maintain(groupId, senderId)
                if (session != null) session.handler(event, user)
                else return false
                return true
            }
            is FriendMessageEvent -> {
                val session = maintain(senderId)
                if (session != null) session.handler(event, user)
                else return false
                return true
            }
            else -> {
                return false
            }
        }
    }

    fun set(event: MessageEvent, expireTime: Instant, handler: (MessageEvent, BotUser) -> Unit) {
        val senderId = event.sender.id
        when(event) {
            is FriendMessageEvent -> {
                val session = Session(senderId, expireTime, handler)
                sessions[senderId] = session
            }
            is GroupMessageEvent -> {
                val groupId = event.group.id
                val session = GroupSession(senderId, groupId, expireTime, handler)
                groupSessions[Pair(groupId, senderId)] = session
            }
        }
    }
}