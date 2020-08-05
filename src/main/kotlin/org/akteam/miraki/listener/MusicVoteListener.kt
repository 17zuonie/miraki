package org.akteam.miraki.listener

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.content
import me.liuwj.ktorm.dsl.*
import me.liuwj.ktorm.entity.add
import me.liuwj.ktorm.entity.filter
import me.liuwj.ktorm.entity.firstOrNull
import me.liuwj.ktorm.entity.sequenceOf
import net.mamoe.mirai.Bot
import net.mamoe.mirai.event.subscribeFriendMessages
import net.mamoe.mirai.message.data.LightApp
import org.akteam.miraki.BotConsts
import org.akteam.miraki.model.BotUsers
import org.akteam.miraki.model.Playlists
import org.akteam.miraki.model.RecommendMusic
import org.akteam.miraki.model.RecommendMusics
import java.time.Instant

object MusicVoteListener : MListener {

    override fun register(bot: Bot) {
        bot.subscribeFriendMessages {
            always {
                val card = message[LightApp]
                if (card != null) {
                    val jsonObject = BotConsts.json.parseJson(card.content).jsonObject
                    val meta = jsonObject["meta"]!!.jsonObject
                    val music = meta["music"]
                    if (music != null && music is JsonObject) {
                        if (BotUsers.get(qq = sender.id) == null) return@always
                        val now = Instant.now()!!
                        val playlist = BotConsts.db.sequenceOf(Playlists).filter {
                            (Playlists.startTime less now) and
                                    (Playlists.endTime.isNull() or (Playlists.endTime greater now))
                        }.firstOrNull()
                        if (playlist == null) {
                            reply("投稿已结束或者未开始，看准时间再来")
                            return@always
                        }
                        val new = RecommendMusic {
                            qq = sender.id
                            subTime = Instant.now()

                            like = 1
                            playlistId = playlist.n
                            confirmed = false

                            title = music["title"]!!.content
                            artist = music["desc"]!!.content
                            platform = music["tag"]!!.content

                            jumpUrl = music["jumpUrl"]!!.content
                            musicUrl = music["musicUrl"]!!.content
                            previewUrl = music["preview"]!!.content
                        }
                        val seq = BotConsts.db.sequenceOf(RecommendMusics)
                        for (m in seq) {
                            if (new.eq(m)) {
                                reply("抱歉啦，这首歌已经放在列表里了哦")
                                return@always
                            }
                        }
                        seq.add(new)
                        reply("投稿成功了！")
                    }
                }
            }
        }
    }

    override val name: String = "午休歌投稿"

}