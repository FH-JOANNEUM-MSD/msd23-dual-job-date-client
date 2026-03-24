package fh.msd.jobdating.feature.companies.ui

import fh.msd.jobdating.feature.companies.domain.model.VoteType

sealed class CompanyListEvent {
    data class Vote(val companyId: Int, val vote: VoteType) : CompanyListEvent()
}
