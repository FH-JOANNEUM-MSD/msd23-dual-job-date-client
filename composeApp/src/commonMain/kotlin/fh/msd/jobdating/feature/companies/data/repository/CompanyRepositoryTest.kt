package fh.msd.jobdating.feature.companies.data.repository

import fh.msd.jobdating.feature.companies.data.service.CompanyService
import fh.msd.jobdating.feature.companies.domain.model.Company
import fh.msd.jobdating.feature.companies.domain.model.VoteType

class CompanyRepositoryTest(
    private val service: CompanyService
) : CompanyRepository {

    override suspend fun getActiveCompanies(): List<Company> =
        service.getActiveCompanies().map {
            Company(
                id = it.id,
                name = it.name,
                description = it.description,
                industry = it.industry,
                logoUrl = it.logoUrl
            )
        }

    override suspend fun submitVote(companyId: String, vote: VoteType) =
        service.submitVote(companyId, vote)
}