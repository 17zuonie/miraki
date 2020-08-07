package org.akteam.miraki.command.subcommand

import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.EmptyMessageChain
import net.mamoe.mirai.message.data.MessageChain
import org.akteam.miraki.BotVariables
import org.akteam.miraki.command.CommandProps
import org.akteam.miraki.command.UserCommand
import org.akteam.miraki.model.BotUser
import org.akteam.miraki.model.BotUsers
import org.akteam.miraki.model.UserLevel
import org.akteam.miraki.util.toMsgChain

class ManageCommand : UserCommand {
    override suspend fun execute(event: MessageEvent, args: List<String>, user: BotUser): MessageChain {
        if (args.isNotEmpty()) {
            when (args[0]) {
                "save" -> {
                    BotVariables.save()
                    return "配置保存了".toMsgChain()
                }
                "userReload" -> {
                    BotUsers.loadUsers()
                    return "用户已经重新加载".toMsgChain()
                }
            }
        } else return help.toMsgChain()
        return EmptyMessageChain
    }

    override val props =
        CommandProps("manage", null, "配置")

    override val level: UserLevel = UserLevel.ADMIN

    override val permission: String? = null

    override val help: String = """
        ======= 命令帮助 =======
        -manage save -> 保存配置文件
        -manage userReload -> 重新加载用户
    """.trimIndent()
}