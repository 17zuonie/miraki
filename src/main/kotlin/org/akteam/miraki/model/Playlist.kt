package org.akteam.miraki.model

import me.liuwj.ktorm.entity.Entity
import me.liuwj.ktorm.schema.Table
import me.liuwj.ktorm.schema.int
import me.liuwj.ktorm.schema.timestamp
import java.time.Instant

interface Playlist : Entity<Playlist> {
    companion object : Entity.Factory<Playlist>()

    var n: Int
    var startTime: Instant
    var endTime: Instant?
}

object Playlists : Table<Playlist>("aki_playlist") {
    val n = int("n").primaryKey().bindTo { it.n }
    val startTime = timestamp("start_time").bindTo { it.startTime }
    val endTime = timestamp("end_time").bindTo { it.endTime }
}