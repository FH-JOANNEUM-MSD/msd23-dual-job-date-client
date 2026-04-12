package fh.msd.jobdating.feature.companies.data.service

import fh.msd.jobdating.BuildKonfig
import fh.msd.jobdating.feature.companies.domain.dto.CompanyDto
import fh.msd.jobdating.feature.companies.domain.model.VoteType
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


class CompanyServiceImpl(
    private val supabaseClient: SupabaseClient,
    private val httpClient: HttpClient
) : CompanyService {

    private fun getAccessToken(): String {
        return supabaseClient.auth.currentSessionOrNull()?.accessToken
            ?: error("No active session found")
    }

    override suspend fun getActiveCompanies(): List<CompanyDto> {
        println("[CompanyService] Fetching active companies...")

        val response = httpClient.get("${BuildKonfig.BACKEND_BASE_URL}/api/companies/active") {
            header(HttpHeaders.Authorization, "Bearer ${getAccessToken()}")
            accept(ContentType.Application.Json)
        }

        val rawBody = response.bodyAsText()
        println("[CompanyService] Response status: ${response.status}")
        println("[CompanyService] Raw response: $rawBody")

        val companies: List<CompanyDto> = response.body()
        println("[CompanyService] Parsed ${companies.size} companies")

        return companies
    }

    override suspend fun submitVote(companyId: Int, vote: VoteType) {
        @Serializable data class VoteRequestDto(val vote: String)

        println("[CompanyService] Submitting vote for company $companyId: ${vote.text}")

        val response = httpClient.post("${BuildKonfig.BACKEND_BASE_URL}/api/companies/$companyId/vote") {
            header(HttpHeaders.Authorization, "Bearer ${getAccessToken()}")
            contentType(ContentType.Application.Json)
            setBody(VoteRequestDto(vote.text))
        }

        val rawBody = response.bodyAsText()
        println("[CompanyService] Vote response status: ${response.status}")
        println("[CompanyService] Vote response body: $rawBody")
    }
}