package org.akteam.miraki.command.subcommand

import net.mamoe.mirai.message.MessageEvent
import org.akteam.miraki.api.ChunHuiApi
import org.akteam.miraki.command.CommandProps
import org.akteam.miraki.command.UserCommand
import org.akteam.miraki.model.BotUser
import org.akteam.miraki.model.ChunHuiNotice
import org.akteam.miraki.model.UserLevel
import org.akteam.miraki.tasks.ChunHuiNoticeUpdater

class NoticeCommand : UserCommand {
    override suspend fun execute(event: MessageEvent, args: List<String>, user: BotUser) {
        event.reply(if (args.isEmpty()) {
            val notice = ChunHuiApi.fetchNotice()
            "校园公告@春晖：\n${notice.titleWithAuthor}\n\t${notice.date} | ${notice.relativeDate}"
        } else {
            when (args[0]) {
                "订阅" -> {
                    user.subChunHuiNotice = !user.subChunHuiNotice
                    val ret = if (user.subChunHuiNotice) {
                        "现在你订阅了春晖网通知"
                    } else {
                        "现在你不再订阅春晖网通知了"
                    }
                    user.flushChanges()

                    ret
                }
                "clear" -> {
                    ChunHuiNoticeUpdater.latestNotice = ChunHuiNotice("", "", "")
                    "Notice 缓存已经清空"
                }
                else -> help
            }
        })
    }

    override val props =
            CommandProps("notice", arrayListOf("通知", "公告"), "获取春晖网通知")

    override val level: UserLevel = UserLevel.NORMAL

    override val permission: String? = null

    override val help: String = """
        ======= 命令帮助 =======
        -notice -> 获取春晖网通知
    """.trimIndent()
}