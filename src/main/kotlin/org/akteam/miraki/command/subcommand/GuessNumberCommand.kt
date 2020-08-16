package org.akteam.miraki.command.subcommand

import net.mamoe.mirai.message.MessageEvent
import net.mamoe.mirai.message.data.content
import org.akteam.miraki.command.Intent
import org.akteam.miraki.command.NaturalCommand
import org.akteam.miraki.model.BotUser
import org.akteam.miraki.model.UserLevel
import kotlin.random.Random

class GuessNumberCommand: NaturalCommand {


    override suspend fun entry(event: MessageEvent, user: BotUser) {
        event.reply("猜数字游戏开始了！\n我已经想好了一个100以内的数字，直接发送数字来猜吧\n回复 不玩了 来退出游戏哦")
        val answer = Random.nextInt(100)
        var next = wait(event).message.content
        loop@ while (next != "不玩了") {
            val guess = next.toIntOrNull()
            when {
                guess == null -> {
                    event.reply("你输的好像不是数字？\n再试一遍吧")
                }
                guess > answer -> {
                    event.reply("大了大了")
                }
                guess < answer -> {
                    event.reply("小了")
                }
                guess == answer -> {
                    event.reply("大佬，猜对了 Orz")
                    break@loop
                }
            }
            next = wait(event).message.content
        }
        event.reply("游戏结束了哦")
    }

    override suspend fun intent(event: MessageEvent, user: BotUser): Intent {
        if (event.message.contentEquals("猜数字")) return Intent(
                confidence = 100,
                advice = this
        ) else return Intent(
                confidence = 0,
                advice = this
        )
    }

    override val name: String = "猜数字"
    override val userLevel: UserLevel = UserLevel.NORMAL
}