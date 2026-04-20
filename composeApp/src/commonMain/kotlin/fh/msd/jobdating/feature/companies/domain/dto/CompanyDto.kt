package fh.msd.jobdating.feature.companies.domain.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CompanyDto(
    val id: Int,
    @SerialName("user_id") val userId: String,
    val name: String,
    @SerialName("short_description") val shortDescription: String,
    val description: String,
    val website: String,
    @SerialName("logo_url") val logoUrl: String,
    @SerialName("image_urls") val imageUrls: String,  // semicolon-separated string
    val active: Boolean,
    @SerialName("last_updated") val lastUpdated: String
)