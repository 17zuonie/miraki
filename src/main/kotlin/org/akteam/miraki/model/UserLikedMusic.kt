package org.akteam.miraki.model

import me.liuwj.ktorm.dsl.and
import me.liuwj.ktorm.dsl.eq
import me.liuwj.ktorm.entity.Entity
import me.liuwj.ktorm.entity.count
import me.liuwj.ktorm.entity.filter
import me.liuwj.ktorm.entity.sequenceOf
import me.liuwj.ktorm.schema.Table
import me.liuwj.ktorm.schema.int
import me.liuwj.ktorm.schema.long
import me.liuwj.ktorm.schema.timestamp
import org.akteam.miraki.BotVariables
import java.time.Instant

interface UserLikedMusic : Entity<UserLikedMusic> {
    companion object : Entity.Factory<UserLikedMusic>()

    var n: Int

    var qq: Long
    var musicId: Int
    var playlistId: Int
    var subTime: Instant
}

object UserLikedMusics : Table<UserLikedMusic>("aki_liked_music") {
    val n = int("n").bindTo { it.n }

    val qq = long("qq").bindTo { it.qq }
    val musicId = int("music_id").bindTo { it.musicId }
    val playlistId = int("playlist_id").bindTo { it.playlistId }
    val subTime = timestamp("sub_time").bindTo { it.subTime }

    fun liked(qq: Long, music: RecommendMusic) = BotVariables.db.sequenceOf(this)
            .filter { (it.qq eq qq) and (it.musicId eq music.n) and (it.playlistId eq music.playlistId) }
            .count()
}
