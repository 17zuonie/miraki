package org.akteam.miraki.objects

import me.liuwj.ktorm.dsl.*
import me.liuwj.ktorm.entity.Entity
import me.liuwj.ktorm.entity.add
import me.liuwj.ktorm.entity.associateBy
import me.liuwj.ktorm.entity.sequenceOf
import me.liuwj.ktorm.schema.Table
import me.liuwj.ktorm.schema.enum
import me.liuwj.ktorm.schema.long
import me.liuwj.ktorm.schema.typeRef
import net.mamoe.mirai.contact.isOperator
import org.akteam.miraki.BotConsts
import org.akteam.miraki.BotMain

enum class UserLevel {
    GUEST, NORMAL, TEACHER, ADMIN, ROOT
}

interface BotUser : Entity<BotUser> {
    companion object : Entity.Factory<BotUser>()

    var qq: Long
    var level: UserLevel

    fun hasPermission(t: String?): Boolean {
        return if (t != null) {
            val p = BotConsts.db.from(UserPermissions)
                .select(UserPermissions.grant)
                .where { (UserPermissions.qq eq qq) and (UserPermissions.permission eq t) }
                .firstOrNull()

            if (p != null) p[UserPermissions.grant]!!
            else false
        } else false
    }
}

object BotUsers : Table<BotUser>("aki_user") {
    val qq by long("qq").primaryKey().bindTo { it.qq }
    val level by enum("level", typeRef<UserLevel>()).bindTo { it.level }

    fun loadUsersFromGroup(gid: Long = BotConsts.cfg.botMainGroup) {
        val group = BotMain.bot.getGroup(gid)
        val users = BotConsts.db.sequenceOf(BotUsers)
        val map = users.associateBy { it.qq }
        group.members.forEach {
            val dbUser = map[it.id]
            if (dbUser == null) users.add(BotUser {
                qq = it.id
                level = if (it.isOperator()) UserLevel.ADMIN else UserLevel.NORMAL
            })
            else {
                if (it.isOperator() && dbUser.level < UserLevel.ADMIN) {
                    dbUser.level = UserLevel.ADMIN
                    dbUser.flushChanges()
                }
            }
        }
    }
}
