package fh.msd.jobdating.core.di

import fh.msd.jobdating.BuildKonfig
import fh.msd.jobdating.core.network.HttpClientFactory
import fh.msd.jobdating.core.session.UserSession
import fh.msd.jobdating.feature.appointments.data.repository.AppointmentRepository
import fh.msd.jobdating.feature.appointments.data.repository.AppointmentRepositoryImpl
import fh.msd.jobdating.feature.appointments.data.service.AppointmentService
import fh.msd.jobdating.feature.appointments.data.service.AppointmentServiceImpl
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
import fh.msd.jobdating.feature.companies.ui.CompanySwipeViewModel
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
            install(Auth) {
                autoLoadFromStorage = false
                autoSaveToStorage = false
            }
        }
    }

    // --- session ---
    single { UserSession() }

    // Auth
    single<AuthService> { AuthServiceImpl(get(), get()) }
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    viewModel { LoginViewModel(get()) }

    // Companies
    single<CompanyService> { CompanyServiceImpl(get(), get()) }
    single<CompanyRepository> { CompanyRepositoryImpl(get(), get()) }
    viewModel { CompanySwipeViewModel(get()) }
    viewModel { CompanyListViewModel(get()) }


// --- appointments ---
    single<AppointmentService> { AppointmentServiceImpl(get(), get(), get()) }
    single<AppointmentRepository> { AppointmentRepositoryImpl(get()) }
    viewModel { AppointmentViewModel(get(), get()) }



    // --- Profile ---
    viewModel { ProfileViewModel(get(), get(), get()) }

}