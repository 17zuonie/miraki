package org.akteam.miraki.web

import kotlinx.serialization.Serializable
import net.mamoe.mirai.contact.Friend
import org.akteam.miraki.model.RecommendMusic

object Response {
    @Serializable
    class Song {
        constructor(r: RecommendMusic, friend: Friend) {
            n = r.n

            // 网页展示，信息多一点更好看
            avatar = friend.avatarUrl
            nick = friend.nick

            qq = r.qq
            subTime = r.subTime.epochSecond

            like = r.like
            playlistId = r.playlistId
            confirmed = r.confirmed

            title = r.title
            artist = r.artist
            platform = r.platform

            musicUrl = r.musicUrl
            jumpUrl = r.jumpUrl
            previewUrl = r.previewUrl
        }

        val n: Int

        val avatar: String
        val nick: String
        val qq: Long
        val subTime: Long

        val like: Int
        val playlistId: Int
        val confirmed: Boolean

        val title: String
        val artist: String
        val platform: String

        val musicUrl: String
        val jumpUrl: String
        val previewUrl: String
    }

    @Serializable
    data class AuthInfo(
            val valid: Boolean,
            val qq: Long,
            val expireAt: Long
    )
}