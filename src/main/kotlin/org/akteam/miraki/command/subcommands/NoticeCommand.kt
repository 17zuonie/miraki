package org.akteam.miraki.command.subcommands

import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.asMessageChain
import org.akteam.miraki.api.ChunHui
import org.akteam.miraki.command.CommandProps
import org.akteam.miraki.command.UserCommand
import org.akteam.miraki.objects.BotUser
import org.akteam.miraki.objects.UserLevel

class NoticeCommand : UserCommand {
    override suspend fun execute(event: MessageEvent, args: List<String>, user: BotUser): MessageChain {
        val notice = ChunHui.fetchNotice()
        return PlainText("校园公告@春晖：\n${notice.titleWithAuthor}\n\t${notice.date} | ${notice.relativeDate}").asMessageChain()
    }

    override val props =
        CommandProps("notice", arrayListOf("通知", "公告"), "获取春晖网通知")

    override val level: UserLevel = UserLevel.NORMAL

    override val permission: String? = null

    override val help: String = """
        ======= 命令帮助 =======
        /notice -> 获取春晖网通知
    """.trimIndent()
}