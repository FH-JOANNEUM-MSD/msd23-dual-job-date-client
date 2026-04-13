package fh.msd.jobdating.core.domain.model

data class Student(
    val id: Int,
    val userId: String,
    val studyProgram: String?,
    val semester: Int?,
    val firstName: String?,
    val lastName: String?
)