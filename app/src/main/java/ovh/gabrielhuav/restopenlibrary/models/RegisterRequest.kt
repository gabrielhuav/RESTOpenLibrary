// RegisterRequest.kt
package ovh.gabrielhuav.restopenlibrary.models

data class RegisterRequest(
    val nombre: String,
    val email: String,
    val password: String
)