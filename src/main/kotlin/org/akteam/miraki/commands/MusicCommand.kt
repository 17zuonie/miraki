package org.akteam.miraki.commands

import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.EmptyMessageChain
import net.mamoe.mirai.message.data.MessageChain
import org.akteam.miraki.utils.BotUtil
import org.akteam.miraki.utils.BotUtil.getRestString
import org.akteam.miraki.utils.MusicUtil
import org.akteam.miraki.utils.toMirai

class MusicCommand : UniversalCommand {
    override suspend fun execute(event: MessageEvent, args: List<String>): MessageChain {
        if (BotUtil.isNoCoolDown(event.sender.id)) {
            if (args.isNotEmpty()) {
                return if (args[0] == "下载") {
                    MusicUtil.searchNetEaseMusic(args.getRestString(1), directLink = true)
                } else MusicUtil.searchNetEaseMusic(args.getRestString(0))
            } else {
                return getHelp().toMirai()
            }
        }
        return EmptyMessageChain
    }

    override fun getProps(): CommandProps =
        CommandProps("music", arrayListOf("dg", "点歌", "歌"), "点歌命令")

    override fun getHelp(): String = """
        ======= 命令帮助 =======
        /music [歌名] -> 点歌
        /music 下载 [歌名] -> 获得音乐 MP3 链接
    """.trimIndent()
}