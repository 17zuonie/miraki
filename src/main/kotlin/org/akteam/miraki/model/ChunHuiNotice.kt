package org.akteam.miraki.model

import me.liuwj.ktorm.entity.Entity
import me.liuwj.ktorm.schema.Table
import me.liuwj.ktorm.schema.int
import me.liuwj.ktorm.schema.varchar

interface ChunHuiNotice : Entity<ChunHuiNotice> {
    companion object : Entity.Factory<ChunHuiNotice>()

    val n: Int
    var relativeDate: String
    var date: String
    var titleWithAuthor: String
}

object ChunHuiNotices : Table<ChunHuiNotice>("aki_notice") {
    val n by int("n").primaryKey().bindTo { it.n }
    val relativeDate by varchar("relative_date").bindTo { it.relativeDate }
    val date by varchar("date").bindTo { it.date }
    val titleWithAuthor by varchar("title_with_author").bindTo { it.titleWithAuthor }
}