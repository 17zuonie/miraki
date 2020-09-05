package org.akteam.miraki.command

import kotlinx.coroutines.TimeoutCancellationException
import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.isBotMuted
import net.mamoe.mirai.event.subscribeMessages
import net.mamoe.mirai.message.GroupMessageEvent
import net.mamoe.mirai.message.MessageEvent
import org.akteam.miraki.BotVariables
import org.akteam.miraki.manager.SessionManager
import org.akteam.miraki.model.BotUser
import org.akteam.miraki.model.BotUsers
import org.akteam.miraki.util.toMsgChain
import java.time.Duration
import java.time.LocalDateTime

/**
 * Mirai 命令处理器
 * 处理群聊/私聊聊天信息中存在的命令
 * @author Nameless
 */
object MessageHandler {
    private var simpleCommands: List<SimpleCommand> = mutableListOf()
    private var naturalCommands: List<NaturalCommand> = mutableListOf()

    /**
     * 注册命令
     *
     * @param command 要注册的命令
     */
    private fun setupSimpleCommand(command: SimpleCommand) {
        if (!simpleCommands.contains(command)) {
            simpleCommands = simpleCommands + command
        } else {
            BotVariables.logger.warning("[命令] 正在尝试注册已有简单命令 ${command.props.name}")
        }
    }

    /**
     * 注册命令
     *
     * @param commands 要注册的命令集合
     */
    fun setupSimpleCommand(commands: Array<SimpleCommand>) {
        commands.forEach {
            setupSimpleCommand(it)
        }
    }

    fun setupNaturalCommand(command: NaturalCommand) {
        if (!naturalCommands.contains(command)) {
            naturalCommands = naturalCommands + command
        } else {
            BotVariables.logger.warning("[命令] 正在尝试注册已有自然命令 ${command.name}")
        }
    }

    fun setupNaturalCommand(commands: Array<NaturalCommand>) {
        commands.forEach {
            setupNaturalCommand(it)
        }
    }

    fun start(bot: Bot) {
        bot.subscribeMessages {
            always {
                if (sender.id != 80000000L) {
                    if (this is GroupMessageEvent && group.isBotMuted) return@always
                    val senderId = sender.id
                    val user = BotUsers.get(senderId)

                    if (SessionManager.executeSession(this, user)) return@always
                    if (executeSimpleCommand(this, user)) return@always
                    executeNaturalCommand(this, user)
                }
            }
        }
    }

    /**
     * 执行消息中的命令
     *
     * @param event Mirai 消息命令 (聊天)
     */
    private suspend fun executeSimpleCommand(event: MessageEvent, user: BotUser?): Boolean {
        val executedTime = LocalDateTime.now()
        val senderId = event.sender.id
        val message = event.message.contentToString()
        val cmd = getCommand(getCommandName(message))

        try {
            if (isCommandPrefix(message)) {
                if (cmd != null) {
                    val splitMessage = message.split(" ")
                    BotVariables.logger.debug("[命令] $senderId 尝试执行命令: ${cmd.props.name}")

                    // 无需认证
                    if (cmd is GuestCommand) {
                        cmd.execute(event, splitMessage.subList(1, splitMessage.size))
                        return true
                    } else if (cmd is UserCommand && user != null) {
                        // 需要认证
                        if (user.hasPermission(cmd.level)) {
                            cmd.execute(event, splitMessage.subList(1, splitMessage.size), user)
                        } else {
                            event.reply("你没有权限!")
                        }
                        val usedTime = Duration.between(executedTime, LocalDateTime.now())
                        BotVariables.logger.debug(
                            "[命令] 命令执行耗时 ${usedTime.toMillis()}ms"
                        )
                        return true
                    }
                }
            }
        } catch (t: Throwable) {
            val msg = t.message
            if (msg != null && msg.contains("time")) {
                event.reply("Bot > 在执行网络操作时连接超时".toMsgChain())
            } else {
                t.printStackTrace()
                BotVariables.logger.warning("[命令] 在试图执行命令时发生了一个错误, 原文: $message, 发送者: $senderId")
                event.reply("Bot > 在试图执行命令时发生了一个错误, 请联系管理员".toMsgChain())
            }
        }
        return false
    }

    // 返回值 表示是否匹配到 NaturalCommand
    private suspend fun executeNaturalCommand(event: MessageEvent, user: BotUser?): Boolean {
        if (user != null) {
            try {
                val intents = naturalCommands
                    .map { it.intent(event, user) }
                    // 置信度大于 60 且用户拥有权限
                    .filter { it.advice != null && it.confidence >= 60 && user.hasPermission(it.advice.userLevel) }

                val intent = intents.minByOrNull { it.confidence }
                return if (intent != null) {
                    BotVariables.logger.info("选择执行 Intent: ${intent.advice} 执行度: ${intent.confidence}")
                    intent.advice!!.entry(event, user)
                    true
                } else false
            } catch (t: Throwable) {
                when (t) {
                    is TimeoutCancellationException -> {
                        event.reply("这么久不理我，我去休息了")
                    }
                    else -> {
                        t.printStackTrace()
                        BotVariables.logger.warning("[命令] 在试图执行命令时发生了一个错误, 原文: ${event.message.contentToString()}, 发送者: ${event.sender.id}")
                        event.reply("Bot > 在试图执行命令时发生了一个错误, 请联系管理员".toMsgChain())
                    }
                }
            }
        }
        return false
    }

    /*@ExperimentalTime
    private suspend fun handleSession(event: MessageEvent, time: LocalDateTime) {
        val sender = event.sender
        if (!isCommandPrefix(event.message.contentToString()) && SessionManager.isValidSessionById(sender.id)) {
            val session: Session? = SessionManager.getSessionByEvent(event)
            if (session != null) {
                val command = session.command
                if (command is SuspendCommand) {
                    var user = BotUser.getUser(sender.id)
                    if (user == null) {
                        user = BotUser.quickRegister(sender.id)
                    }
                    command.handleInput(event, user, session)
                }
            }
        }

        val usedTime = Duration.between(time, LocalDateTime.now())
        BotVariables.logger.debug(
            "[会话] 处理会话耗时 ${usedTime.toKotlinDuration().toLong(DurationUnit.SECONDS)}s${usedTime.toKotlinDuration()
                .toLong(DurationUnit.MILLISECONDS)}ms"
        )
    }*/

    private fun getCommand(cmdPrefix: String): SimpleCommand? {
        for (command in simpleCommands) {
            if (commandEquals(command, cmdPrefix)) {
                return command
            }
        }
        return null
    }

    private fun getCommandName(command: String): String {
        var cmdPrefix = command
        for (string: String in BotVariables.cfg.commandPrefix) {
            cmdPrefix = cmdPrefix.replace(string, "")
        }

        return cmdPrefix.split(" ")[0]
    }

    private fun isCommandPrefix(message: String): Boolean {
        if (message.isNotEmpty()) {
            BotVariables.cfg.commandPrefix.forEach {
                if (message.startsWith(it)) {
                    return true
                }
            }
        }

        return false
    }

    private fun commandEquals(cmd: SimpleCommand, cmdName: String): Boolean {
        val props = cmd.props

        // 匹配 name
        if (props.name.contentEquals(cmdName)) return true

        // 匹配 alias
        props.aliases?.forEach {
            if (it.contentEquals(cmdName)) {
                return true
            }
        }

        // 失败
        return false
    }

    fun countSimpleCommands(): Int = simpleCommands.size

    fun countNaturalCommands(): Int = naturalCommands.size

    fun getSimpleCommands() = simpleCommands

    fun getNaturalCommands() = naturalCommands
}