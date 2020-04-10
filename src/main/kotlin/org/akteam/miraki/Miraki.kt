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
import net.mamoe.mirai.contact.sendMessage
import net.mamoe.mirai.event.events.MessageRecallEvent
import net.mamoe.mirai.event.events.author
import net.mamoe.mirai.event.subscribeAlways
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.PlainText
import okhttp3.OkHttpClient
import okhttp3.Request
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

    override fun onLoad() {
//        database = Database.connect("jdbc:h2:miraki_db", "org.h2.Driver")
        database = Database.connect("jdbc:sqlite:test.db", "org.sqlite.JDBC")
    }

    override fun onEnable() {
        logger.warning("Miraki enabled!")
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
                        .header("X-User-Token", "jLiBz0S2lSVPODeBTwnKT5B5Cxz8t5G6")
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
            if (this.group.id != 995339804L) return@subscribeAlways

            val msg = database.sequenceOf(Models.StoredGroupMessages)
                .filter { it.groupId eq this.group.id }
                .filter { it.sourceId eq this.messageId }
                .sortedByDescending { it.messageTime }
                .first()

            if (msg.revoked) return@subscribeAlways
            msg.revoked = true
            if (Random.nextInt(10) == 1) this.group.sendMessage(PlainText("哈哈~ 我看到了\n") + At(this.author) + "：“${msg.text}”")
            msg.flushChanges()
        }
    }
}