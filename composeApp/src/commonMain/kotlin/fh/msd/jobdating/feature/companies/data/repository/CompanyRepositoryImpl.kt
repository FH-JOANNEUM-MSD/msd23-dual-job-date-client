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
        val user = userSession.getUser()

        println("[REPO] studentId from session: $studentId")
        println("[REPO] Full user object: $user")

        val preferencesDto = if (studentId != null) {
            try {
                val prefs = service.getMyPreferences(studentId)
                println("[REPO] Fetched ${prefs.size} preferences")
                prefs
            } catch (e: Exception) {
                println("[REPO] Failed to fetch preferences: ${e.message}")
                emptyList()
            }
        } else {
            println("[REPO] No studentId, skipping preferences fetch")
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
                    shortDescription = dto.shortDescription,
                    description = dto.description,
                    website = dto.website,
                    logoUrl = dto.logoUrl,
                    imageUrls = dto.imageUrls.split(";").filter { it.isNotBlank() },
                    active = dto.active,
                    lastUpdated = dto.lastUpdated,
                    vote = vote
                )
            }
    }

    override suspend fun submitVote(companyId: Int, vote: VoteType) {
        println("[REPO] submitVote: companyId=$companyId, vote=$vote")
        service.submitVote(companyId, vote)
        println("[REPO] Vote submitted")
    }
}