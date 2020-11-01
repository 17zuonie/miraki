package org.akteam.miraki.listener

import net.mamoe.mirai.Bot
import net.mamoe.mirai.event.events.FriendInputStatusChangedEvent
import net.mamoe.mirai.event.subscribeAlways
import net.mamoe.mirai.message.data.Face
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.PlainText
import org.akteam.miraki.manager.SessionManager
import java.time.Instant
import kotlin.random.Random

object InputStatusListener : MListener {
    private val tips = arrayOf(
        PlainText("有话要说？春戊一直都在哦") + Face(74), // 太阳
        PlainText("慢慢说，我等你") + Face(21), // 可爱
        Image("/1306815402-2683676205-A1575070C3972D4B1BBD374F0D324EA3"),
        Image("/1306815402-806776711-AE2983E2091C2FB82FAFAB378A7DEB19")
    )

    private val hit: MutableMap<Long, Instant> = mutableMapOf()

    override fun register(bot: Bot) {
        bot.subscribeAlways<FriendInputStatusChangedEvent> {
            val last = hit.getOrDefault(friend.id, Instant.MIN)
            if (!inputting || SessionManager.match(friend.id) != null || last >= Instant.now()
                    .minusSeconds(300)
            ) return@subscribeAlways

            // 1/3 的概率触发
            if (Random.nextInt(3) == 0) {
                friend.sendMessage(tips.random())
                hit[friend.id] = Instant.now()
            }
        }
    }

    override val name: String = "输入状态"
}