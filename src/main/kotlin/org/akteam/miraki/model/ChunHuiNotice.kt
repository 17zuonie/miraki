package org.akteam.miraki.model

data class ChunHuiNotice(
        var relativeDate: String,
        var date: String,
        var titleWithAuthor: String
) {
    override fun equals(other: Any?): Boolean {
        return if (other != null && other is ChunHuiNotice) {
            other.titleWithAuthor == this.titleWithAuthor
        } else false
    }
}