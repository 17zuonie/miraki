package org.akteam.miraki

import net.mamoe.mirai.Bot
import net.mamoe.mirai.alsoLogin
import net.mamoe.mirai.event.subscribeMessages
import net.mamoe.mirai.join
import net.mamoe.mirai.message.data.EmptyMessageChain
import net.mamoe.mirai.utils.BotConfiguration
import net.mamoe.mirai.utils.MiraiLogger
import org.akteam.miraki.commands.CommandExecutor
import org.akteam.miraki.commands.HelpCommand
import org.akteam.miraki.commands.VersionCommand
import kotlin.system.exitProcess

object BotMain {
    const val version = "Miraki 2.2"
    var startTime: Long = 0
    var qq = 0L
    lateinit var password: String
    lateinit var bot: Bot
    lateinit var logger: MiraiLogger

    suspend fun login() {
        val config = BotConfiguration.Default
        config.fileBasedDeviceInfo()
        bot = Bot(qq = BotMain.qq, password = BotMain.password, configuration = config)
        bot.alsoLogin()
    }
}

suspend fun main() {
    BotMain.startTime = System.currentTimeMillis()
    BotConsts.init()
    BotConsts.load()

    BotMain.qq = BotConsts.cfg.akiQQ
    BotMain.password = BotConsts.cfg.password
    if (BotMain.qq == 0L || BotMain.password == "") {
        println("请到 config.json 里填写机器人的QQ号&密码")
        exitProcess(0)
    }
    BotMain.login()
    BotMain.logger = BotMain.bot.logger

    CommandExecutor.setupCommand(
        arrayOf(
            VersionCommand(),
            HelpCommand()
        )
    )

    BotMain.bot.subscribeMessages {
        always {
            if (sender.id != 80000000L) {
                val result = CommandExecutor.execute(this)
                if (result !is EmptyMessageChain) {
                    reply(result)
                }
            }
        }
    }

    BotMain.bot.join() // 等待 Bot 离线, 避免主线程退出
    TODO("Retry login.")
}
