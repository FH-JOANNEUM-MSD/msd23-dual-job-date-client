package fh.msd.jobdating.core.session

import com.russhwolf.settings.Settings
import fh.msd.jobdating.core.domain.model.User
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class UserSession(private val settings: Settings = Settings()) {

    private var currentUser: User? = null

    init {
        currentUser = loadUser()
    }

    fun setUser(user: User) {
        currentUser = user
        saveUser(user)
    }

    fun getStudentId(): Int? = currentUser?.studentId

    fun getUser(): User? = currentUser

    fun clear() {
        currentUser = null
        settings.remove("user")
    }

    fun isLoggedIn(): Boolean = currentUser != null

    private fun saveUser(user: User) {
        val json = Json.encodeToString(user)
        settings.putString("user", json)
    }

    private fun loadUser(): User? {
        return try {
            val json = settings.getStringOrNull("user")
            json?.let { Json.decodeFromString<User>(it) }
        } catch (e: Exception) {
            null
        }
    }
}