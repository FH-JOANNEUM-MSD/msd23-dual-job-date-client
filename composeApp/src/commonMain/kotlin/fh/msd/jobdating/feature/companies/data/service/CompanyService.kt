
// --- data/service/CompanyService.kt ---
package fh.msd.jobdating.feature.companies.data.service

import fh.msd.jobdating.feature.companies.domain.dto.CompanyDto
import fh.msd.jobdating.feature.companies.domain.model.VoteType

interface CompanyService {
    suspend fun getActiveCompanies(): List<CompanyDto>
    suspend fun submitVote(companyId: String, vote: VoteType)
}