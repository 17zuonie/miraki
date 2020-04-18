package org.akteam.miraki

import com.squareup.moshi.JsonClass
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
        var rawMessage: String
        var revoked: Boolean
    }

    object StoredGroupMessages : Table<StoredGroupMessage>("aki_stored_message") {
        val n by int("n").primaryKey().bindTo { it.n }
        val sourceId by int("source_id").bindTo { it.sourceId }
        val messageTime by int("message_time").bindTo { it.messageTime }
        val groupId by long("group_id").bindTo { it.groupId }
        val senderId by long("sender_id").bindTo { it.senderId }
        val text by varchar("text").bindTo { it.text }
        val rawMessage by varchar("raw_message").bindTo { it.rawMessage }
        val revoked by boolean("revoked").bindTo { it.revoked }
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
        var trueName: String
    }

    object Users : Table<User>("aki_user") {
        val id by long("id").primaryKey().bindTo { it.id }
        val trueName by varchar("true_name").bindTo { it.trueName }
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

    interface Notice : Entity<Notice> {
        companion object : Entity.Factory<Notice>()

        val n: Int
        var relativeDate: String
        var date: String
        var titleWithAuthor: String
    }

    object Notices : Table<Notice>("aki_notice") {
        val n by int("n").primaryKey().bindTo { it.n }
        val relativeDate by varchar("relative_date").bindTo { it.relativeDate }
        val date by varchar("date").bindTo { it.date }
        val titleWithAuthor by varchar("title_with_author").bindTo { it.titleWithAuthor }
    }
}
