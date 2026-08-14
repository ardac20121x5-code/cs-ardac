package com.ardac

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*
import org.jsoup.nodes.Element

class CsArdacProvider : MainAPI() {
    override var mainUrl = "https://dizipal.tv" // URL değişebilir, dinamik kontrol gerekli
    override var name = "Dizipal"
    override val hasMainPage = true
    override val hasQuickSearch = true
    override val supportedTypes = setOf(TvType.TvSeries, TvType.Movie)

    // Dinamik URL kontrolü
    private suspend fun getWorkingUrl(): String {
        val possibleUrls = listOf(
            "https://dizipal.tv",
            "https://dizipal.com",
            "https://dizipal.site",
            "https://dizipal.xyz"
        )
        
        for (url in possibleUrls) {
            try {
                app.get(url, timeout = 10)
                return url
            } catch (e: Exception) {
                continue
            }
        }
        return mainUrl
    }

    override suspend fun getMainPage(
        page: Int,
        request: MainPageRequest
    ): HomePageResponse {
        val items = mutableListOf<HomePageList>()
        val workingUrl = getWorkingUrl()
        val doc = app.get(workingUrl).document

        // Popüler Diziler
        val popular = doc.select("div.populer-series, div.popular-content")
        if (popular.isNotEmpty()) {
            items.add(
                HomePageList(
                    "Popüler Diziler",
                    popular.mapNotNull { it.toSearchResult() }
                )
            )
        }

        // Son Eklenen
        val recent = doc.select("div.recent-series, div.new-content")
        if (recent.isNotEmpty()) {
            items.add(
                HomePageList(
                    "Son Eklenen",
                    recent.mapNotNull { it.toSearchResult() }
                )
            )
        }

        return HomePageResponse(items)
    }

    private fun Element.toSearchResult(): SearchResponse? {
        val title = this.selectFirst("h3, .title")?.text() ?: return null
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
        val workingUrl = getWorkingUrl()
        val url = "$workingUrl/search?q=$query"
        val doc = app.get(url).document

        return doc.select("div.search-result, div.content-item").mapNotNull { it.toSearchResult() }
    }

    override suspend fun load(url: String): LoadResponse {
        val doc = app.get(url).document

        val title = doc.selectFirst("h1.title, h1")?.text() ?: "Bilinmiyor"
        val poster = doc.selectFirst("img.poster, img.cover")?.attr("src")
        val description = doc.selectFirst("div.description, div.synopsis")?.text()
        val rating = doc.selectFirst("div.rating, span.imdb")?.text()?.toRatingInt()

        val episodes = doc.select("div.episode, .ep-item").mapIndexed { index, ep ->
            val epName = ep.selectFirst("span.ep-name, .ep-title")?.text() ?: ""
            val epLink = ep.selectFirst("a")?.attr("href") ?: ""
            Episode(
                name = epName,
                url = epLink,
                season = 1,
                episode = index + 1
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
        doc.select("div.video-source, iframe, .player-frame").forEach { source ->
            val quality = source.selectFirst("span.quality, .quality-badge")?.text() ?: "720p"
            val videoUrl = source.selectFirst("a")?.attr("href") 
                ?: source.attr("src")
                ?: source.attr("data-src")
            
            if (videoUrl.isNotEmpty()) {
                callback(
                    ExtractorLink(
                        source = this.name,
                        name = "$quality",
                        url = videoUrl,
                        referer = data,
                        quality = getQuality(quality),
                        isM3u8 = videoUrl.contains("m3u8", ignoreCase = true)
                    )
                )
            }
        }

        // Altyazıları çek
        doc.select("div.subtitle, track, .sub-option").forEach { sub ->
            val lang = sub.selectFirst("span.lang, .lang-name")?.text() ?: "Türkçe"
            val subUrl = sub.selectFirst("a")?.attr("href") 
                ?: sub.attr("src")
                ?: sub.attr("data-src")

            if (subUrl.isNotEmpty()) {
                subtitleCallback(
                    SubtitleFile(
                        lang = lang,
                        url = subUrl
                    )
                )
            }
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
    override var mainUrl = "https://hdfilmchennemimi.nl" // Güncellenmiş URL
    override var name = "HDFilmchennemini"
    override val hasMainPage = true
    override val supportedTypes = setOf(TvType.Movie, TvType.TvSeries)

    // Dinamik URL kontrolü
    private suspend fun getWorkingUrl(): String {
        val possibleUrls = listOf(
            "https://hdfilmchennemimi.nl",
            "https://hdfilmchennemini.com",
            "https://hdfilmchennemini.site",
            "https://hdfilmchennemini.tv"
        )
        
        for (url in possibleUrls) {
            try {
                app.get(url, timeout = 10)
                return url
            } catch (e: Exception) {
                continue
            }
        }
        return mainUrl
    }

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val items = mutableListOf<HomePageList>()
        val workingUrl = getWorkingUrl()
        val doc = app.get(workingUrl).document

        val movies = doc.select("div.movie-item, div.film-box, .content-box")
        if (movies.isNotEmpty()) {
            items.add(
                HomePageList(
                    "Filmler",
                    movies.mapNotNull { it.toSearchResult() }
                )
            )
        }

        return HomePageResponse(items)
    }

    private fun Element.toSearchResult(): SearchResponse? {
        val title = this.selectFirst("h3, .title, .film-title")?.text() ?: return null
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
        val workingUrl = getWorkingUrl()
        val url = "$workingUrl/search?q=$query"
        val doc = app.get(url).document
        return doc.select("div.movie-item, div.film-box").mapNotNull { it.toSearchResult() }
    }

    override suspend fun load(url: String): LoadResponse {
        val doc = app.get(url).document

        val title = doc.selectFirst("h1.movie-title, h1")?.text() ?: "Bilinmiyor"
        val poster = doc.selectFirst("img.movie-poster, img.cover")?.attr("src")
        val description = doc.selectFirst("div.movie-description, .synopsis")?.text()

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

        doc.select("div.player-link, iframe, .video-source").forEach { link ->
            val quality = link.selectFirst("span.quality, .quality-badge")?.text() ?: "720p"
            val url = link.selectFirst("a")?.attr("href") 
                ?: link.attr("src")
                ?: link.attr("data-src")

            if (url.isNotEmpty()) {
                callback(
                    ExtractorLink(
                        source = this.name,
                        name = quality,
                        url = url,
                        referer = data,
                        quality = when {
                            quality.contains("1080") -> Qualities.P1080.value
                            quality.contains("720") -> Qualities.P720.value
                            else -> Qualities.P480.value
                        },
                        isM3u8 = url.contains("m3u8", ignoreCase = true)
                    )
                )
            }
        }

        // Altyazıları çek
        doc.select("track, div.subtitle, .sub-item").forEach { sub ->
            val lang = sub.selectFirst("span.lang")?.text() ?: "Türkçe"
            val subUrl = sub.attr("src").ifEmpty { sub.attr("data-src") }
            
            if (subUrl.isNotEmpty()) {
                subtitleCallback(
                    SubtitleFile(lang = lang, url = subUrl)
                )
            }
        }

        return true
    }
}
