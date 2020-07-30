package org.akteam.miraki

import com.impossibl.postgres.jdbc.PGDataSource
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import me.liuwj.ktorm.database.Database
import me.liuwj.ktorm.support.postgresql.PostgreSqlDialect
import okhttp3.OkHttpClient
import org.akteam.miraki.model.Config
import java.io.File
import java.io.FileReader
import java.util.concurrent.TimeUnit

object BotConsts {
    private val cfgFile = File("config.json")
    lateinit var cfg: Config
    lateinit var db: Database

    val json = Json(
        JsonConfiguration.Stable.copy(
            prettyPrint = true
        )
    )

    val http = OkHttpClient().newBuilder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    fun init() {
        if (!cfgFile.exists()) {
            cfgFile.writeText(json.stringify(Config.serializer(), Config(100000L, "")))
        }
    }

    fun load() {
        cfg = json.parse(Config.serializer(), FileReader(cfgFile).readText())
        val ds = PGDataSource()
        ds.databaseUrl = cfg.databaseUrl
        ds.user = cfg.databaseUser
        ds.password = cfg.databasePassword
        db = Database.connect(ds, dialect = PostgreSqlDialect())
    }

    fun save() {
        cfgFile.writeText(json.stringify(Config.serializer(), cfg))
    }
}