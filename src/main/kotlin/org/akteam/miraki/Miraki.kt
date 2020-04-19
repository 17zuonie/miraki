package org.akteam.miraki

import com.squareup.moshi.Moshi
import kotlinx.coroutines.*
import me.liuwj.ktorm.database.Database
import me.liuwj.ktorm.dsl.*
import me.liuwj.ktorm.entity.*
import me.liuwj.ktorm.support.postgresql.PostgreSqlDialect
import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.command.registerCommand
import net.mamoe.mirai.console.plugins.PluginBase
import net.mamoe.mirai.console.plugins.withDefaultWriteSave
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.contact.sendMessage
import net.mamoe.mirai.event.events.MessageRecallEvent
import net.mamoe.mirai.event.events.author
import net.mamoe.mirai.event.subscribeAlways
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.Face
import net.mamoe.mirai.message.data.MessageChainBuilder
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.recallIn
import okhttp3.OkHttpClient
import okhttp3.Request
import java.time.Instant
import java.util.concurrent.TimeUnit
import kotlin.random.Random

object Miraki : PluginBase() {
    lateinit var database: Database

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

        val akiQQ by fileConfig.withDefaultWriteSave { 100000L }
        val rootUser by fileConfig.withDefaultWriteSave { 100000L }
        val databaseUrl by fileConfig.withDefaultWriteSave { "jdbc:postgresql:miraki" }
        val databaseUser by fileConfig.withDefaultWriteSave { "miraki" }
        val jinrishiciToken by fileConfig.withDefaultWriteSave { "jLiBz0S2lSVPODeBTwnKT5B5Cxz8t5G6" } // it is persistent no need to change
        val fetchNoticeDelay by fileConfig.withDefaultWriteSave { 60 * 1000L }

        val antiRevokeGroups by lazy {
            fileConfig.setIfAbsent("antiRevokeGroups", listOf<Long>(187410654))
            fileConfig.getLongList("antiRevokeGroups")
        }
        val longwangLookupGroups by lazy {
            fileConfig.setIfAbsent("longwangLookupGroups", listOf<Long>(187410654))
            fileConfig.getLongList("longwangLookupGroups")
        }
        val noticeBroadcastGroups by lazy {
            fileConfig.setIfAbsent("noticeBroadcastGroups", listOf<Long>(187410654))
            fileConfig.getLongList("noticeBroadcastGroups")
        }
    }

    lateinit var latestNoticeTitle: String
    lateinit var fetchNoticeLoop: Job

    fun startFetchNoticeLoop() {
        database.sequenceOf(Models.Notices).lastOrNull().let {
            latestNoticeTitle = it?.titleWithAuthor ?: ""
        }
        fetchNoticeLoop = launch {
            while (true) {
                delay(config.fetchNoticeDelay)
                logger.info("Triggered fetchNotice")
                val notice = ChunHuiNotice.fetchNotice()
                if (notice.titleWithAuthor != latestNoticeTitle) {
                    latestNoticeTitle = notice.titleWithAuthor
                    val seq = database.sequenceOf(Models.Notices)
                    seq.add(notice)
                    val bot = Bot.getInstance(config.akiQQ)
                    bot.getFriend(config.rootUser).sendMessage(notice.toString())

                    val msg =
                        PlainText("校园公告@春晖：\n${notice.titleWithAuthor}\n\t${notice.date} | ${notice.relativeDate}")
                    for (gid in config.noticeBroadcastGroups) {
                        bot.getGroup(gid).sendMessage(msg)
                    }
                } else {
                    logger.info("No new notice found")
                }
            }
        }
    }

    override fun onLoad() {
//        database = Database.connect("jdbc:h2:miraki_db", "org.h2.Driver")
//        database = Database.connect("jdbc:sqlite:${config.databaseUrl}", "org.sqlite.JDBC", dialect = SQLiteDialect())
        database = Database.connect(
            config.databaseUrl,
            "org.postgresql.Driver",
            config.databaseUser,
            dialect = PostgreSqlDialect()
        )
    }

    override fun onEnable() {
        registerCommand {
            name = "aki"
            description = "管理 Aki 的运行"
            usage = """
                /aki save
                /aki dumpGroup <botQQ> <targetGroupId>
                /aki dumpNotice
                /aki startLoop
                /aki clearNotices
            """.trimIndent()
            onCommand {
                if (it.isEmpty()) return@onCommand false
                when (it[0].toLowerCase()) {
                    "save" -> {
                        config.fileConfig.save()
                        sendMessage("Done.")
                    }
                    "dumpgroup" -> {
                        val builder = MessageChainBuilder()
                        Bot.getInstance(it[1].toLong())
                            .getGroup(it[2].toLong())
                            .members
                            .forEach { m ->
                                builder.add("${m.id} ${m.nameCardOrNick}\n")
                            }
                        sendMessage(builder.asMessageChain())
                    }
                    "dumpnotice" -> {
                        val notice = ChunHuiNotice.fetchNotice()
                        sendMessage(notice.toString())
                    }
                    "startloop" -> {
                        if (!fetchNoticeLoop.isActive) {
                            startFetchNoticeLoop()
                            sendMessage("Started.")
                        } else {
                            sendMessage("The loop is already running!")
                        }
                    }
                    "clearnotices" -> {
                        database.deleteAll(Models.Notices)
                        latestNoticeTitle = ""
                        sendMessage("Done.")
                    }
                    else -> {
                        return@onCommand false
                    }
                }
                return@onCommand true
            }
        }

        subscribeGroupMessages {
            always {
                val msg = Models.StoredGroupMessage {
                    sourceId = source.id
                    messageTime = Instant.ofEpochSecond(source.time.toLong())
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

                    quoteReply(text)
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

                    quoteReply(poem.data.content)
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
                    try {
                        builder.add("No.${i}: ${group.members[row[t.senderId]!!].nameCardOrNick}: ${row[cnt]}\n")
                    } catch (e: NoSuchElementException) {
                        builder.add("No.${i}: 他挥了挥衣袖,不带走一片云彩: ${row[cnt]}\n")
                    }

                }
                quoteReply(builder.asMessageChain())
                    .recallIn(20000)
            }
        }

        subscribeAlways<MessageRecallEvent.GroupRecall> {
            if (operator == null) return@subscribeAlways
            if (authorId == bot.id && operator!!.id != config.rootUser) {
                if (Random.nextInt(2) == 1)
                    this.group.sendMessage(
                        PlainText("居然撤我消息") + Face(106) + PlainText("\n哼唧~ ╭(╯^╰)╮")
                    )
                return@subscribeAlways
            }

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
                if (this.group.id in config.antiRevokeGroups && Random.nextInt(5) == 1) {
                    this.group.sendMessage(
                        PlainText("哈哈~ 我看到了\n") + At(this.author) + "：“${it.text}”"
                    ).recallIn(20000)
                }
                it.flushChanges()
            }
        }

        startFetchNoticeLoop()

        logger.warning("Miraki enabled!")
    }

    override fun onDisable() {
        logger.warning("Miraki disabled!")
    }
}