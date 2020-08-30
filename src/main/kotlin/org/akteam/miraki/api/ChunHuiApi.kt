package org.akteam.miraki.api

import org.akteam.miraki.BotVariables
import org.akteam.miraki.model.ChunHuiNotice
import org.akteam.miraki.util.BotUtils.get
import org.akteam.miraki.util.BotUtils.readText
import org.jsoup.Jsoup

object ChunHuiApi {
    suspend fun fetchNotice(): ChunHuiNotice {
        val html = BotVariables.http.get(BotVariables.cfg.chunHuiUrl)
        val doc = Jsoup.parse(html.readText())
        val row = doc.selectFirst(".school-notice .row")

        return ChunHuiNotice(
                relativeDate = row.selectFirst(".noticeDateBlock > div:nth-child(1)").text(),
                date = row.selectFirst(".noticeDateBlock > div:nth-child(2)").text(),
                titleWithAuthor = row.selectFirst("div.NoticeTitle > span").text()
        )
//            return mapOf(
//                "relativeDate" to relativeDate,
//                "date" to date,
//                "titleWithAuthor" to titleWithAuthor
//            )
    }
}