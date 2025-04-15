package ovh.gabrielhuav.restopenlibrary.api

/**
 * Clase para deserializar la respuesta del servidor cuando se elimina un libro de favoritos.
 */
data class RemoveFavoriteResponse(
    val mensaje: String,
    // Si el backend devuelve algún código de estado o info adicional, puedes agregarlo aquí
    val success: Boolean = true
)