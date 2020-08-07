package org.akteam.miraki.command

import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.MessageChain
import org.akteam.miraki.BotVariables
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
    private var commands: List<UniversalCommand> = mutableListOf()

    /**
     * 注册命令
     *
     * @param command 要注册的命令
     */
    private fun setupCommand(command: UniversalCommand) {
        if (!commands.contains(command)) {
            commands = commands + command
        } else {
            BotVariables.logger.warning("[命令] 正在尝试注册已有命令 ${command.props.name}")
        }
    }

    /**
     * 注册命令
     *
     * @param commands 要注册的命令集合
     */
    fun setupCommand(commands: Array<UniversalCommand>) {
        commands.forEach {
            setupCommand(it)
        }
    }

    /**
     * 执行消息中的命令
     *
     * @param event Mirai 消息命令 (聊天)
     */
    suspend fun execute(event: MessageEvent) {
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
                    } else if (cmd is UserCommand) {
                        // 需要认证
                        val user = BotUsers.get(senderId) ?: return
                        val result: MessageChain =
                            if (user.hasPermission(cmd.level)) {
                                cmd.execute(event, splitMessage.subList(1, splitMessage.size), user)
                            } else {
                                "你没有权限!".toMsgChain()
                            }
                        event.reply(result)
                        val usedTime = Duration.between(executedTime, LocalDateTime.now())
                        BotVariables.logger.debug(
                            "[命令] 命令执行耗时 ${usedTime.toMillis()}ms"
                        )
                    }
                }
            }
        } catch (t: Throwable) {
            val msg = t.message
            if (msg != null && msg.contains("time")) {
                event.reply("Bot > 在执行网络操作时连接超时".toMsgChain())
            } else {
                BotVariables.logger.warning("[命令] 在试图执行命令时发生了一个错误, 原文: $message, 发送者: $senderId", t)
                event.reply("Bot > 在试图执行命令时发生了一个错误, 请联系管理员".toMsgChain())
            }
        }
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

    private fun getCommand(cmdPrefix: String): UniversalCommand? {
        for (command in commands) {
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

    private fun commandEquals(cmd: UniversalCommand, cmdName: String): Boolean {
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

    fun countCommands(): Int = commands.size

    fun getCommands() = commands
}