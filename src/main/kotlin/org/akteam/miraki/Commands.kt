package org.akteam.miraki

import me.liuwj.ktorm.dsl.deleteAll
import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.command.registerCommand
import net.mamoe.mirai.contact.nameCardOrNick
import net.mamoe.mirai.message.data.MessageChainBuilder

object Commands {
    fun register() {
        Miraki.registerCommand {
            name = "aki"
            description = "管理 Aki 的运行"
            usage = """
                /aki save
                /aki dumpGroup <botQQ> <targetGroupId>
                /aki dumpNotice
                /aki startLoop
                /aki clearNotices
            """.trimIndent()
            onCommand {
                if (it.isEmpty()) return@onCommand false
                when (it[0].toLowerCase()) {
                    "save" -> {
                        Miraki.Config.fileConfig.save()
                        sendMessage("Done.")
                    }
                    "dumpgroup" -> {
                        val builder = MessageChainBuilder()
                        Bot.getInstance(it[1].toLong())
                            .getGroup(it[2].toLong())
                            .members
                            .forEach { m ->
                                builder.add("${m.id} ${m.nameCardOrNick}\n")
                            }
                        sendMessage(builder.asMessageChain())
                    }
                    "dumpnotice" -> {
                        val notice = ChunHuiNotice.fetchNotice()
                        sendMessage(notice.toString())
                    }
                    "startloop" -> {
                        if (!Miraki.fetchNoticeLoop.isActive) {
                            Miraki.startFetchNoticeLoop()
                            sendMessage("Started.")
                        } else {
                            sendMessage("The loop is already running!")
                        }
                    }
                    "clearnotices" -> {
                        Miraki.database.deleteAll(Models.Notices)
                        Miraki.latestNoticeTitle = ""
                        sendMessage("Done.")
                    }
                    else -> {
                        return@onCommand false
                    }
                }
                return@onCommand true
            }
        }
    }
}