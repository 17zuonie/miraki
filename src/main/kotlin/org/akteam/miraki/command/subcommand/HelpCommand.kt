package org.akteam.miraki.command.subcommand

import net.mamoe.mirai.message.MessageEvent
import org.akteam.miraki.command.CommandProps
import org.akteam.miraki.command.GuestCommand
import org.akteam.miraki.command.MessageHandler

class HelpCommand : GuestCommand {
    override suspend fun execute(event: MessageEvent, args: List<String>) {
        val sb = StringBuilder()
        for (cmd in MessageHandler.getSimpleCommands()) {
            if (cmd.props.name.contentEquals("help") || !cmd.props.name.contentEquals("debug")) {
                sb.append("-").append(cmd.props.name).append("  ").append(cmd.props.description).append("\n")
            }
        }

        event.reply(sb.toString().trim())
    }

    override val props = CommandProps(
            "help",
            arrayListOf("?", "帮助", "菜单"),
            "帮助命令"
    )

    // 它自己就是帮助命令 不需要再帮了
    override val help: String = ""
}