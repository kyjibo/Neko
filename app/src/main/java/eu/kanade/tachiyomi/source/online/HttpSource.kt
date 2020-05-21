package eu.kanade.tachiyomi.source.online

import eu.kanade.tachiyomi.network.GET
import eu.kanade.tachiyomi.network.NetworkHelper
import eu.kanade.tachiyomi.network.asObservableSuccess
import eu.kanade.tachiyomi.network.newCallWithProgress
import eu.kanade.tachiyomi.source.Source
import eu.kanade.tachiyomi.source.model.FilterList
import eu.kanade.tachiyomi.source.model.Page
import eu.kanade.tachiyomi.source.model.SManga
import eu.kanade.tachiyomi.source.online.handlers.MangaPlusHandler
import eu.kanade.tachiyomi.source.online.utils.MdUtil
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import rx.Observable
import uy.kohesive.injekt.injectLazy
import java.security.MessageDigest

/**
 * A simple implementation for sources from a website.
 */
abstract class HttpSource : Source {

    /**
     * Network service.
     */
    protected val network: NetworkHelper by injectLazy()

//    /**
//     * Preferences that a source may need.
//     */
//    val preferences: SharedPreferences by lazy {
//        Injekt.get<Application>().getSharedPreferences("source_$id", Context.MODE_PRIVATE)
//    }

    /**
     * Base url of the website without the trailing slash, like: http://mysite.com
     */
    val baseUrl = MdUtil.baseUrl

    override val name = "MangaDex"

    /**
     * Version id used to generate the source id. If the site completely changes and urls are
     * incompatible, you may increase this value and it'll be considered as a new source.
     */
    open val versionId = 1

    /**
     * Id of the source. By default it uses a generated id using the first 16 characters (64 bits)
     * of the MD5 of the string: sourcename/language/versionId
     * Note the generated id sets the sign bit to 0.
     */
    override val id by lazy {
        val key = "${name.toLowerCase()}/en/$versionId"
        val bytes = MessageDigest.getInstance("MD5").digest(key.toByteArray())
        (0..7).map { bytes[it].toLong() and 0xff shl 8 * (7 - it) }
            .reduce(Long::or) and Long.MAX_VALUE
    }

    /**
     * Headers used for requests.
     */
    val headers: Headers by lazy { headersBuilder().build() }

    /**
     * Default network client for doing requests.
     */
    val client: OkHttpClient = network.client

    val nonRateLimitedClient = network.nonRateLimitedClient

    /**
     * Headers builder for request.
     */
    protected fun headersBuilder() = Headers.Builder().apply {
        add("User-Agent", "Tachiyomi " + System.getProperty("http.agent"))
        add("X-Requested-With", "XMLHttpRequest")
    }

    /**
     * Visible name of the source.
     */
    override fun toString() = name

    // used to get the manga url instead of the api manga url
    open fun mangaDetailsRequest(manga: SManga): Request {
        return GET(baseUrl + manga.url, headers)
    }

    /**
     * Returns an observable with the page containing the source url of the image. If there's any
     * error, it will return null instead of throwing an exception.
     *
     * @param page the page whose source image has to be fetched.
     */
    open fun fetchImageUrl(page: Page): Observable<String> {
        if (page.imageUrl!!.contains("mangaplus", true)) {
            return MangaPlusHandler(nonRateLimitedClient).client.newCall(GET(page.imageUrl!!, headers))
                .asObservableSuccess()
                .map { "" }
        }
        return nonRateLimitedClient.newCall(GET(page.imageUrl!!))
            .asObservableSuccess()
            .map { "" }
    }

    /**
     * Returns an observable with the response of the source image.
     *
     * @param page the page whose source image has to be downloaded.
     */
    fun fetchImage(page: Page): Observable<Response> {
        if (page.imageUrl!!.contains("mangaplus", true)) {
            return MangaPlusHandler(nonRateLimitedClient).client.newCall(GET(page.imageUrl!!, headers))
                .asObservableSuccess()
        }
        return nonRateLimitedClient.newCallWithProgress(GET(page.imageUrl!!), page)
            .asObservableSuccess()
    }

    /**
     * Returns the list of filters for the source.
     */
    override fun getFilterList() = FilterList()
}
