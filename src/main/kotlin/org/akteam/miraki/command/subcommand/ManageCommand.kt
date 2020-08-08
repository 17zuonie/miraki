package org.akteam.miraki.command.subcommand

import net.mamoe.mirai.message.MessageEvent
import org.akteam.miraki.BotVariables
import org.akteam.miraki.command.CommandProps
import org.akteam.miraki.command.UserCommand
import org.akteam.miraki.model.BotUser
import org.akteam.miraki.model.BotUsers
import org.akteam.miraki.model.UserLevel

class ManageCommand : UserCommand {
    override suspend fun execute(event: MessageEvent, args: List<String>, user: BotUser) {
        if (args.isNotEmpty()) {
            when (args[0]) {
                "save" -> {
                    BotVariables.save()
                    event.reply("配置保存了")
                }
                "userReload" -> {
                    BotUsers.loadUsers()
                    event.reply("用户已经重新加载")
                }
            }
        } else event.reply(help)
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