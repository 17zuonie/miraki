package org.akteam.miraki.commands

import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.MessageChain
import org.akteam.miraki.objects.BotUser
import org.akteam.miraki.utils.toMirai

class HelpCommand : UniversalCommand {
    override suspend fun execute(event: MessageEvent, args: List<String>, user: BotUser): MessageChain {
        val sb = StringBuilder()
        for (cmd in CommandExecutor.commands) {
            if (cmd.getProps().name.contentEquals("help") || !cmd.getProps().name.contentEquals("debug")) {
                sb.append("/").append(cmd.getProps().name).append("  ").append(cmd.getProps().description).append("\n")
            }
        }

        return sb.toString().trim().toMirai()
    }

    override fun getProps(): CommandProps =
        CommandProps("help", arrayListOf("?", "帮助", "菜单"), "帮助命令")

    // 它自己就是帮助命令 不需要再帮了
    override fun getHelp(): String = ""
}