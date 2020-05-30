package org.akteam.miraki.commands

import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.EmptyMessageChain
import net.mamoe.mirai.message.data.MessageChain
import org.akteam.miraki.utils.BotUtil.getRestString
import org.akteam.miraki.utils.FileUtil
import java.io.File

class UploadCommand : UniversalCommand {
    override suspend fun execute(event: MessageEvent, args: List<String>): MessageChain {
        if (args.isNotEmpty()) {
            FileUtil.uploadToFFSup(File(args.getRestString(0)))
        }
        return EmptyMessageChain
    }

    override fun getProps(): CommandProps =
        CommandProps("upload", arrayListOf("ul", "上传"), "上传文件")

    override fun getHelp(): String {
        TODO("Not yet implemented")
    }

}