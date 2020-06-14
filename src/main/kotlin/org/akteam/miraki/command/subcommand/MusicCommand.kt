package org.akteam.miraki.command.subcommand

import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.EmptyMessageChain
import net.mamoe.mirai.message.data.MessageChain
import org.akteam.miraki.command.CommandProps
import org.akteam.miraki.command.UserCommand
import org.akteam.miraki.model.BotUser
import org.akteam.miraki.model.UserLevel
import org.akteam.miraki.util.BotUtils
import org.akteam.miraki.util.BotUtils.getRestString
import org.akteam.miraki.util.MusicUtil
import org.akteam.miraki.util.toMirai

class MusicCommand : UserCommand {
    override suspend fun execute(event: MessageEvent, args: List<String>, user: BotUser): MessageChain {
        if (BotUtils.isNoCoolDown(event.sender.id)) {
            return if (args.isNotEmpty()) {
                if (args[0] == "下载") {
                    MusicUtil.searchNetEaseMusic(args.getRestString(1), directLink = true)
                } else MusicUtil.searchNetEaseMusic(args.getRestString(0))
            } else {
                help.toMirai()
            }
        }
        return EmptyMessageChain
    }

    override val props =
        CommandProps("music", arrayListOf("dg", "点歌", "歌"), "点歌命令")

    override val level: UserLevel = UserLevel.NORMAL

    override val permission: String? = null

    override val help: String = """
        ======= 命令帮助 =======
        /music [歌名] -> 点歌
        /music 下载 [歌名] -> 获得音乐 MP3 链接
    """.trimIndent()
}