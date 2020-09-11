package br.com.feitosa.taian.github.client

import br.com.feitosa.taian.github.client.ApplicationResponseAssert.Companion.assertThat
import io.ktor.http.*
import io.ktor.server.testing.*
import io.ktor.util.*
import kotlin.test.*

@KtorExperimentalAPI
class ApplicationTest {
    @Test
    fun testRoot() {
        withTestApplication({ main(testing = true) }) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertThat(response).redirectsTo("/login")
            }
        }
    }

    @Test
    fun testAuth() {
        withTestApplication({ main(testing = true) }) {
            cookiesSession {
                authenticate()
            }
        }
    }

    @Test
    fun testAuthFailure() {
        withTestApplication({ main(testing = true) }) {
            cookiesSession {
                handleRequest(HttpMethod.Post, "/login") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
                    setBody(listOf("username" to "foo", "password" to "iforgot").formUrlEncode())
                }.apply {
                    assertThat(response).redirectsTo("/login?invalid")
                }
            }
        }
    }

    @Test
    fun testNeedToAuthFirst() {
        withTestApplication({ main(testing = true) }) {
            cookiesSession {
                handleRequest(HttpMethod.Get, "/profile").apply {
                    assertThat(response).redirectsTo("/login?no")
                }
            }
        }
    }

    @Test
    fun testCanAccessProfileAfterAuth() {
        withTestApplication({ main(testing = true) }) {
            cookiesSession {
                authenticate()
                handleRequest(HttpMethod.Get, "/profile").apply {
                    assertThat(response).hasStatus(HttpStatusCode.OK)
                }
            }
        }
    }

    @Test
    fun testCanUnAuthenticate() {
        withTestApplication({ main(testing = true) }) {
            cookiesSession {
                authenticate()
                unauthenticate()
                handleRequest(HttpMethod.Get, "/profile").apply {
                    assertThat(response).redirectsTo("/login?no")
                }
            }
        }
    }

    @Test
    fun testRootAuth() {
        withTestApplication({ main(testing = true) }) {
            cookiesSession {
                authenticate()
                handleRequest(HttpMethod.Get, "/").apply {
                    assertThat(response).redirectsTo("/profile")
                }
            }
        }
    }

    @Test
    fun testPageNotFound() {
        withTestApplication({ main(testing = true) }) {
            handleRequest(HttpMethod.Get, "/notFoundErrorTes").apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
                assertEquals("Page not found", response.content)
            }
        }
    }

    private fun CookieTrackerTestApplicationEngine.authenticate() {
        handleRequest(HttpMethod.Get, "/login").apply {
            assertThat(response).hasStatus(HttpStatusCode.OK)
        }
        handleRequest(HttpMethod.Post, "/login") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            setBody(listOf("username" to "foo", "password" to "bar").formUrlEncode())
        }.apply {
            assertThat(response).redirectsTo("/profile")
        }
    }

    private fun CookieTrackerTestApplicationEngine.unauthenticate() {
        handleRequest(HttpMethod.Get, "/logout").apply {
            assertThat(response).redirectsTo("/login")
        }
    }
}

