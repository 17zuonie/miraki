package org.akteam.miraki.model

import me.liuwj.ktorm.entity.Entity
import me.liuwj.ktorm.schema.*
import java.time.Instant

interface StoredGroupMessage : Entity<StoredGroupMessage> {
    companion object : Entity.Factory<StoredGroupMessage>()

    val n: Int
    var sourceId: Int
    var messageTime: Instant
    var groupId: Long
    var senderId: Long
    var text: String
    var revoked: Boolean
}

object StoredGroupMessages : Table<StoredGroupMessage>("aki_stored_message") {
    val n = int("n").primaryKey().bindTo { it.n }
    val sourceId = int("source_id").bindTo { it.sourceId }
    val messageTime = timestamp("message_time").bindTo { it.messageTime }
    val groupId = long("group_id").bindTo { it.groupId }
    val senderId = long("sender_id").bindTo { it.senderId }
    val text = varchar("text").bindTo { it.text }
    val revoked = boolean("revoked").bindTo { it.revoked }
}
