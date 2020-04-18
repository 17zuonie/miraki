package org.akteam.miraki

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

class ChunHuiNotice {
    companion object {
        const val CHSiteUrl = "http://10.181.200.3/home/index/"

        suspend fun fetchNotice(): Models.Notice {
            val doc = withContext(Dispatchers.IO) {
                Jsoup.connect(CHSiteUrl)
                    .timeout(10000)
                    .get()
            }
            val row = doc.selectFirst(".school-notice .row")

            return Models.Notice {
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
}
