package br.com.feitosa.taian.github.client

import io.ktor.http.*
import io.ktor.server.testing.*
import io.ktor.util.*

// From https://ktor.io/servers/testing.html#preserving-cookies

fun TestApplicationEngine.cookiesSession(
    initialCookies: List<Cookie> = listOf(),
    callback: CookieTrackerTestApplicationEngine.() -> Unit,
) {
    callback(CookieTrackerTestApplicationEngine(this, initialCookies))
}

class CookieTrackerTestApplicationEngine(
    val engine: TestApplicationEngine,
    var trackedCookies: List<Cookie> = listOf(),
)

@KtorExperimentalAPI
fun CookieTrackerTestApplicationEngine.handleRequest(
    method: HttpMethod,
    uri: String,
    setup: TestApplicationRequest.() -> Unit = {},
): TestApplicationCall {
    return engine.handleRequest(method, uri) {
        val cookieValue =
            trackedCookies.joinToString("; ") { it.name.encodeURLParameter() + "=" + it.value.encodeURLParameter() }
        addHeader(HttpHeaders.Cookie, cookieValue)
        setup()
    }.apply {
        trackedCookies = response.headers.values(HttpHeaders.SetCookie).map(::parseServerSetCookieHeader)
    }
}