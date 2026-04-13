package fh.msd.jobdating.feature.appointments.domain.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventDto(
    val id: Int,
    val name: String,
    val location: String,
    val description: String,
    @SerialName("event_date") val eventDate: String,
    @SerialName("is_active") val isActive: Boolean
)