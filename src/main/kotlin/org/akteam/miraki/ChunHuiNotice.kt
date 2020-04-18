package org.akteam.miraki

import org.jsoup.Jsoup

class ChunHuiNotice {
    companion object {
        const val CHSiteUrl = "http://10.181.200.3/home/index/"
        fun fetchNotice(): Map<String, String> {
            val doc = Jsoup.connect(CHSiteUrl)
                .timeout(10000)
                .get()
            println(doc.html())
            val row = doc.selectFirst(".school-notice .row")

            val relativeDate = row.selectFirst(".noticeDateBlock > div:nth-child(1)").text()
            val date = row.selectFirst(".noticeDateBlock > div:nth-child(2)").text()
            val titleWithAuthor = row.selectFirst("div.NoticeTitle > span").text()
            return mapOf(
                "relativeDate" to relativeDate,
                "date" to date,
                "titleWithAuthor" to titleWithAuthor
            )
        }
    }
}
