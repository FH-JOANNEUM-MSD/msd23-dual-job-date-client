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
class CompanySwipeViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── Initial load (preserved behavior) ──

    @Test
    fun init_filtersOutAlreadyVotedCompanies() = runTest {
        val repo = FakeCompanyRepository(
            companies = listOf(
                company(1, "Acme"),
                company(2, "Globex", vote = VoteType.LIKE),
                company(3, "Initech")
            )
        )

        val vm = CompanySwipeViewModel(repo)

        assertEquals(listOf(1, 3), vm.state.value.companies.map { it.id })
        assertEquals(0, vm.state.value.currentIndex)
        assertFalse(vm.state.value.isDone)
    }

    @Test
    fun init_allCompaniesAlreadyVoted_setsIsDone() = runTest {
        val repo = FakeCompanyRepository(
            companies = listOf(company(1, "Acme", vote = VoteType.LIKE))
        )

        val vm = CompanySwipeViewModel(repo)

        assertTrue(vm.state.value.isDone)
    }

    @Test
    fun init_repositoryFails_setsHasError() = runTest {
        val vm = CompanySwipeViewModel(FakeCompanyRepository(shouldFailLoad = true))

        assertTrue(vm.state.value.hasError)
        assertFalse(vm.state.value.isLoading)
    }

    // ── New behavior: advance in memory instead of refetching ──

    @Test
    fun vote_advancesToNextCard() = runTest {
        val repo = FakeCompanyRepository(
            companies = listOf(company(1, "Acme"), company(2, "Globex"))
        )
        val vm = CompanySwipeViewModel(repo)

        vm.onEvent(CompanyListEvent.Vote(companyId = 1, vote = VoteType.LIKE))

        assertEquals(1, vm.state.value.currentIndex)
        assertFalse(vm.state.value.isDone)
    }

    @Test
    fun vote_onLastCard_setsIsDoneAndKeepsIndexInBounds() = runTest {
        val repo = FakeCompanyRepository(
            companies = listOf(company(1, "Acme"), company(2, "Globex"))
        )
        val vm = CompanySwipeViewModel(repo)

        vm.onEvent(CompanyListEvent.Vote(companyId = 1, vote = VoteType.LIKE))
        vm.onEvent(CompanyListEvent.Vote(companyId = 2, vote = VoteType.DISLIKE))

        assertTrue(vm.state.value.isDone)
        // SwipeContent indexes companies[currentIndex] directly, so the index
        // must stay within bounds even after the final swipe.
        assertTrue(vm.state.value.currentIndex < vm.state.value.companies.size)
    }

    @Test
    fun vote_submitsEachVoteOnceWithCorrectArgs() = runTest {
        val repo = FakeCompanyRepository(
            companies = listOf(company(1, "Acme"), company(2, "Globex"))
        )
        val vm = CompanySwipeViewModel(repo)

        vm.onEvent(CompanyListEvent.Vote(companyId = 1, vote = VoteType.LIKE))
        vm.onEvent(CompanyListEvent.Vote(companyId = 2, vote = VoteType.NEUTRAL))
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(
            listOf(1 to VoteType.LIKE, 2 to VoteType.NEUTRAL),
            repo.submittedVotes
        )
    }

    @Test
    fun vote_doesNotRefetchAfterInitialLoad() = runTest {
        val repo = FakeCompanyRepository(
            companies = listOf(company(1, "Acme"), company(2, "Globex"))
        )
        val vm = CompanySwipeViewModel(repo)
        assertEquals(1, repo.loadCount)

        vm.onEvent(CompanyListEvent.Vote(companyId = 1, vote = VoteType.LIKE))
        vm.onEvent(CompanyListEvent.Vote(companyId = 2, vote = VoteType.DISLIKE))
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, repo.loadCount)
    }

    @Test
    fun vote_submitFails_doesNotWipePlaceWithError() = runTest {
        val repo = FakeCompanyRepository(
            companies = listOf(company(1, "Acme"), company(2, "Globex")),
            shouldFailVote = true
        )
        val vm = CompanySwipeViewModel(repo)

        vm.onEvent(CompanyListEvent.Vote(companyId = 1, vote = VoteType.LIKE))
        dispatcher.scheduler.advanceUntilIdle()

        assertFalse(vm.state.value.hasError)
        assertEquals(1, vm.state.value.currentIndex)
    }

    private fun company(id: Int, name: String, vote: VoteType? = null) = Company(
        id = id,
        userId = "user-$id",
        name = name,
        shortDescription = "",
        description = "",
        website = "",
        logoUrl = "",
        imageUrls = emptyList(),
        active = true,
        lastUpdated = "",
        vote = vote
    )

    private class FakeCompanyRepository(
        private val companies: List<Company> = emptyList(),
        private val shouldFailLoad: Boolean = false,
        private val shouldFailVote: Boolean = false
    ) : CompanyRepository {
        val submittedVotes = mutableListOf<Pair<Int, VoteType>>()
        var loadCount = 0
            private set

        override suspend fun getActiveCompanies(): List<Company> {
            loadCount++
            if (shouldFailLoad) throw RuntimeException("load failed")
            return companies
        }

        override suspend fun submitVote(companyId: Int, vote: VoteType) {
            if (shouldFailVote) throw RuntimeException("vote failed")
            submittedVotes += companyId to vote
        }
    }
}
