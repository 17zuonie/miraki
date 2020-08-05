package org.akteam.miraki.web

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import me.liuwj.ktorm.dsl.eq
import me.liuwj.ktorm.entity.find
import me.liuwj.ktorm.entity.sequenceOf
import org.akteam.miraki.BotConsts
import org.akteam.miraki.model.BotUser
import org.akteam.miraki.model.BotUsers

object WebUtil {
    fun ApplicationCall.jwt(): JWTPrincipal? {
        return authentication.principal<JWTPrincipal>()
    }

    fun ApplicationCall.user(): BotUser? {
        val qq = jwt()!!.payload.getClaim("qq").asLong()
        return if (qq != null) BotConsts.db.sequenceOf(BotUsers).find { it.qq eq qq }
        else null
    }
}