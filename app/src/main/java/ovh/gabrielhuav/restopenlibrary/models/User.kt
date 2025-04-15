
// User.kt
package ovh.gabrielhuav.restopenlibrary.models

data class User(
    val id: Long,
    val nombre: String,
    val email: String,
    var roles: List<String> = listOf()
)