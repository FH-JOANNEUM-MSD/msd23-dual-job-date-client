package fh.msd.jobdating.feature.appointments.data.service

import fh.msd.jobdating.BuildKonfig
import fh.msd.jobdating.core.session.UserSession
import fh.msd.jobdating.feature.appointments.domain.dto.AppointmentDto
import fh.msd.jobdating.feature.appointments.domain.dto.EventDto
import fh.msd.jobdating.feature.appointments.domain.dto.MeetingDto
import fh.msd.jobdating.feature.appointments.domain.dto.SlotDto
import fh.msd.jobdating.feature.companies.domain.dto.CompanyDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders

class AppointmentServiceImpl(
    private val supabaseClient: SupabaseClient,
    private val httpClient: HttpClient,
    private val userSession: UserSession
) : AppointmentService {

    private fun getAccessToken(): String {
        val token = supabaseClient.auth.currentSessionOrNull()?.accessToken
        return token ?: error("No active session")
    }

    override suspend fun getActiveEvent(): EventDto {
        val token = getAccessToken()
        return httpClient.get("${BuildKonfig.BACKEND_BASE_URL}/api/events/active") {
            header(HttpHeaders.Authorization, "Bearer $token")
            accept(ContentType.Application.Json)
        }.body()
    }

    override suspend fun getMyAppointments(): List<AppointmentDto> {
        val studentId = userSession.getStudentId()

        val token = getAccessToken()

        val event = getActiveEvent()

        val meetings: List<MeetingDto> = httpClient.get("${BuildKonfig.BACKEND_BASE_URL}/api/students/$studentId/meetings") {
            header(HttpHeaders.Authorization, "Bearer $token")
            accept(ContentType.Application.Json)
        }.body()

        val slots: List<SlotDto> = httpClient.get("${BuildKonfig.BACKEND_BASE_URL}/api/slots") {
            header(HttpHeaders.Authorization, "Bearer $token")
            accept(ContentType.Application.Json)
        }.body()

        val companies: List<CompanyDto> = httpClient.get("${BuildKonfig.BACKEND_BASE_URL}/api/companies/active") {
            header(HttpHeaders.Authorization, "Bearer $token")
            accept(ContentType.Application.Json)
        }.body()

        val slotMap = slots.associateBy { it.id }
        val companyMap = companies.associateBy { it.id }

        return meetings
            .sortedBy { meeting -> slotMap[meeting.slotId]?.startTime ?: "" }
            .map { meeting ->
                val slot = slotMap[meeting.slotId]
                val company = companyMap[meeting.companyId]

                val startTime = slot?.startTime?.take(5) ?: "??:??"
                val endTime = slot?.endTime?.take(5) ?: "??:??"
                val companyName = company?.name ?: "Company #${meeting.companyId}"
                val timeSlot = "${event.eventDate} • $startTime - $endTime"

                AppointmentDto(
                    id = meeting.id.toString(),
                    companyId = meeting.companyId.toString(),
                    companyName = companyName,
                    timeSlot = timeSlot
                )
            }
    }
}