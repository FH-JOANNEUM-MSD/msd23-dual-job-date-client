package fh.msd.jobdating.feature.auth.data.service

import fh.msd.jobdating.BuildKonfig
import fh.msd.jobdating.core.domain.model.User
import fh.msd.jobdating.feature.auth.domain.dto.LoginRequestDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders

class AuthServiceImpl(
    private val supabaseClient: SupabaseClient,
    private val httpClient: HttpClient
) : AuthService {

    override suspend fun login(request: LoginRequestDto): String {
        supabaseClient.auth.signInWith(Email) {
            email = request.email
            password = request.password
        }
        val token = supabaseClient.auth.currentSessionOrNull()?.accessToken
        return token ?: error("Login succeeded but no session found")
    }

    override suspend fun logout() {
        supabaseClient.auth.signOut()
    }

    override suspend fun getMe(token: String): User {
        return httpClient.get("${BuildKonfig.BACKEND_BASE_URL}/api/me") {
            header(HttpHeaders.Authorization, "Bearer $token")
            accept(ContentType.Application.Json)
        }.body()
    }

    override suspend fun tryRestoreSession(): String? {
        val token = supabaseClient.auth.currentSessionOrNull()?.accessToken
        return token
    }

    override suspend fun changePassword(newPassword: String) {
        supabaseClient.auth.updateUser {
            password = newPassword
        }
    }

    override fun getCurrentUserEmail(): String? {
        return supabaseClient.auth.currentUserOrNull()?.email
    }
}