package org.akteam.miraki.utils

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.content
import kotlinx.serialization.json.int
import net.mamoe.mirai.message.data.LightApp
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.asMessageChain
import okhttp3.OkHttpClient
import okhttp3.Request
import org.akteam.miraki.BotConsts
import org.akteam.miraki.BotMain
import org.akteam.miraki.utils.BotUtil.get
import org.akteam.miraki.utils.BotUtil.readText
import java.io.IOException
import java.net.URLEncoder

object MusicUtil {
    /** 1分钟100次，10分钟500次，1小时2000次 */
    private const val thirdPartyApi = "https://api.qq.jsososo.com/song/urls?id="
    private val client = OkHttpClient()
    private val json = Json(JsonConfiguration.Stable)

    suspend fun searchNetEaseMusic(songName: String, directLink: Boolean = false): MessageChain {
        try {
            BotMain.logger.debug(
                "http://${BotConsts.cfg.netEaseApi}/search?keywords=${URLEncoder.encode(
                    songName,
                    "UTF-8"
                )}"
            )
            val rep = client.get(
                "http://${BotConsts.cfg.netEaseApi}/search?keywords=${URLEncoder.encode(
                    songName,
                    "UTF-8"
                )}"
            )
            if (rep.isSuccessful) {
                val searchResult = json.parseJson(rep.readText()).jsonObject
                val musicId = searchResult
                    .getObject("result")
                    .getArray("songs")[0]
                    .jsonObject["id"]!!
                    .int
                val musicUrl = "https://music.163.com/#/song?id=$musicId"
                val songResult = client.get("http://${BotConsts.cfg.netEaseApi}/song/detail?ids=$musicId").readText()

                BotMain.logger.debug("http://${BotConsts.cfg.netEaseApi}/song/detail?ids=$musicId")

                val songJson = json.parseJson(songResult)
                val albumUrl = songJson
                    .jsonObject["songs"]!!
                    .jsonArray[0]
                    .jsonObject["al"]!!
                    .jsonObject["picUrl"]!!
                    .content
                val name = songJson
                    .jsonObject["songs"]!!
                    .jsonArray[0]
                    .jsonObject["name"]!!
                    .content
                var artistName = ""

                songJson.jsonObject["songs"]!!.jsonArray[0].jsonObject["ar"]!!.jsonArray.forEach {
                    artistName += (it.jsonObject["name"]!!.content + "/")
                }

                artistName = artistName.substring(0, artistName.length - 1)

                val playResult = if (BotConsts.cfg.netEaseApi.isEmpty()) {
                    client.get("http://${BotConsts.cfg.netEaseApi}/song/url?id=$musicId")
                } else {
                    val req = Request.Builder()
                        .url("http://${BotConsts.cfg.netEaseApi}/song/url?id=$musicId")
                        .header("Cookie", BotConsts.cfg.netEaseCookie)
                        .build()
                    BotMain.logger.debug(req.headers.toString())

                    client.get(req)
                }

                if (playResult.isSuccessful) {
                    val playJson = json.parseJson(playResult.readText()).jsonObject
                    val playUrl = playJson["data"]!!
                        .jsonArray[0]
                        .jsonObject["url"]!!
                        .content

                    return if (!directLink) {
                        LightApp(
                            """
                            {
                                "app": "com.tencent.structmsg",
                                "desc": "音乐",
                                "meta": {
                                    "music": {
                                        "action": "",
                                        "android_pkg_name": "",
                                        "app_type": 1,
                                        "appid": "100495085",
                                        "desc": "$artistName",
                                        "jumpUrl": "$musicUrl",
                                        "musicUrl": "$playUrl",
                                        "preview": "$albumUrl",
                                        "sourceMsgId": 0,
                                        "source_icon": "",
                                        "source_url": "",
                                        "tag": "网易云音乐",
                                        "title": "$name"
                                    }
                                },
                                "prompt": "[分享]$name",
                                "ver": "0.0.0.1",
                                "view": "music"
                            }
                        """.trimIndent()
                        ).asMessageChain()
                    } else {
                        return playUrl.toMirai()
                    }
                }
            }
        } catch (e: IOException) {
            BotMain.logger.error(e)
        }
        return "找不到歌曲".toMirai()
    }

/*
    fun searchQQMusic(name: String): MessageChain {
        try {
            val songResult = HttpRequest.get(
                "https://c.y.qq.com/soso/fcgi-bin/client_search_cp?g_tk=5381&p=1&n=20&w=${URLEncoder.encode(
                    name,
                    "UTF-8"
                )}&format=json&loginUin=0&hostUin=0&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&remoteplace=txt.yqq.song&t=0&aggr=1&cr=1&catZhida=0&flag_qc=0"
            ).timeout(8000).executeAsync()
            if (songResult.isOk) {
                val json = JsonParser.parseString(songResult.body())
                if (!json.isJsonNull) {
                    val info = json.asJsonObject["data"].asJsonObject["song"]["list"].asJsonArray[0].asJsonObject
                    val mid = info["songmid"].asString
                    val songName = info["songname"].asString
                    val songId = info["songid"].asInt
                    val albumId = info["albumid"]
                    val playResult = HttpRequest.get("$thirdPartyApi$mid").executeAsync()
                    if (playResult.isOk) {
                        val playJson = JsonParser.parseString(playResult.body())
                        val playUrl = playJson.asJsonObject["data"].asJsonObject[mid].asString

                        var artistName = ""

                        info["singer"].asJsonArray.forEach {
                            run {
                                artistName += (it.asJsonObject["name"].asString + "/")
                            }
                        }

                        artistName = artistName.substring(0, artistName.length - 1)

                        return LightApp(
                            "{\n" +
                                    "    \"app\": \"com.tencent.structmsg\",\n" +
                                    "    \"desc\": \"音乐\",\n" +
                                    "    \"view\": \"music\",\n" +
                                    "    \"ver\": \"0.0.0.1\",\n" +
                                    "    \"prompt\": \"[分享] $songName\",\n" +
                                    "    \"meta\": {\n" +
                                    "        \"music\": {\n" +
                                    "            \"action\": \"\",\n" +
                                    "            \"android_pkg_name\": \"\",\n" +
                                    "            \"app_type\": 1,\n" +
                                    "            \"appid\": 100497308,\n" +
                                    "            \"desc\": \"$artistName\",\n" +
                                    "            \"jumpUrl\": \"http://y.qq.com/#type=song&id=$songId\",\n" +
                                    "            \"musicUrl\": \"$playUrl\",\n" +
                                    "            \"preview\": \"http://imgcache.qq.com/music/photo/album_300/17/300_albumpic_${albumId}_0.jpg\",\n" +
                                    "            \"sourceMsgId\": \"0\",\n" +
                                    "            \"source_icon\": \"\",\n" +
                                    "            \"source_url\": \"\",\n" +
                                    "            \"tag\": \"QQ音乐\",\n" +
                                    "            \"title\": \"$songName\"\n" +
                                    "        }\n" +
                                    "    }\n" +
                                    "}"
                        ).asMessageChain()
                    }
                }
            } else BotMain.logger.debug("无法从 API 获取到歌曲信息, 响应码为 " + songResult.status)
        } catch (x: Exception) {
            BotMain.logger.error("在通过 QQ 音乐搜索歌曲时发生了一个错误, ", x)
        }
        return "找不到歌曲".toMessage().asMessageChain()
    }
*/

}