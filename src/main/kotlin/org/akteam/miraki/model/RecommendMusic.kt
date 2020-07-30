package org.akteam.miraki.model

import me.liuwj.ktorm.entity.Entity
import me.liuwj.ktorm.schema.*
import java.time.Instant

interface RecommendMusic : Entity<RecommendMusic> {
    companion object : Entity.Factory<RecommendMusic>()

    var n: Int

    var qq: Long
    var subTime: Instant

    var like: Int
    var playlistId: Int
    var confirmed: Boolean

    var title: String
    var artist: String
    var platform: String

    var musicUrl: String
    var jumpUrl: String
    var previewUrl: String

    fun eq(other: RecommendMusic): Boolean {
        return other.title == this.title && other.artist == this.artist
    }
}

object RecommendMusics : Table<RecommendMusic>("aki_rec_music") {
    val n = int("n").primaryKey().bindTo { it.n }
    val qq = long("qq").bindTo { it.qq }
    val subTime = timestamp("sub_time").bindTo { it.subTime }
    val like = int("like_num").bindTo { it.like }
    val playlistId = int("playlist_id").bindTo { it.playlistId }
    val confirmed = boolean("confirmed").bindTo { it.confirmed }
    val title = varchar("title").bindTo { it.title }
    val artist = varchar("artist").bindTo { it.artist }
    val platform = varchar("platform").bindTo { it.platform }
    val musicUrl = varchar("music_url").bindTo { it.musicUrl }
    val jumpUrl = varchar("jump_url").bindTo { it.jumpUrl }
    val previewUrl = varchar("preview_url").bindTo { it.previewUrl }
}
