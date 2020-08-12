package org.akteam.miraki.command

import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import net.mamoe.mirai.message.MessageEvent
import org.akteam.miraki.BotVariables
import org.akteam.miraki.manager.SessionManager
import org.akteam.miraki.model.BotUser
import org.akteam.miraki.model.UserLevel
import java.time.Instant
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.time.Duration

data class Intent(

    // 0..100
    val confidence: Int,

    // 推荐执行命令，可以不是自己
    val advice: NaturalCommand
)

interface NaturalCommand {
    suspend fun entry(event: MessageEvent, user: BotUser)

    suspend fun intent(event: MessageEvent, user: BotUser): Intent

    suspend fun wait(event: MessageEvent, expireIn: Long = BotVariables.cfg.sessionExpireTime): MessageEvent {
        return withTimeout(expireIn * 1000L) {
            suspendCancellableCoroutine<MessageEvent> {
                val expireTime = Instant.now().plusSeconds(expireIn)
                SessionManager.set(event, expireTime) { nextEvent ->
                    it.resume(nextEvent)
                }
                it.invokeOnCancellation {
                    SessionManager.remove(event)
                }
            }
        }
    }

    val name: String
    val userLevel: UserLevel
}