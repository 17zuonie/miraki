package org.akteam.miraki

import com.squareup.moshi.JsonClass
import me.liuwj.ktorm.database.Database
import me.liuwj.ktorm.entity.Entity
import me.liuwj.ktorm.schema.*

class Models {
    interface StoredGroupMessage : Entity<StoredGroupMessage> {
        companion object : Entity.Factory<StoredGroupMessage>()

        val n: Int
        var sourceId: Int
        var messageTime: Int
        var groupId: Long
        var senderId: Long
        var text: String
        var revoked: Boolean
    }

    object StoredGroupMessages : Table<StoredGroupMessage>("aki_stored_message") {
        val n by int("n").primaryKey().bindTo { it.n }
        val sourceId by int("source_id").bindTo { it.sourceId }
        val messageTime by int("message_time").bindTo { it.messageTime }
        val groupId by long("group_id").bindTo { it.groupId }
        val senderId by long("sender_id").bindTo { it.senderId }
        val text by varchar("text").bindTo { it.text }
        val revoked by boolean("revoked").bindTo { it.revoked }
    }

    fun createStoredGroupMessages(db: Database) {
        db.useConnection { conn ->
            val sql = """
                create table aki_stored_message(
                    n integer primary key autoincrement,
                    source_id integer not null,
                    message_time integer not null,
                    group_id long not null,
                    sender_id long not null,
                    text varchar not null
                )
            """.trimIndent()
        }
    }

    interface Group : Entity<Group> {
        companion object : Entity.Factory<Group>()

        var id: Long
        var name: String
    }

    object Groups : Table<Group>("aki_group") {
        val id by long("id").primaryKey().bindTo { it.id }
        val name by varchar("name").bindTo { it.name }
    }

    interface User : Entity<User> {
        companion object : Entity.Factory<User>()

        var id: Long
        var name: String
    }

    object Users : Table<User>("aki_user") {
        val id by long("id").primaryKey().bindTo { it.id }
        val name by varchar("name").bindTo { it.name }
    }

    @JsonClass(generateAdapter = true)
    data class Poem(
        val status: String,
        val data: FnData
    ) {
        @JsonClass(generateAdapter = true)
        data class FnData(
            val id: String,
            val content: String,
            val popularity: Int,
            val origin: Map<String, Any>,
            val matchTags: List<String>,
            val recommendedReason: String,
            val cacheAt: String
        )

    }
}
