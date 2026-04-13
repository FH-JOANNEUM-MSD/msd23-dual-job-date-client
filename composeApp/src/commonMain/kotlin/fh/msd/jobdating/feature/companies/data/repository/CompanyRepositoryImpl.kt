package fh.msd.jobdating.feature.companies.data.repository

import fh.msd.jobdating.core.session.UserSession
import fh.msd.jobdating.feature.companies.data.service.CompanyService
import fh.msd.jobdating.feature.companies.domain.model.Company
import fh.msd.jobdating.feature.companies.domain.model.VoteType

class CompanyRepositoryImpl(
    private val service: CompanyService,
    private val userSession: UserSession
) : CompanyRepository {

    override suspend fun getActiveCompanies(): List<Company> {
        val companiesDto = service.getActiveCompanies()
        val studentId = userSession.getStudentId()

        val preferencesDto = if (studentId != null) {
            try {
                service.getMyPreferences(studentId)
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }

        return companiesDto
            .filter { dto ->
                dto.description.isNotBlank() &&
                        dto.website.isNotBlank() &&
                        dto.logoUrl.isNotBlank()
            }
            .map { dto ->
                val pref = preferencesDto.find { it.companyId == dto.id }
                val vote = when (pref?.preferenceType) {
                    "like" -> VoteType.LIKE
                    "dislike" -> VoteType.DISLIKE
                    "neutral" -> VoteType.NEUTRAL
                    else -> null
                }

                Company(
                    id = dto.id,
                    userId = dto.userId,
                    name = dto.name,
                    description = dto.description,
                    website = dto.website,
                    logoUrl = dto.logoUrl,
                    active = dto.active,
                    lastUpdated = dto.lastUpdated,
                    vote = vote
                )
            }
    }

    override suspend fun submitVote(companyId: Int, vote: VoteType) {
        service.submitVote(companyId, vote)
    }
}