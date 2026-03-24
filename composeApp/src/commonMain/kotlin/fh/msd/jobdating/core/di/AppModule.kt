package fh.msd.jobdating.core.di

import fh.msd.jobdating.BuildKonfig
import fh.msd.jobdating.core.network.HttpClientFactory
import fh.msd.jobdating.feature.appointments.data.repository.AppointmentRepository
import fh.msd.jobdating.feature.appointments.data.repository.AppointmentRepositoryTest
import fh.msd.jobdating.feature.appointments.data.service.AppointmentService
import fh.msd.jobdating.feature.appointments.data.service.AppointmentServiceTest
import fh.msd.jobdating.feature.appointments.ui.AppointmentViewModel
import fh.msd.jobdating.feature.auth.data.repository.AuthRepository
import fh.msd.jobdating.feature.auth.data.repository.AuthRepositoryImpl
import fh.msd.jobdating.feature.auth.data.service.AuthService
import fh.msd.jobdating.feature.auth.data.service.AuthServiceImpl
import fh.msd.jobdating.feature.auth.ui.LoginViewModel
import fh.msd.jobdating.feature.companies.data.repository.CompanyRepository
import fh.msd.jobdating.feature.companies.data.repository.CompanyRepositoryImpl
import fh.msd.jobdating.feature.companies.data.service.CompanyService
import fh.msd.jobdating.feature.companies.data.service.CompanyServiceImpl
import fh.msd.jobdating.feature.companies.ui.CompanyListViewModel
import fh.msd.jobdating.feature.profile.data.repository.ProfileRepository
import fh.msd.jobdating.feature.profile.data.repository.ProfileRepositoryTest
import fh.msd.jobdating.feature.profile.ui.ProfileViewModel
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // --- network ---
    single { HttpClientFactory.create() }

    single {
        createSupabaseClient(
            supabaseUrl = BuildKonfig.SUPABASE_URL,
            supabaseKey = BuildKonfig.SUPABASE_ANON_KEY
        ) {
            install(Auth)
        }
    }

    // --- auth ---
    single<AuthService> { AuthServiceImpl(get(), get()) }
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    viewModel { LoginViewModel(get()) }

    // --- companies ---
    single<CompanyService> { CompanyServiceImpl(get(), get()) }
    single<CompanyRepository> { CompanyRepositoryImpl(get()) }
    viewModel { CompanyListViewModel(get()) }

    // --- appointments ---
    single<AppointmentService> { AppointmentServiceTest() }
    single<AppointmentRepository> { AppointmentRepositoryTest(get()) }
    viewModel { AppointmentViewModel(get()) }

    // --- Profile ---
    single<ProfileRepository> { ProfileRepositoryTest() }
    viewModel { ProfileViewModel(get()) }

}