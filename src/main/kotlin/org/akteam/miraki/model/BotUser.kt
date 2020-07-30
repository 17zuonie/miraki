package org.akteam.miraki.model

import me.liuwj.ktorm.dsl.eq
import me.liuwj.ktorm.entity.*
import me.liuwj.ktorm.schema.*
import net.mamoe.mirai.contact.Friend
import org.akteam.miraki.BotConsts
import org.akteam.miraki.BotMain

enum class UserLevel {
    GUEST, NORMAL, TEACHER, ADMIN, ROOT
}

interface BotUser : Entity<BotUser> {
    companion object : Entity.Factory<BotUser>()

    var qq: Long
    var level: UserLevel
    var subChunHuiNotice: Boolean
}

object BotUsers : Table<BotUser>("aki_user") {
    val qq = long("qq").primaryKey().bindTo { it.qq }
    val level = enum("level", typeRef<UserLevel>()).bindTo { it.level }
    val subChunHuiNotice = boolean("sub_chnotice").bindTo { it.subChunHuiNotice }

    fun add(friend: Friend, users: EntitySequence<BotUser, BotUsers> = BotConsts.db.sequenceOf(BotUsers)) {
        users.add(BotUser {
            qq = friend.id
            level = UserLevel.NORMAL
        })
    }

    fun loadUsers() {
        val friends = BotMain.bot.friends
        val users = BotConsts.db.sequenceOf(BotUsers)
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
        return BotConsts.db.sequenceOf(BotUsers).find { it.qq eq qq }
    }
}
