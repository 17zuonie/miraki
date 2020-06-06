package org.akteam.miraki

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.Bot
import net.mamoe.mirai.alsoLogin
import net.mamoe.mirai.event.Listener
import net.mamoe.mirai.event.subscribeMessages
import net.mamoe.mirai.join
import net.mamoe.mirai.message.data.EmptyMessageChain
import net.mamoe.mirai.utils.BotConfiguration
import net.mamoe.mirai.utils.MiraiLogger
import org.akteam.miraki.commands.*
import org.akteam.miraki.listeners.FuckLightAppListener
import org.akteam.miraki.listeners.MListener
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
        bot = Bot(qq = qq, password = password, configuration = config)
        bot.alsoLogin()
    }
}

fun main() = runBlocking<Unit> {
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
            HelpCommand(),
            MusicCommand(),
            NoticeCommand(),
            ManageCommand()
//            UploadCommand()
        )
    )

    val listeners: Array<MListener> = arrayOf(
        FuckLightAppListener
    )

    /** 监听器 */
    listeners.forEach {
        it.register(BotMain.bot)
        BotMain.logger.info("[监听器] 已注册 ${it.getName()} 监听器")
    }

    BotMain.bot.subscribeMessages(priority = Listener.EventPriority.NORMAL) {
        always {
            if (sender.id != 80000000L) {
                launch {
                    val result = CommandExecutor.execute(this@always)
                    if (result !is EmptyMessageChain) reply(result)
                }
            }
        }
    }

    BotMain.logger.info("[命令] 已注册 " + CommandExecutor.commands.size + " 个命令")

    BotMain.bot.join() // 等待 Bot 离线, 避免主线程退出
    TODO("Retry login.")
}
