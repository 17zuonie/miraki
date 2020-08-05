package org.akteam.miraki.web

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import me.liuwj.ktorm.dsl.eq
import me.liuwj.ktorm.entity.add
import me.liuwj.ktorm.entity.find
import me.liuwj.ktorm.entity.map
import me.liuwj.ktorm.entity.sequenceOf
import org.akteam.miraki.BotConsts
import org.akteam.miraki.BotMain
import org.akteam.miraki.model.RecommendMusics
import org.akteam.miraki.model.UserLevel
import org.akteam.miraki.model.UserLikedMusic
import org.akteam.miraki.model.UserLikedMusics
import org.akteam.miraki.web.WebUtil.jwt
import org.akteam.miraki.web.WebUtil.user
import java.time.Instant

fun Application.main() {
    install(CORS) {
        anyHost()
        header("Authorization")
    }

    install(Authentication) {
        jwt {
            verifier(JwtConfig.verifier)
            realm = JwtConfig.realm
            validate {
                JWTPrincipal(it.payload)
            }
        }
    }

    install(ContentNegotiation) {
        json()
    }

    routing {
        authenticate(optional = true) {
            get("/songs") {
                val seq = BotConsts.db.sequenceOf(RecommendMusics)

                val list = seq
                    .map {
                        val friend = BotMain.bot.friends[it.qq]
                        Response.Song(it, friend)
                    }
                call.respond(list)
            }
        }

        get("/") {
            call.respondText("Hello, world!")
        }

        authenticate {
            get("/auth/info") {
                val rep = Response.AuthInfo(
                    valid = true,
                    qq = call.jwt()!!.payload.getClaim("qq").asLong(),
                    expireAt = call.jwt()!!.payload.expiresAt.time
                )
                call.respond(rep)
            }

            post("/song/{id}/like") {
                val seq = BotConsts.db.sequenceOf(RecommendMusics)
                val song = seq.find { it.n eq call.parameters["id"]!!.toInt() }!!
                val user = call.user()!!

                // 管理员特权？
                if (user.level >= UserLevel.ADMIN || UserLikedMusics.liked(user.qq, song) == 0) {
                    // 使用事务，保证一致性
                    BotConsts.db.useTransaction {
                        song.like++
                        val record = UserLikedMusic {
                            qq = user.qq
                            musicId = song.n
                            playlistId = song.playlistId
                            subTime = Instant.now()
                        }

                        BotConsts.db.sequenceOf(UserLikedMusics).add(record)
                        song.flushChanges()
                    }
                    call.respond(mapOf("OK" to true))
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }

            get("/user") {
                call.respondText { call.jwt().toString() }
            }
        }

    }
}

object WebMain {
    lateinit var server: ApplicationEngine
    fun run(args: Array<String>, wait: Boolean = true) {
        val applicationEnvironment = commandLineEnvironment(args)
        server = embeddedServer(Netty, applicationEnvironment)
        server.start(wait)
    }
}