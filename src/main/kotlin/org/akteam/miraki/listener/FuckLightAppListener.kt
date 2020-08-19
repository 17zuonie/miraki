package org.akteam.miraki.listener

import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import net.mamoe.mirai.Bot
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.message.data.LightApp
import org.akteam.miraki.BotVariables.json

object FuckLightAppListener : MListener {
    override fun register(bot: Bot) {
        bot.subscribeGroupMessages {
            always {
                val lightApp = message[LightApp]
                if (lightApp != null) {
                    val jsonObject = json.parseToJsonElement(lightApp.content).jsonObject
                    val prompt = jsonObject["prompt"]!!.jsonPrimitive.content
                    if (prompt.contentEquals("[QQ小程序]哔哩哔哩")) {
                        val meta = jsonObject["meta"]!!
                            .jsonObject["detail_1"]!!
                            .jsonObject
                        val title = meta["desc"]!!.jsonPrimitive.content
                        val url = meta["qqdocurl"]!!.jsonPrimitive.content
                        reply(
                            "天下无小程序 > 自动为电脑选手转换了小程序:\n" +
                                    "视频标题: $title\n" +
                                    "链接: ${url.substring(0, url.indexOf("?") - 1)}"
                        )
                    }
                }
            }
        }
    }

    override val name: String = "去你大爷的小程序"
}