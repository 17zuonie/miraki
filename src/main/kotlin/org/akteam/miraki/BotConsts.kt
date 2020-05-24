package org.akteam.miraki

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.akteam.miraki.objects.Config
import java.io.File
import java.io.FileReader

object BotConsts {
    private val cfgFile = File("config.json")
    lateinit var cfg: Config

    val json = Json(
        JsonConfiguration.Stable.copy(
            prettyPrint = true
        )
    )

    fun init() {
        if (!cfgFile.exists()) {
            cfgFile.writeText(json.stringify(Config.serializer(), Config(100000L, "", 10000L)))
        }
    }

    fun load() {
        cfg = json.parse(Config.serializer(), FileReader(cfgFile).readText())
    }
}