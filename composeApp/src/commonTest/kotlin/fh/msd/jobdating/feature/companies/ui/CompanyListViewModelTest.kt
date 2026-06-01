package fh.msd.jobdating.feature.companies.ui

import fh.msd.jobdating.feature.companies.data.repository.CompanyRepository
import fh.msd.jobdating.feature.companies.domain.model.Company
import fh.msd.jobdating.feature.companies.domain.model.VoteType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class CompanyListViewModelTest {

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun init_loadsCompaniesFromRepository() = runTest {
        val repo = FakeCompanyRepository(
            companies = listOf(
                company(1, "Acme"),
                company(2, "Globex")
            )
        )

        val vm = CompanyListViewModel(repo)

        assertEquals(2, vm.state.value.companies.size)
        assertEquals("Acme", vm.state.value.companies[0].name)
        assertFalse(vm.state.value.isLoading)
        assertFalse(vm.state.value.hasError)
    }

    @Test
    fun init_repositoryFails_setsHasError() = runTest {
        val vm = CompanyListViewModel(FakeCompanyRepository(shouldFailLoad = true))

        assertTrue(vm.state.value.hasError)
        assertFalse(vm.state.value.isLoading)
    }

    @Test
    fun vote_callsSubmitVoteOnRepository() = runTest {
        val repo = FakeCompanyRepository(companies = listOf(company(1, "Acme")))
        val vm = CompanyListViewModel(repo)

        vm.onEvent(CompanyListEvent.Vote(companyId = 1, vote = VoteType.LIKE))

        assertEquals(listOf(1 to VoteType.LIKE), repo.submittedVotes)
    }

    private fun company(id: Int, name: String) = Company(
        id = id,
        userId = "user-$id",
        name = name,
        shortDescription = "",
        description = "",
        website = "",
        logoUrl = "",
        imageUrls = emptyList(),
        active = true,
        lastUpdated = ""
    )

    private class FakeCompanyRepository(
        private val companies: List<Company> = emptyList(),
        private val shouldFailLoad: Boolean = false
    ) : CompanyRepository {
        val submittedVotes = mutableListOf<Pair<Int, VoteType>>()

        override suspend fun getActiveCompanies(): List<Company> {
            if (shouldFailLoad) throw RuntimeException("load failed")
            return companies
        }

        override suspend fun submitVote(companyId: Int, vote: VoteType) {
            submittedVotes += companyId to vote
        }
    }
}
