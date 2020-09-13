@file:Suppress("EXPERIMENTAL_API_USAGE")

package br.com.feitosa.taian.github.client

import br.com.feitosa.taian.github.client.ApplicationResponseAssert.Companion.assertThat
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

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
    fun testPageNotFound() {
        withTestApplication({ main(testing = true) }) {
            handleRequest(HttpMethod.Get, "/notFoundErrorTes").apply {
                assertEquals(HttpStatusCode.NotFound, response.status())
                assertEquals("Page not found", response.content)
            }
        }
    }
}

