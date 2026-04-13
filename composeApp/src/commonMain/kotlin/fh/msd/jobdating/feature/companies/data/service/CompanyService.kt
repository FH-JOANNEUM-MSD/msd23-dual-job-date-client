package fh.msd.jobdating.feature.companies.data.service

import fh.msd.jobdating.core.domain.dto.PreferenceDto
import fh.msd.jobdating.feature.companies.domain.dto.CompanyDto
import fh.msd.jobdating.feature.companies.domain.model.VoteType

interface CompanyService {
    suspend fun getActiveCompanies(): List<CompanyDto>
    suspend fun getMyPreferences(studentId: Int): List<PreferenceDto>
    suspend fun submitVote(companyId: Int, vote: VoteType): Int
}