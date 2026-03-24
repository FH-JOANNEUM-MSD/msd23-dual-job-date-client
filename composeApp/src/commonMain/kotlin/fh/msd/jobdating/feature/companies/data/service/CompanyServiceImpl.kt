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
        return httpClient.get("${BuildKonfig.BACKEND_BASE_URL}/api/companies/active") {
            header(HttpHeaders.Authorization, "Bearer ${getAccessToken()}")
            accept(ContentType.Application.Json)
        }.body()
    }

    override suspend fun submitVote(companyId: Int, vote: VoteType) {
        val userId = supabaseClient.auth.currentSessionOrNull()?.user?.id
            ?: error("No active session found")

        @Serializable
        data class VoteRequestDto(
            @SerialName("company_id")
            val companyId: Int,
            val liked: Boolean
        )

        httpClient.post("${BuildKonfig.BACKEND_BASE_URL}/api/students/$userId/preferences") {
            header(HttpHeaders.Authorization, "Bearer ${getAccessToken()}")
            contentType(ContentType.Application.Json)
            setBody(VoteRequestDto(
                companyId = companyId,
                liked = vote == VoteType.LIKE
            ))
        }
    }
}