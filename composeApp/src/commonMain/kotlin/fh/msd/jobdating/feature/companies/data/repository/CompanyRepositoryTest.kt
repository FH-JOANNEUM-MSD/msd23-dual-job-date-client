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
                userId = it.userId,
                name = it.name,
                description = it.description,
                website = it.website,
                logoUrl = it.logoUrl,
                active = it.active,
                lastUpdated = it.lastUpdated
            )
        }


    override suspend fun submitVote(companyId: Int, vote: VoteType) =
        service.submitVote(companyId, vote)
}