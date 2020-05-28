package org.akteam.miraki.objects

import me.liuwj.ktorm.entity.Entity
import me.liuwj.ktorm.schema.*

enum class UserRole {
    ROOT, ADMIN, TESTER, STUDENT, TEACHER, NORMAL
}

interface BotUser : Entity<BotUser> {
    companion object : Entity.Factory<BotUser>()

    var id: Long
    var trueName: String
    var role: UserRole
}

object BotUsers : Table<BotUser>("aki_user") {
    val id by long("id").primaryKey().bindTo { it.id }
    val trueName by varchar("true_name").bindTo { it.trueName }
    val role by enum("role", typeRef<UserRole>()).bindTo { it.role }
}
