package fh.msd.jobdating.feature.companies.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class CompanyDto(
    val id: String,
    val name: String,
    val description: String,
    val industry: String,
    val logoUrl: String
)