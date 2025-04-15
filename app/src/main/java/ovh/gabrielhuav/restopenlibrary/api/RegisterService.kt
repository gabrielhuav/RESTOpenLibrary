package ovh.gabrielhuav.restopenlibrary.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RegisterService {
    // Cambiamos el tipo de retorno a String para manejar respuestas de texto plano
    @POST("api/register")
    fun registerUser(@Body registerRequest: RegisterRequest): Call<String>
}

data class RegisterRequest(
    val nombre: String,
    val email: String,
    val password: String
)

// No necesitamos RegisterResponse ya que estamos usando String como tipo de respuesta