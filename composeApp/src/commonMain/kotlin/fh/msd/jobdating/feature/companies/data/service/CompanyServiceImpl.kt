package fh.msd.jobdating.feature.companies.data.service

import fh.msd.jobdating.BuildKonfig
import fh.msd.jobdating.core.domain.dto.PreferenceDto
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
            ?: error("No active session")
    }

    override suspend fun getActiveCompanies(): List<CompanyDto> {
        val companies: List<CompanyDto> = httpClient.get("${BuildKonfig.BACKEND_BASE_URL}/api/companies/active") {
            header(HttpHeaders.Authorization, "Bearer ${getAccessToken()}")
            accept(ContentType.Application.Json)
        }.body()

        return companies
    }

    override suspend fun getMyPreferences(studentId: Int): List<PreferenceDto> {
        return httpClient.get("${BuildKonfig.BACKEND_BASE_URL}/api/students/$studentId/preferences") {
            header(HttpHeaders.Authorization, "Bearer ${getAccessToken()}")
            accept(ContentType.Application.Json)
        }.body()
    }

    override suspend fun submitVote(companyId: Int, vote: VoteType): Int {
        @Serializable
        data class VoteRequest(val vote: String)

        @Serializable
        data class VoteResponse(
            val id: Int,
            @SerialName("student_id") val studentId: Int,
            @SerialName("company_id") val companyId: Int,
            @SerialName("preference_type") val preferenceType: String
        )



        val response = httpClient.post("${BuildKonfig.BACKEND_BASE_URL}/api/companies/$companyId/vote") {
            header(HttpHeaders.Authorization, "Bearer ${getAccessToken()}")
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(VoteRequest(vote.text))
        }

        println("[CompanyService] Response status: ${response.status}")

        if (response.status.value !in 200..299) {
            val errorBody = response.bodyAsText()
            println("[CompanyService] Error response: $errorBody")
            error("Vote failed (${response.status.value}): $errorBody")
        }

        val voteResponse: VoteResponse = response.body()
        println("[CompanyService] Vote successful, student_id: ${voteResponse.studentId}")
        return voteResponse.studentId
    }
}