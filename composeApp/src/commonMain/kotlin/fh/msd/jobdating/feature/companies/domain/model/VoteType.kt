package fh.msd.jobdating.feature.companies.domain.model

enum class VoteType(val text: String) { // text used for API
    LIKE("like"), DISLIKE("dislike"), NEUTRAL("neutral")
}

