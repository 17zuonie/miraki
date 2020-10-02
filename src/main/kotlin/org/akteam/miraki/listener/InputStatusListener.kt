package org.akteam.miraki.listener

import net.mamoe.mirai.Bot
import net.mamoe.mirai.event.events.FriendInputStatusChangedEvent
import net.mamoe.mirai.event.subscribeAlways
import net.mamoe.mirai.message.data.Face
import net.mamoe.mirai.message.data.PlainText
import org.akteam.miraki.manager.SessionManager
import java.time.Instant
import kotlin.random.Random

object InputStatusListener : MListener {
    private val tips = arrayOf(
            PlainText("有话要说？春戊一直都在哦") + Face(74), // 太阳
            PlainText("慢慢说，我等你") + Face(21) // 可爱
    )

    private val hit: MutableMap<Long, Instant> = mutableMapOf()

    override fun register(bot: Bot) {
        bot.subscribeAlways<FriendInputStatusChangedEvent> {
            val last = hit.getOrDefault(friend.id, Instant.MIN)
            if (!inputting || SessionManager.match(friend.id) != null || last >= Instant.now().minusSeconds(300)) return@subscribeAlways

            // 4/5 的概率触发
            if (Random.nextInt(5) < 4) {
                friend.sendMessage(tips.random())
                hit[friend.id] = Instant.now()
            }
        }
    }

    override val name: String = "输入状态"
}