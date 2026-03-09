
package fh.msd.jobdating.feature.companies.domain.model

data class Company(
    val id: String,
    val name: String,
    val description: String,
    val industry: String,
    val logoUrl: String,
    val vote: VoteType = VoteType.NEUTRAL
)