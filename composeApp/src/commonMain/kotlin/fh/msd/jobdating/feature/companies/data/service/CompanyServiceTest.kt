package fh.msd.jobdating.feature.companies.data.service

import fh.msd.jobdating.feature.companies.domain.dto.CompanyDto
import fh.msd.jobdating.feature.companies.domain.model.VoteType

class CompanyServiceTest : CompanyService {

    override suspend fun getActiveCompanies(): List<CompanyDto> = listOf(
        CompanyDto(
            id = 1,
            userId = "fe2ad44a-ed3e-4f92-9b8b-1a76c5dd2db1",
            name = "TechCorp GmbH",
            description = "We build cool software solutions for enterprise clients.",
            website = "https://techcorp.example.com",
            logoUrl = "",
            active = true,
            lastUpdated = "2026-03-23T22:30:04"
        ),
        CompanyDto(
            id = 2,
            userId = "83e1e957-bbe4-4e4b-b9ab-c54c0d68f446",
            name = "GreenEnergy AG",
            description = "Sustainable energy solutions for a better tomorrow.",
            website = "https://greenenergy.example.com",
            logoUrl = "",
            active = true,
            lastUpdated = "2026-03-23T22:30:04"
        ),
        CompanyDto(
            id = 3,
            userId = "a657b23e-a2c4-4293-8355-122c0e35c2c2",
            name = "FinanceHub",
            description = "Modern banking and fintech solutions.",
            website = "https://financehub.example.com",
            logoUrl = "",
            active = true,
            lastUpdated = "2026-03-23T22:30:04"
        )
    )

    override suspend fun submitVote(companyId: Int, vote: VoteType) {
        // stub - will call ktor later
    }
}