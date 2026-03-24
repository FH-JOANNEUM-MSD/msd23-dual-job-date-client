package fh.msd.jobdating.feature.companies.data.repository

import fh.msd.jobdating.feature.companies.data.service.CompanyService
import fh.msd.jobdating.feature.companies.domain.model.Company
import fh.msd.jobdating.feature.companies.domain.model.VoteType

class CompanyRepositoryImpl(
    private val service: CompanyService
) : CompanyRepository {

    private var cachedCompanies: List<Company>? = null

    override suspend fun getActiveCompanies(): List<Company> {
        cachedCompanies?.let { return it }

        val companies = service.getActiveCompanies().map {
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
        cachedCompanies = companies
        return companies
    }

    override suspend fun submitVote(companyId: Int, vote: VoteType) =
        service.submitVote(companyId, vote)
}