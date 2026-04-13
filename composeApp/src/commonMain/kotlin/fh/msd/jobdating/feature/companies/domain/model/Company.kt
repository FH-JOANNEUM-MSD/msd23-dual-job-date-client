package fh.msd.jobdating.feature.companies.domain.model

data class Company(
    val id: Int,
    val userId: String,
    val name: String,
    val description: String,
    val website: String,
    val logoUrl: String,
    val active: Boolean,
    val lastUpdated: String,
    val vote: VoteType? = null  // null = not voted yet
)