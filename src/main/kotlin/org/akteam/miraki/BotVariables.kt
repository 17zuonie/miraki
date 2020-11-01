package org.akteam.miraki

import com.impossibl.postgres.jdbc.PGDataSource
import kotlinx.serialization.json.Json
import me.liuwj.ktorm.database.Database
import me.liuwj.ktorm.support.postgresql.PostgreSqlDialect
import net.mamoe.mirai.Bot
import net.mamoe.mirai.utils.MiraiLogger
import okhttp3.OkHttpClient
import org.akteam.miraki.model.Config
import java.io.File
import java.io.FileReader
import java.time.LocalDateTime
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

object BotVariables {
    const val version = "Miraki 4.0"

    private val cfgFile = File("config.json")
    lateinit var cfg: Config
    lateinit var bot: Bot
    lateinit var logger: MiraiLogger
    lateinit var db: Database

    lateinit var startTime: LocalDateTime
    lateinit var service: ScheduledExecutorService

    val json = Json {
        prettyPrint = true
        encodeDefaults = true
    }

    val http = OkHttpClient().newBuilder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    fun init() {
        startTime = LocalDateTime.now()
        service = Executors.newScheduledThreadPool(4)

        if (!cfgFile.exists()) {
            cfgFile.writeText(json.encodeToString(Config.serializer(), Config(100000L, "")))
        }
    }

    fun load() {
        cfg = json.decodeFromString(Config.serializer(), FileReader(cfgFile).readText())
        val ds = PGDataSource()
        ds.databaseUrl = cfg.databaseUrl
        ds.user = cfg.databaseUser
        ds.password = cfg.databasePassword
        db = Database.connect(ds, dialect = PostgreSqlDialect())
    }

    fun save() {
        cfgFile.writeText(json.encodeToString(Config.serializer(), cfg))
    }
}