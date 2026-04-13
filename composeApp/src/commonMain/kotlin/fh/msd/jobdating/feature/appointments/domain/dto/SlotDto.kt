package fh.msd.jobdating.feature.appointments.domain.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SlotDto(
    val id: Int,
    @SerialName("start_time") val startTime: String,
    @SerialName("end_time") val endTime: String
)