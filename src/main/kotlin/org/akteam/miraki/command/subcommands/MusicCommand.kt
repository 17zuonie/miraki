package org.akteam.miraki.command.subcommands

import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.EmptyMessageChain
import net.mamoe.mirai.message.data.MessageChain
import org.akteam.miraki.command.CommandProps
import org.akteam.miraki.command.UserCommand
import org.akteam.miraki.objects.BotUser
import org.akteam.miraki.objects.UserLevel
import org.akteam.miraki.utils.BotUtil
import org.akteam.miraki.utils.BotUtil.getRestString
import org.akteam.miraki.utils.MusicUtil
import org.akteam.miraki.utils.toMirai

class MusicCommand : UserCommand {
    override suspend fun execute(event: MessageEvent, args: List<String>, user: BotUser): MessageChain {
        if (BotUtil.isNoCoolDown(event.sender.id)) {
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