package br.com.feitosa.taian.github.client

import io.ktor.http.*
import io.ktor.response.*
import org.assertj.core.api.*

class ApplicationResponseAssert(response: ApplicationResponse) :
    AbstractAssert<ApplicationResponseAssert, ApplicationResponse>(response, ApplicationResponseAssert::class.java) {
    fun hasStatus(statusCode: HttpStatusCode): ApplicationResponseAssert {
        isNotNull

        checkStatusCode(statusCode)

        return this
    }

    fun redirectsTo(url: String, statusCode: HttpStatusCode = HttpStatusCode.Found): ApplicationResponseAssert {
        require(statusCode.value in 300..399) { "Asserted status code must be in 3XX range! (was ${statusCode.value})" }

        isNotNull

        checkStatusCode(statusCode)

        val redirectedTo = actual.headers[HttpHeaders.Location]
        if (redirectedTo != url) {
            failWithMessage("Expected to be redirected to `%s` but was redirected to `%s`", url, redirectedTo)
        }

        return this
    }

    private fun checkStatusCode(statusCode: HttpStatusCode) {
        if (actual.status() != statusCode) {
            failWithMessage(
                "Expected response status to be `%s` but was `%s`",
                statusCode, actual.status()
            )
        }
    }

    companion object {
        fun assertThat(response: ApplicationResponse): ApplicationResponseAssert = ApplicationResponseAssert(response)
    }
}
