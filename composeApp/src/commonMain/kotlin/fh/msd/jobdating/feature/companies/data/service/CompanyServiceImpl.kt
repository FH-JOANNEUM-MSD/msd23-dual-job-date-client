package fh.msd.jobdating.feature.companies.data.service

import fh.msd.jobdating.BuildKonfig
import fh.msd.jobdating.feature.companies.domain.dto.CompanyDto
import fh.msd.jobdating.feature.companies.domain.model.VoteType
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

class CompanyServiceImpl(
    private val httpClient: HttpClient
) : CompanyService {

    override suspend fun getActiveCompanies(): List<CompanyDto> {
        val test = httpClient.get("${BuildKonfig.BACKEND_BASE_URL}/api/companies/active") {
            accept(ContentType.Application.Json)
        }

        val body = test.bodyAsText()
        println(body)
        return emptyList()
    }

    override suspend fun submitVote(companyId: String, vote: VoteType) {
        // stub - will call ktor later
    }
}