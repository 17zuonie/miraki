package org.akteam.miraki.command

import me.liuwj.ktorm.dsl.eq
import me.liuwj.ktorm.entity.firstOrNull
import me.liuwj.ktorm.entity.sequenceOf
import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.EmptyMessageChain
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.content
import org.akteam.miraki.BotConsts
import org.akteam.miraki.BotMain
import org.akteam.miraki.objects.BotUsers
import org.akteam.miraki.utils.toMirai

/**
 * Mirai 命令处理器
 * 处理群聊/私聊聊天信息中存在的命令
 * @author Nameless
 */
object CommandExecutor {
    var commands: List<UniversalCommand> = mutableListOf()

    /**
     * 注册命令
     *
     * @param command 要注册的命令
     */
    fun setupCommand(command: UniversalCommand) {
        if (!commands.contains(command)) {
            commands = commands + command
        }
    }

    /**
     * 注册命令
     *
     * @param commands 要注册的命令集合
     */
    fun setupCommand(commands: Array<UniversalCommand>) {
        commands.forEach {
            if (!this.commands.contains(it)) {
                this.commands = this.commands.plus(it)
            }
        }
    }

    /**
     * 执行消息中的命令
     *
     * @param event 消息
     */
    suspend fun execute(event: MessageEvent): MessageChain {
        try {
            if (isCommandPrefix(event.message.content) /*&& !SessionManager.isValidSession(event.sender.id)*/) {
                val splitMessage = event.message.contentToString().split(" ")
                val cmd = getCommand(getCommandName(splitMessage))
                if (cmd != null) {
                    BotMain.logger.debug("[命令] " + event.sender.id + " 执行了命令: " + cmd.props.name)
                    return when (cmd) {
                        is UserCommand -> {
                            val user = BotConsts.db.sequenceOf(BotUsers).firstOrNull { it.qq eq event.sender.id }
                            if (user != null && (user.level >= cmd.level || user.hasPermission(cmd.permission))) {
                                cmd.execute(event, splitMessage.subList(1, splitMessage.size), user)
                            } else {
                                BotMain.logger.debug("Rejected.")
                                "你没有权限!".toMirai()
                            }
                        }
                        is GuestCommand -> {
                            cmd.execute(event, splitMessage.subList(1, splitMessage.size))
                        }
                        else -> EmptyMessageChain
                    }
                }
            }
        } catch (e: Exception) {
            BotMain.logger.error("[命令] 出现了不可描述的错误")
            BotMain.logger.error(e)
            return "出现了不可描述的错误".toMirai()
        }
        return EmptyMessageChain
    }

    private fun getCommand(cmdPrefix: String): UniversalCommand? {
        commands.forEach {
            if (commandEquals(it, cmdPrefix)) {
                return it
            }
        }
        return null
    }

    private fun getCommandName(splitMessage: List<String>): String = splitMessage[0].substring(1)

    private fun isCommandPrefix(message: String): Boolean {
        return BotConsts.cfg.commandPrefix.contains(
            message.substring(0, 1)
        ) && message.isNotEmpty()
    }

    private fun commandEquals(cmd: UniversalCommand, cmdName: String): Boolean {
        val props = cmd.props
        when {
            props.name.contentEquals(cmdName) -> {
                return true
            }
            props.aliases != null -> {
                props.aliases?.forEach {
                    if (it.contentEquals(cmdName)) {
                        return true
                    }
                }
            }
            else -> {
                return false
            }
        }
        return false
    }

    /*private fun doFilter(chain: MessageChain): MessageChain {
        if (BotConsts.cfg.filterWords.isNullOrEmpty()) {
            return chain
        }

        val revampChain = LinkedList<SingleMessage>()
        chain.forEach { revampChain.add(it) }

        var count = 0

        for (i in revampChain.indices) {
            if (revampChain[i] is PlainText) {
                var context = revampChain[i].content
                BotConsts.cfg.filterWords.forEach {
                    if (context.contains(it)) {
                        count++
                        context = context.replace(it.toRegex(), " ")
                    }

                    if (count > 3) {
                        return EmptyMessageChain
                    }
                }
                revampChain[i] = PlainText(context)
            }
        }

        return revampChain.asMessageChain()
    }*/
}