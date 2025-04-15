package ovh.gabrielhuav.restopenlibrary.api

/**
 * Gestiona el estado de la sesión del usuario actual.
 */
object SessionManager {
    private var isLoggedIn = false
    private var authToken: String? = null
    private var username: String? = null
    private var password: String? = null

    /**
     * Almacena la información de inicio de sesión.
     */
    fun login(token: String, user: String, pass: String? = null) {
        authToken = token
        username = user
        password = pass
        isLoggedIn = true
    }

    /**
     * Limpia la información de sesión.
     */
    fun logout() {
        authToken = null
        username = null
        password = null
        isLoggedIn = false
    }

    /**
     * Verifica si hay un usuario autenticado.
     */
    fun isLoggedIn(): Boolean = isLoggedIn

    /**
     * Obtiene el token de autenticación.
     */
    fun getAuthToken(): String? = authToken

    /**
     * Obtiene el nombre de usuario o correo.
     */
    fun getUsername(): String? = username

    /**
     * Obtiene la contraseña (para autenticación si es necesario).
     */
    fun getPassword(): String? = password
}