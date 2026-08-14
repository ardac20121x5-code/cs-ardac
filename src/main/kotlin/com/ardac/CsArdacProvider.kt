package com.ardac

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*
import org.jsoup.nodes.Element

class CsArdacProvider : MainAPI() {
    override var mainUrl = "https://dizipal.com"
    override var name = "Dizipal"
    override val hasMainPage = true
    override val hasQuickSearch = true
    override val supportedTypes = setOf(TvType.TvSeries, TvType.Movie)

    override suspend fun getMainPage(
        page: Int,
        request: MainPageRequest
    ): HomePageResponse {
        val items = mutableListOf<HomePageList>()
        val doc = app.get(mainUrl).document

        // Popüler Diziler
        val popular = doc.select("div.populer-series")
        items.add(
            HomePageList(
                "Popüler Diziler",
                popular.mapNotNull { it.toSearchResult() }
            )
        )

        // Son Eklenen
        val recent = doc.select("div.recent-series")
        items.add(
            HomePageList(
                "Son Eklenen",
                recent.mapNotNull { it.toSearchResult() }
            )
        )

        return HomePageResponse(items)
    }

    private fun Element.toSearchResult(): SearchResponse? {
        val title = this.selectFirst("h3")?.text() ?: return null
        val href = this.selectFirst("a")?.attr("href") ?: return null
        val poster = this.selectFirst("img")?.attr("src")

        return MovieSearchResponse(
            name = title,
            url = href,
            apiName = this@CsArdacProvider.name,
            type = TvType.TvSeries,
            posterUrl = poster
        )
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val url = "$mainUrl/search?q=$query"
        val doc = app.get(url).document

        return doc.select("div.search-result").mapNotNull { it.toSearchResult() }
    }

    override suspend fun load(url: String): LoadResponse {
        val doc = app.get(url).document

        val title = doc.selectFirst("h1.title")?.text() ?: "Bilinmiyor"
        val poster = doc.selectFirst("img.poster")?.attr("src")
        val description = doc.selectFirst("div.description")?.text()
        val rating = doc.selectFirst("div.rating")?.text()?.toRatingInt()

        val episodes = doc.select("div.episode").map { ep ->
            val epName = ep.selectFirst("span.ep-name")?.text() ?: ""
            val epLink = ep.selectFirst("a")?.attr("href") ?: ""
            Episode(
                name = epName,
                url = epLink,
                season = 1,
                episode = episodes.indexOf(ep) + 1
            )
        }

        return TvSeriesLoadResponse(
            name = title,
            url = url,
            apiName = this@CsArdacProvider.name,
            type = TvType.TvSeries,
            episodes = mapOf(1 to episodes),
            posterUrl = poster,
            plot = description,
            rating = rating
        )
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        val doc = app.get(data).document

        // Video kaynakları çek
        doc.select("div.video-source").forEach { source ->
            val quality = source.selectFirst("span.quality")?.text() ?: "720p"
            val videoUrl = source.selectFirst("a")?.attr("href") ?: return@forEach
            val type = source.selectFirst("span.type")?.text() ?: "MP4"

            callback(
                ExtractorLink(
                    source = this.name,
                    name = "$quality - $type",
                    url = videoUrl,
                    referer = mainUrl,
                    quality = getQuality(quality),
                    isM3u8 = type.contains("M3U8", ignoreCase = true)
                )
            )
        }

        // Altyazıları çek
        doc.select("div.subtitle").forEach { sub ->
            val lang = sub.selectFirst("span.lang")?.text() ?: "Türkçe"
            val subUrl = sub.selectFirst("a")?.attr("href") ?: return@forEach

            subtitleCallback(
                SubtitleFile(
                    lang = lang,
                    url = subUrl
                )
            )
        }

        return true
    }

    private fun getQuality(quality: String): Int {
        return when {
            quality.contains("1080") -> Qualities.P1080.value
            quality.contains("720") -> Qualities.P720.value
            quality.contains("480") -> Qualities.P480.value
            quality.contains("360") -> Qualities.P360.value
            else -> Qualities.Unknown.value
        }
    }
}

class HDFilmchennemiProvider : MainAPI() {
    override var mainUrl = "https://hdfilmchennemini.com"
    override var name = "HDFilmchennemini"
    override val hasMainPage = true
    override val supportedTypes = setOf(TvType.Movie, TvType.TvSeries)

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val items = mutableListOf<HomePageList>()
        val doc = app.get(mainUrl).document

        val movies = doc.select("div.movie-item")
        items.add(
            HomePageList(
                "Filmler",
                movies.mapNotNull { it.toSearchResult() }
            )
        )

        return HomePageResponse(items)
    }

    private fun Element.toSearchResult(): SearchResponse? {
        val title = this.selectFirst("h3")?.text() ?: return null
        val href = this.selectFirst("a")?.attr("href") ?: return null
        val poster = this.selectFirst("img")?.attr("src")

        return MovieSearchResponse(
            name = title,
            url = href,
            apiName = this@HDFilmchennemiProvider.name,
            type = TvType.Movie,
            posterUrl = poster
        )
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val url = "$mainUrl/search?q=$query"
        val doc = app.get(url).document
        return doc.select("div.movie-item").mapNotNull { it.toSearchResult() }
    }

    override suspend fun load(url: String): LoadResponse {
        val doc = app.get(url).document

        val title = doc.selectFirst("h1.movie-title")?.text() ?: "Bilinmiyor"
        val poster = doc.selectFirst("img.movie-poster")?.attr("src")
        val description = doc.selectFirst("div.movie-description")?.text()

        return MovieLoadResponse(
            name = title,
            url = url,
            apiName = this@HDFilmchennemiProvider.name,
            type = TvType.Movie,
            posterUrl = poster,
            plot = description
        )
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        val doc = app.get(data).document

        doc.select("div.player-link").forEach { link ->
            val quality = link.selectFirst("span.quality")?.text() ?: "720p"
            val url = link.selectFirst("a")?.attr("href") ?: return@forEach

            callback(
                ExtractorLink(
                    source = this.name,
                    name = quality,
                    url = url,
                    referer = mainUrl,
                    quality = when {
                        quality.contains("1080") -> Qualities.P1080.value
                        quality.contains("720") -> Qualities.P720.value
                        else -> Qualities.P480.value
                    }
                )
            )
        }

        return true
    }
}
