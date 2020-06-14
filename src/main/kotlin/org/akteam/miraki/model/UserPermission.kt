package org.akteam.miraki.model

import me.liuwj.ktorm.entity.Entity
import me.liuwj.ktorm.schema.*

interface UserPermission : Entity<UserPermission> {
    companion object : Entity.Factory<UserPermission>()

    var id: Int
    var qq: Long
    var permission: String
    var grant: Boolean
}

object UserPermissions : Table<UserPermission>("aki_user") {
    val id by int("id").primaryKey().bindTo { it.id }
    val qq by long("qq").bindTo { it.qq }
    val permission by varchar("permission").bindTo { it.permission }
    val grant by boolean("grant").bindTo { it.grant }
}
