package org.akteam.miraki.command.subcommand

import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.EmptyMessageChain
import net.mamoe.mirai.message.data.MessageChain
import org.akteam.miraki.BotMain
import org.akteam.miraki.command.CommandExecutor
import org.akteam.miraki.command.CommandProps
import org.akteam.miraki.command.GuestCommand
import org.akteam.miraki.util.BotUtils
import org.akteam.miraki.util.toMirai

class VersionCommand : GuestCommand {
    override suspend fun execute(event: MessageEvent, args: List<String>): MessageChain {
        if (BotUtils.isNoCoolDown(event.sender.id)) {
            return ("Mirai " + BotMain.version + "\n已注册的命令个数: " + CommandExecutor.commands.size +
                    "\n运行时间: ${BotUtils.getRunningTime()}" +
                    "\nMade with ❤, Running on Mirai").toMirai()
        }
        return EmptyMessageChain
    }

    override val props = CommandProps(
        "version",
        arrayListOf("v", "版本"),
        "查看版本号"
    )

    override val help = ""
}