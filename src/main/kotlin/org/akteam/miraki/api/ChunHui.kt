package org.akteam.miraki.api

import org.akteam.miraki.BotConsts
import org.akteam.miraki.objects.ChunHuiNotice
import org.akteam.miraki.utils.BotUtil.get
import org.akteam.miraki.utils.BotUtil.readText
import org.jsoup.Jsoup

object ChunHui {
    suspend fun fetchNotice(): ChunHuiNotice {
        val html = BotConsts.http.get(BotConsts.cfg.chunHuiUrl)
        val doc = Jsoup.parse(html.readText())
        val row = doc.selectFirst(".school-notice .row")

        return ChunHuiNotice {
            relativeDate = row.selectFirst(".noticeDateBlock > div:nth-child(1)").text()
            date = row.selectFirst(".noticeDateBlock > div:nth-child(2)").text()
            titleWithAuthor = row.selectFirst("div.NoticeTitle > span").text()
        }
//            return mapOf(
//                "relativeDate" to relativeDate,
//                "date" to date,
//                "titleWithAuthor" to titleWithAuthor
//            )
    }
}