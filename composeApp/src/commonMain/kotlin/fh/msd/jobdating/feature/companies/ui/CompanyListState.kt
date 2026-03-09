package fh.msd.jobdating.feature.companies.ui

import fh.msd.jobdating.feature.companies.domain.model.Company

data class CompanyListState(
    val companies: List<Company> = emptyList(),
    val currentIndex: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isDone: Boolean = false
)