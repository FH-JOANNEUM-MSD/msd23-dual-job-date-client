package fh.msd.jobdating.feature.companies.domain.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CompanyDto(
    val id: Int,
    @SerialName("user_id") val userId: String,
    val name: String,
    val description: String,
    val website: String,
    @SerialName("logo_url") val logoUrl: String,
    val active: Boolean,
    @SerialName("last_updated") val lastUpdated: String
)
