package org.akteam.miraki

import com.google.gson.Gson
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.liuwj.ktorm.database.Database
import me.liuwj.ktorm.dsl.eq
import me.liuwj.ktorm.entity.*
import net.mamoe.mirai.console.plugins.PluginBase
import net.mamoe.mirai.console.plugins.withDefaultWriteSave
import net.mamoe.mirai.contact.sendMessage
import net.mamoe.mirai.event.events.MessageRecallEvent
import net.mamoe.mirai.event.events.author
import net.mamoe.mirai.event.subscribeAlways
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.message.data.At
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

        val databaseUrl by fileConfig.withDefaultWriteSave { "jdbc:sqlite:${dataFolder.toRelativeString(
            File("") // current working directory
        )}/test.db" }
        val databaseDriver by fileConfig.withDefaultWriteSave { "org.sqlite.JDBC" }
        val jinrishiciToken by fileConfig.withDefaultWriteSave { "jLiBz0S2lSVPODeBTwnKT5B5Cxz8t5G6" } // it is persistent no need to change
        val antiRevokeGroups by lazy {
            fileConfig.setIfAbsent("antiRevokeGroups", listOf<Long>(187410654))
            fileConfig.getLongList("antiRevokeGroups").toMutableList()
        }

        fun manualSave() {
            fileConfig["antiRevokeGroups"] = antiRevokeGroups
            fileConfig.save()
        }
    }

    override fun onLoad() {
//        database = Database.connect("jdbc:h2:miraki_db", "org.h2.Driver")
        database = Database.connect(config.databaseUrl, config.databaseDriver)
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
        }
        subscribeAlways<MessageRecallEvent.GroupRecall> {

            val msg = database.sequenceOf(Models.StoredGroupMessages)
                .filter { it.groupId eq this.group.id }
                .filter { it.sourceId eq this.messageId }
                .sortedByDescending { it.messageTime }
                .first()

            if (msg.revoked) return@subscribeAlways
            msg.revoked = true
            if (this.group.id in config.antiRevokeGroups && Random.nextInt(5) == 1) this.group.sendMessage(PlainText("哈哈~ 我看到了\n") + At(this.author) + "：“${msg.text}”")
            msg.flushChanges()
        }
        logger.warning("Miraki enabled!")
    }

    override fun onDisable() {
        config.manualSave()
        logger.warning("Miraki disabled!")
    }
}