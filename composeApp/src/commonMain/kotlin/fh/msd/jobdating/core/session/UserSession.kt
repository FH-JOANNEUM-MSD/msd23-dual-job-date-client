package fh.msd.jobdating.core.session

import fh.msd.jobdating.core.domain.model.User

class UserSession {
    private var currentUser: User? = null

    fun setUser(user: User) {
        currentUser = user
    }

    fun getStudentId(): Int? = currentUser?.studentId

    fun getUser(): User? = currentUser

    fun clear() {
        currentUser = null
    }

    fun isLoggedIn(): Boolean = currentUser != null
}