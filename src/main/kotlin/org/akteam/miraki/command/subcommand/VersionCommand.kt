package org.akteam.miraki.command.subcommand

import net.mamoe.mirai.message.MessageEvent
import org.akteam.miraki.BotVariables
import org.akteam.miraki.command.CommandProps
import org.akteam.miraki.command.GuestCommand
import org.akteam.miraki.command.MessageHandler
import org.akteam.miraki.util.BotUtils

class VersionCommand : GuestCommand {
    override suspend fun execute(event: MessageEvent, args: List<String>) {
        event.reply(("Miraki " + BotVariables.version +
                "\n已注册命令数: " + MessageHandler.countSimpleCommands() +
                "\n运行时长 ${BotUtils.getRunningTime()}" +
                "\nMade with ❤ & Mirai 1.1.3"))
    }

    override val props = CommandProps(
            "version",
            arrayListOf("v", "版本"),
            "查看版本号"
    )

    override val help = ""
}