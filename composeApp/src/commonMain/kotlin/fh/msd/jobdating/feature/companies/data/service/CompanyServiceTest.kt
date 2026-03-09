package fh.msd.jobdating.feature.companies.data.service

import fh.msd.jobdating.feature.companies.domain.dto.CompanyDto
import fh.msd.jobdating.feature.companies.domain.model.VoteType

class CompanyServiceTest : CompanyService {

    override suspend fun getActiveCompanies(): List<CompanyDto> = listOf(
        CompanyDto(
            id = "1",
            name = "TechCorp GmbH",
            description = "We build cool software solutions for enterprise clients.",
            industry = "Software Development",
            logoUrl = ""
        ),
        CompanyDto(
            id = "2",
            name = "GreenEnergy AG",
            description = "Sustainable energy solutions for a better tomorrow.",
            industry = "Energy",
            logoUrl = ""
        ),
        CompanyDto(
            id = "3",
            name = "FinanceHub",
            description = "Modern banking and fintech solutions.",
            industry = "Finance",
            logoUrl = ""
        )
    )

    override suspend fun submitVote(companyId: String, vote: VoteType) {
        // stub - will call ktor later
    }
}