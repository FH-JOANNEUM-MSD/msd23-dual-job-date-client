package fh.msd.jobdating.core.di

import fh.msd.jobdating.feature.appointments.data.repository.AppointmentRepository
import fh.msd.jobdating.feature.appointments.data.repository.AppointmentRepositoryTest
import fh.msd.jobdating.feature.appointments.data.service.AppointmentService
import fh.msd.jobdating.feature.appointments.data.service.AppointmentServiceTest
import fh.msd.jobdating.feature.appointments.ui.AppointmentViewModel
import fh.msd.jobdating.feature.auth.data.repository.AuthRepository
import fh.msd.jobdating.feature.auth.data.repository.AuthRepositoryTest
import fh.msd.jobdating.feature.auth.data.service.AuthService
import fh.msd.jobdating.feature.auth.data.service.AuthServiceTest
import fh.msd.jobdating.feature.auth.ui.LoginViewModel
import fh.msd.jobdating.feature.companies.data.repository.CompanyRepository
import fh.msd.jobdating.feature.companies.data.repository.CompanyRepositoryTest
import fh.msd.jobdating.feature.companies.data.service.CompanyService
import fh.msd.jobdating.feature.companies.data.service.CompanyServiceTest
import fh.msd.jobdating.feature.companies.ui.CompanyListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // --- auth ---
    single<AuthService> { AuthServiceTest() }
    single<AuthRepository> { AuthRepositoryTest(get()) }
    viewModel { LoginViewModel(get()) }

    // --- companies ---
    single<CompanyService> { CompanyServiceTest() }
    single<CompanyRepository> { CompanyRepositoryTest(get()) }
    viewModel { CompanyListViewModel(get()) }

    // --- appointments ---
    single<AppointmentService> { AppointmentServiceTest() }
    single<AppointmentRepository> { AppointmentRepositoryTest(get()) }
    viewModel { AppointmentViewModel(get()) }
}