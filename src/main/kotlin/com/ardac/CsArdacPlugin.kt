package com.ardac

import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.providers.MainAPI

@CloudstreamPlugin
class CsArdacPlugin : MainAPI {
    override val mainPage = listOf(
        "Popüler Diziler" to "popular",
        "Son Eklenen" to "recent",
        "Filmler" to "movies"
    )

    override fun getMainPage(): List<Pair<String, String>> = mainPage
}
