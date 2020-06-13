package org.akteam.miraki.command.subcommands

import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.EmptyMessageChain
import net.mamoe.mirai.message.data.MessageChain
import org.akteam.miraki.BotConsts
import org.akteam.miraki.command.CommandProps
import org.akteam.miraki.command.UserCommand
import org.akteam.miraki.objects.BotUser
import org.akteam.miraki.objects.BotUsers
import org.akteam.miraki.objects.UserLevel
import org.akteam.miraki.utils.toMirai

class ManageCommand : UserCommand {
    override suspend fun execute(event: MessageEvent, args: List<String>, user: BotUser): MessageChain {
        if (args.isNotEmpty()) {
            when (args[0]) {
                "save" -> {
                    BotConsts.save()
                    return "配置保存了".toMirai()
                }
                "userReload" -> {
                    BotUsers.loadUsersFromGroup()
                    return "用户已经重新加载".toMirai()
                }
            }
        } else return help.toMirai()
        return EmptyMessageChain
    }

    override val props =
        CommandProps("manage", null, "配置")

    override val level: UserLevel = UserLevel.ADMIN

    override val permission: String? = null

    override val help: String = """
        ======= 命令帮助 =======
        /manage save -> 保存配置文件
        /manage userReload -> 重新加载用户
    """.trimIndent()
}