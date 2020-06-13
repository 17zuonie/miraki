package org.akteam.miraki.command.subcommands

import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.EmptyMessageChain
import net.mamoe.mirai.message.data.MessageChain
import org.akteam.miraki.command.CommandProps
import org.akteam.miraki.command.UserCommand
import org.akteam.miraki.objects.BotUser
import org.akteam.miraki.objects.UserLevel
import org.akteam.miraki.utils.BotUtil.getRestString
import org.akteam.miraki.utils.FileUtil
import java.io.File

class UploadCommand : UserCommand {
    override suspend fun execute(event: MessageEvent, args: List<String>, user: BotUser): MessageChain {
        if (args.isNotEmpty()) {
            FileUtil.uploadToFFSup(File(args.getRestString(0)))
        }
        return EmptyMessageChain
    }

    override val props = CommandProps(
        "upload",
        arrayListOf("ul", "上传"),
        "上传文件"
    )

    override val level: UserLevel = UserLevel.ADMIN

    override val permission: String? = null

    override val help: String = "上传一个文件"

}