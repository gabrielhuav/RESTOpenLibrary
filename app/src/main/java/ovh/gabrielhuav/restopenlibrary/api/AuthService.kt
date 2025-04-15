package ovh.gabrielhuav.restopenlibrary.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Interfaz para los servicios de autenticación de la API.
 */
interface AuthService {
    /**
     * Método para iniciar sesión en la API.
     * @param credentials Las credenciales del usuario (correo y contraseña)
     * @return Un objeto Call que representa la solicitud asíncrona
     */
    @POST("api/auth/login")
    fun login(@Body credentials: LoginCredentials): Call<String>
}

/**
 * Clase para las credenciales de inicio de sesión.
 * @param correo El correo electrónico del usuario
 * @param password La contraseña del usuario
 */
data class LoginCredentials(
    val correo: String,
    val password: String
)