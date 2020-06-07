package org.akteam.miraki.objects

import me.liuwj.ktorm.entity.Entity
import me.liuwj.ktorm.schema.*

enum class UserLevel {
    ROOT, ADMIN, TEACHER, NORMAL, GUEST
}

interface BotUser : Entity<BotUser> {
    companion object : Entity.Factory<BotUser>()

    var qq: Long
    var trueName: String
    var level: UserLevel
    fun compareLevel(t: UserLevel) = this.level <= t
}

object BotUsers : Table<BotUser>("aki_user") {
    val qq by long("qq").primaryKey().bindTo { it.qq }
    val trueName by varchar("true_name").bindTo { it.trueName }
    val level by enum("level", typeRef<UserLevel>()).bindTo { it.level }
}
