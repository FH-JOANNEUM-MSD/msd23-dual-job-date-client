package fh.msd.jobdating.feature.companies.data.repository

import fh.msd.jobdating.feature.companies.domain.model.Company
import fh.msd.jobdating.feature.companies.domain.model.VoteType

interface CompanyRepository {
    suspend fun getActiveCompanies(): List<Company>
    suspend fun submitVote(companyId: Int, vote: VoteType)
}
