package org.akteam.miraki

import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.Bot
import net.mamoe.mirai.alsoLogin
import net.mamoe.mirai.join
import net.mamoe.mirai.utils.BotConfiguration
import org.akteam.miraki.command.MessageHandler
import org.akteam.miraki.command.subcommand.*
import org.akteam.miraki.listener.FuckLightAppListener
import org.akteam.miraki.listener.MListener
import org.akteam.miraki.listener.MusicVoteListener
import org.akteam.miraki.listener.NewFriendListener
import org.akteam.miraki.manager.TaskManager
import org.akteam.miraki.model.BotUsers
import org.akteam.miraki.tasks.ChunHuiNoticeUpdater
import org.akteam.miraki.web.WebMain
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

object BotMain {
    suspend fun start(qq: Long, password: String) {
        val config = BotConfiguration.Default
        config.fileBasedDeviceInfo()
        BotVariables.bot = Bot(qq = qq, password = password, configuration = config).alsoLogin()
        BotVariables.logger = BotVariables.bot.logger
        BotVariables.bot.alsoLogin()

        BotUsers.loadUsers()
        BotVariables.logger.info("[用户] 已加载用户")

        startUpTask()

        MessageHandler.setupSimpleCommand(
                arrayOf(
                        VersionCommand(),
                        HelpCommand(),
                        MusicCommand(),
                        NoticeCommand(),
                        ManageCommand()
                )
        )
        BotVariables.logger.info("[命令] 已注册 ${MessageHandler.countSimpleCommands()} 个简单命令")

        MessageHandler.setupNaturalCommand(
                GuessNumberCommand()
        )
        BotVariables.logger.info("[命令] 已注册 ${MessageHandler.countNaturalCommands()} 个自然命令")

        MessageHandler.start(BotVariables.bot)

        val listeners: Array<MListener> = arrayOf(
                FuckLightAppListener,
                NewFriendListener,
                MusicVoteListener
        )

        /** 监听器 */
        listeners.forEach {
            it.register(BotVariables.bot)
            BotVariables.logger.info("[监听器] 已注册 ${it.name} 监听器")
        }

        Runtime.getRuntime().addShutdownHook(Thread {
            runBlocking {
                WebMain.server.stop(2000, 5000)
                BotVariables.service.shutdown()
                BotVariables.bot.close(null)
            }
        })
    }

    private fun startUpTask() {
        TaskManager.runScheduleTaskAsync(
                ChunHuiNoticeUpdater::run,
                BotVariables.cfg.fetchNoticeDelay,
                BotVariables.cfg.fetchNoticeDelay,
                TimeUnit.SECONDS
        )
    }
}

fun main(args: Array<String>) = runBlocking<Unit> {
    BotVariables.init()
    BotVariables.load()

    if (BotVariables.cfg.akiQQ == 0L || BotVariables.cfg.password == "") {
        println("请到 config.json 里填写机器人的QQ号&密码")
        exitProcess(0)
    }

    BotMain.start(BotVariables.cfg.akiQQ, BotVariables.cfg.password)
    WebMain.run(args, wait = false)

    BotVariables.bot.join() // 等待 Bot 离线, 避免主线程退出
    TODO("Retry login.")
}
