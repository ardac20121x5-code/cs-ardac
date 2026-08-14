package com.ardac

import org.jsoup.nodes.Element
import com.lagradost.cloudstream3.*

/**
 * Dizipal ve HDFilmchennemini için ortak extractor sınıfı
 */
object TurkishExtractors {
    
    fun extractLinksFromPage(
        doc: org.jsoup.nodes.Document,
        baseUrl: String,
        callback: (ExtractorLink) -> Unit
    ) {
        // Video kaynakları çek
        doc.select("div.video-player, iframe").forEach { element ->
            val videoUrl = element.attr("src").ifEmpty { element.attr("data-src") }
            if (videoUrl.isNotEmpty()) {
                val quality = extractQuality(element)
                callback(
                    ExtractorLink(
                        source = "TurkishExtractor",
                        name = quality,
                        url = videoUrl,
                        referer = baseUrl,
                        quality = getQualityValue(quality),
                        isM3u8 = videoUrl.contains(".m3u8")
                    )
                )
            }
        }
    }

    fun extractSubtitles(
        doc: org.jsoup.nodes.Document,
        subtitleCallback: (SubtitleFile) -> Unit
    ) {
        doc.select("track, div.subtitle-option").forEach { element ->
            val lang = element.attr("label").ifEmpty { element.attr("data-lang") }
            val url = element.attr("src").ifEmpty { element.attr("data-src") }
            
            if (url.isNotEmpty() && lang.isNotEmpty()) {
                subtitleCallback(
                    SubtitleFile(lang = lang, url = url)
                )
            }
        }
    }

    private fun extractQuality(element: Element): String {
        return element.attr("data-quality")
            .ifEmpty { element.selectFirst("span.quality")?.text() ?: "720p" }
    }

    private fun getQualityValue(quality: String): Int {
        return when {
            quality.contains("1080") -> Qualities.P1080.value
            quality.contains("720") -> Qualities.P720.value
            quality.contains("480") -> Qualities.P480.value
            quality.contains("360") -> Qualities.P360.value
            else -> Qualities.Unknown.value
        }
    }
}
