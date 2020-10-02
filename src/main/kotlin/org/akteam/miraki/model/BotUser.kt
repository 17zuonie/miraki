package org.akteam.miraki.model

import me.liuwj.ktorm.dsl.eq
import me.liuwj.ktorm.entity.*
import me.liuwj.ktorm.schema.Table
import me.liuwj.ktorm.schema.boolean
import me.liuwj.ktorm.schema.enum
import me.liuwj.ktorm.schema.long
import net.mamoe.mirai.contact.Friend
import org.akteam.miraki.BotVariables

enum class UserLevel {
    GUEST, NORMAL, TEACHER, ADMIN, ROOT
}

interface BotUser : Entity<BotUser> {
    companion object : Entity.Factory<BotUser>()

    var qq: Long
    var level: UserLevel
    var subChunHuiNotice: Boolean

    fun hasPermission(minimalUserLevel: UserLevel): Boolean {
        return this.qq == BotVariables.cfg.rootUser || this.level >= minimalUserLevel
    }
}

object BotUsers : Table<BotUser>("aki_user") {
    val qq = long("qq").primaryKey().bindTo { it.qq }
    val level = enum<UserLevel>("level").bindTo { it.level }
    val subChunHuiNotice = boolean("sub_chnotice").bindTo { it.subChunHuiNotice }

    fun add(friend: Friend, users: EntitySequence<BotUser, BotUsers> = BotVariables.db.sequenceOf(BotUsers)) {
        users.add(BotUser {
            qq = friend.id
            level = UserLevel.NORMAL
        })
    }

    fun delete(friend: Friend, users: EntitySequence<BotUser, BotUsers> = BotVariables.db.sequenceOf(BotUsers)) {
        users.removeIf { qq eq friend.id }
    }

    fun loadUsers() {
        val friends = BotVariables.bot.friends
        val users = BotVariables.db.sequenceOf(BotUsers)
        val map = users.associateBy { it.qq }
        map.forEach { // https://github.com/kotlin-orm/ktorm/issues/124#issuecomment-608982814 查询结果不能遍历第二遍（创建 map 时算第一遍）
            val friend = friends.getOrNull(it.value.qq)
            if (friend == null) it.value.delete()
        }
        friends.forEach {
            val dbUser = map[it.id]
            if (dbUser == null) add(it, users)
        }
    }

    fun get(qq: Long): BotUser? {
        return BotVariables.db.sequenceOf(BotUsers).find { it.qq eq qq }
    }
}
