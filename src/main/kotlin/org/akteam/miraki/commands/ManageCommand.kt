package org.akteam.miraki.commands

import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.EmptyMessageChain
import net.mamoe.mirai.message.data.MessageChain
import org.akteam.miraki.BotConsts
import org.akteam.miraki.objects.UserLevel
import org.akteam.miraki.utils.toMirai

class ManageCommand : UniversalCommand {
    override suspend fun execute(event: MessageEvent, args: List<String>): MessageChain {
        if (args.isNotEmpty()) {
            when (args[0]) {
                "save" -> {
                    BotConsts.save()
                    return "配置保存了".toMirai()
                }
            }
        }
        return EmptyMessageChain
    }

    override val props = CommandProps("manage", null, "配置", UserLevel.ADMIN)

    override fun getHelp(): String = ""
}