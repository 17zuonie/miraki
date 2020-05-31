package org.akteam.miraki

import com.impossibl.postgres.jdbc.PGDataSource
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import me.liuwj.ktorm.database.Database
import me.liuwj.ktorm.support.postgresql.PostgreSqlDialect
import org.akteam.miraki.objects.Config
import java.io.File
import java.io.FileReader

object BotConsts {
    private val cfgFile = File("config.json")
    lateinit var cfg: Config
    lateinit var db: Database

    val json = Json(
        JsonConfiguration.Stable.copy(
            prettyPrint = true
        )
    )

    fun init() {
        if (!cfgFile.exists()) {
            cfgFile.writeText(json.stringify(Config.serializer(), Config(100000L, "", 10000L)))
        }
/*        db = Database.connect(
            cfg.databaseUrl,
            "org.postgresql.Driver",
            cfg.databaseUser,
            dialect = PostgreSqlDialect()
        )*/
    }

    fun load() {
        cfg = json.parse(Config.serializer(), FileReader(cfgFile).readText())
        val ds = PGDataSource()
        ds.databaseUrl = cfg.databaseUrl
        ds.user = cfg.databaseUser
        db = Database.connect(ds, dialect = PostgreSqlDialect())
    }
}