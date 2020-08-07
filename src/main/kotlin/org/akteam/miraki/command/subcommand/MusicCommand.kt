package org.akteam.miraki.command.subcommand

import me.liuwj.ktorm.dsl.eq
import me.liuwj.ktorm.entity.add
import me.liuwj.ktorm.entity.find
import me.liuwj.ktorm.entity.sequenceOf
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.asMessageChain
import org.akteam.miraki.BotVariables
import org.akteam.miraki.command.CommandProps
import org.akteam.miraki.command.UserCommand
import org.akteam.miraki.model.BotUser
import org.akteam.miraki.model.Playlist
import org.akteam.miraki.model.Playlists
import org.akteam.miraki.model.UserLevel
import org.akteam.miraki.util.BotUtils
import org.akteam.miraki.util.BotUtils.getRestString
import org.akteam.miraki.util.MusicUtil
import org.akteam.miraki.util.toMsgChain
import org.akteam.miraki.web.JwtConfig
import java.time.Instant

class MusicCommand : UserCommand {
    override suspend fun execute(event: MessageEvent, args: List<String>, user: BotUser): MessageChain {
        return if (args.isNotEmpty()) {
            when (args[0]) {
                "创建歌单" -> {
                    if (user.hasPermission(UserLevel.ADMIN)) "没有权限".toMsgChain()
                    try {
                        val pl = Playlist {
                            startTime = Instant.now()
                            endTime = null
                        }
                        BotVariables.db.sequenceOf(Playlists).add(pl)
                        "成功，歌单 ID 为 ${pl.n}".toMsgChain()
                    } catch (e: Exception) {
                        "出现错误 ${e.message}".toMsgChain()
                    }
                }
                "终止歌单" -> {
                    if (user.hasPermission(UserLevel.ADMIN)) "没有权限".toMsgChain()
                    try {
                        val pl = BotVariables.db.sequenceOf(Playlists).find { it.n eq args[1].toInt() }
                        if (pl == null) "失败，歌单不存在".toMsgChain()
                        else {
                            pl.endTime = Instant.now()
                            pl.flushChanges()
                            "成功，歌单 ${pl.n} 已终止".toMsgChain()
                        }
                    } catch (e: Exception) {
                        "出现错误 ${e.message}".toMsgChain()
                    }
                }
                "投票" -> {
                    BotUtils.sendLinkCard(
                        "专属午休歌投票通道",
                        "有效期十五分钟，请不要泄漏给别人哦",
                        "${BotVariables.cfg.httpApiUrl}#/auth/${JwtConfig.makeToken(user)}",
                        "[链接]午休歌投票"
                    ).asMessageChain()
                }
                "下载" -> MusicUtil.searchNetEaseMusic(args.getRestString(1), directLink = true)
                else -> MusicUtil.searchNetEaseMusic(args.getRestString(1))
            }
        } else {
            help.toMsgChain()
        }
    }

    override val props =
        CommandProps("music", arrayListOf("dg", "点歌", "歌"), "点歌命令")

    override val level: UserLevel = UserLevel.NORMAL

    override val permission: String? = null

    override val help: String = """
        ======= 命令帮助 =======
        直接把歌曲分享过来 -> 投稿
        -music [歌名] -> 点歌
        -music 下载 [歌名] -> 获得音乐 MP3 链接
        -music 投票 -> 获得投票链接
        -music 创建歌单 -> 新建一个歌单
        -music 终止歌单 <歌单ID> -> 结束指定 ID 的歌单的投稿
    """.trimIndent()
}