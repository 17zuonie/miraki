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
import org.akteam.miraki.command.CommandExecutor
import org.akteam.miraki.command.subcommand.*
import org.akteam.miraki.listener.FuckLightAppListener
import org.akteam.miraki.listener.MListener
import org.akteam.miraki.listener.MusicVoteListener
import org.akteam.miraki.listener.NewFriendListener
import org.akteam.miraki.manager.TaskManager
import org.akteam.miraki.model.BotUsers
import org.akteam.miraki.tasks.ChunHuiNoticeUpdater
import org.akteam.miraki.web.WebMain
import java.time.LocalDateTime
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

object BotMain {
    const val version = "Miraki 3.0"
    lateinit var startTime: LocalDateTime
    var qq = 0L
    lateinit var password: String
    lateinit var bot: Bot
    lateinit var logger: MiraiLogger

    lateinit var service: ScheduledExecutorService

    suspend fun login() {
        val config = BotConfiguration.Default
        config.fileBasedDeviceInfo()
        bot = Bot(qq = qq, password = password, configuration = config).alsoLogin()
    }

    fun startUpTask() {
        TaskManager.runScheduleTaskAsync(
            ChunHuiNoticeUpdater::run,
            1L,
            1L,
            TimeUnit.MINUTES
        )
    }
}

fun main(args: Array<String>) = runBlocking<Unit> {
    BotMain.startTime = LocalDateTime.now()
    BotConsts.init()
    BotConsts.load()

    BotMain.qq = BotConsts.cfg.akiQQ
    BotMain.password = BotConsts.cfg.password
    if (BotMain.qq == 0L || BotMain.password == "") {
        println("请到 config.json 里填写机器人的QQ号&密码")
        exitProcess(0)
    }
    BotMain.login()
    Runtime.getRuntime().addShutdownHook(Thread {
        runBlocking {
            WebMain.server.stop(2000, 5000)
            BotMain.service.shutdown()
            BotMain.bot.close(null)
        }
    })

    BotMain.logger = BotMain.bot.logger

    CommandExecutor.setupCommand(
        arrayOf(
            VersionCommand(),
            HelpCommand(),
            MusicCommand(),
            NoticeCommand(),
            ManageCommand()
        )
    )

    val listeners: Array<MListener> = arrayOf(
        FuckLightAppListener,
        NewFriendListener,
        MusicVoteListener
    )

    /** 监听器 */
    listeners.forEach {
        it.register(BotMain.bot)
        BotMain.logger.info("[监听器] 已注册 ${it.name} 监听器")
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

    BotUsers.loadUsers()
    BotMain.logger.info("[用户] 已加载用户")

    BotMain.service = Executors.newScheduledThreadPool(4)
    BotMain.startUpTask()

    WebMain.run(args, wait = false)

    BotMain.bot.join() // 等待 Bot 离线, 避免主线程退出
    TODO("Retry login.")
}
