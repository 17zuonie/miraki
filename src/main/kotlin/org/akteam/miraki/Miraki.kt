package org.akteam.miraki

import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.liuwj.ktorm.database.Database
import me.liuwj.ktorm.database.SqlDialect
import me.liuwj.ktorm.dsl.*
import me.liuwj.ktorm.entity.*
import me.liuwj.ktorm.support.sqlite.SQLiteDialect
import net.mamoe.mirai.console.plugins.PluginBase
import net.mamoe.mirai.console.plugins.withDefaultWriteSave
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.contact.sendMessage
import net.mamoe.mirai.event.events.MessageRecallEvent
import net.mamoe.mirai.event.events.author
import net.mamoe.mirai.event.subscribeAlways
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.MessageChainBuilder
import net.mamoe.mirai.message.data.PlainText
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.random.Random

object Miraki : PluginBase() {
    private lateinit var database: Database

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .writeTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .callTimeout(10, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
//        .add(KotlinJsonAdapterFactory())
        .build()!!

    private val config = object {
        val fileConfig = loadConfig("settings.yml")

        val databaseUrl by fileConfig.withDefaultWriteSave {
            "${dataFolder.toRelativeString(File("").absoluteFile)}/test.db" // related to current working directory
        }
        val jinrishiciToken by fileConfig.withDefaultWriteSave { "jLiBz0S2lSVPODeBTwnKT5B5Cxz8t5G6" } // it is persistent no need to change
        val antiRevokeGroups by lazy {
            fileConfig.setIfAbsent("antiRevokeGroups", listOf<Long>(187410654))
            fileConfig.getLongList("antiRevokeGroups").toMutableList()
        }
        val longwangLookupGroups by lazy {
            fileConfig.setIfAbsent("longwangLookupGroups", listOf<Long>(187410654))
            fileConfig.getLongList("longwangLookupGroups")
        }

        fun manualSave() {
            fileConfig["antiRevokeGroups"] = antiRevokeGroups
            fileConfig["longwangLookupGroups"] = longwangLookupGroups
            fileConfig.save()
        }
    }

    override fun onLoad() {
//        database = Database.connect("jdbc:h2:miraki_db", "org.h2.Driver")
        database = Database.connect("jdbc:sqlite:${config.databaseUrl}", "org.sqlite.JDBC", dialect = SQLiteDialect())
    }

    override fun onEnable() {
        subscribeGroupMessages {
            always {
                val msg = Models.StoredGroupMessage {
                    sourceId = source.id
                    messageTime = source.time
                    groupId = group.id
                    senderId = sender.id
                    text = message.contentToString()
                    revoked = false
                }
                database.sequenceOf(Models.StoredGroupMessages).add(msg)
            }

            contains("一言") {
                launch {
                    val req = Request.Builder()
                        .url("https://v1.hitokoto.cn/?encode=text")
                        .build()

                    val text = withContext(Dispatchers.IO) {
                        httpClient.newCall(req).execute()
                            .body!!.string()
                    }

                    subject.sendMessage(text)
                }
            }

            contains("诗") {
                launch {
                    val req = Request.Builder()
                        .url("https://v2.jinrishici.com/sentence")
                        .header("X-User-Token", config.jinrishiciToken)
                        .build()

                    val poem = withContext(Dispatchers.IO) {
                        val raw = httpClient.newCall(req).execute()
                            .body!!.string()

                        moshi.adapter(Models.Poem::class.java).fromJson(raw)!!
                    }

                    subject.sendMessage(poem.data.content)
                }
            }

            contains("龙王") {
                if (group.id !in config.longwangLookupGroups) return@contains
                val t = Models.StoredGroupMessages
                val cnt = count(t.n).aliased("cnt")
                val all = database.from(t)
                    .select(t.senderId, cnt)
                    .where { t.groupId eq group.id }
                    .groupBy(t.senderId)
                    .orderBy(cnt.desc())
                    .limit(0, 9)

                val builder = MessageChainBuilder()
                builder.add("我们群拥有来自五湖四海的龙王：\n")
                all.forEachIndexed { i, row ->
                    builder.add("No.${i}: ${group.members[row[t.senderId]!!].nameCardOrNick}: ${row[cnt]}\n")
                }
                reply(builder.asMessageChain())
            }
        }

        subscribeAlways<MessageRecallEvent.GroupRecall> {
            var msg: Models.StoredGroupMessage? = null
            try {
                msg = database.sequenceOf(Models.StoredGroupMessages)
                    .filter { it.groupId eq this.group.id }
                    .filter { it.sourceId eq this.messageId }
                    .sortedByDescending { it.messageTime }
                    .first()
            } catch (e: NoSuchElementException) {
                logger.info("Message not found in database")
            }

            msg?.let {
                if (it.revoked) return@subscribeAlways
                it.revoked = true
                if (this.group.id in config.antiRevokeGroups && Random.nextInt(5) == 1) this.group.sendMessage(
                    PlainText(
                        "哈哈~ 我看到了\n"
                    ) + At(this.author) + "：“${it.text}”"
                )
                it.flushChanges()
            }
        }

        logger.warning("Miraki enabled!")
    }

    override fun onDisable() {
        config.manualSave()
        logger.warning("Miraki disabled!")
    }
}