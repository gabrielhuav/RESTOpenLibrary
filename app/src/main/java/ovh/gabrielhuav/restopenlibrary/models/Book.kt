package ovh.gabrielhuav.restopenlibrary.models

/**
 * Representa un libro de la API de OpenLibrary
 */
data class Book(
    val key: String? = "",
    val title: String? = "",
    val author_name: List<String>? = emptyList(),
    val first_publish_year: Int? = 0,
    val cover_i: Int? = null
) {
    /**
     * Obtiene una representación formateada de los autores
     */
    fun getAuthorText(): String = author_name?.joinToString(", ") ?: "Unknown Author"

    /**
     * Obtiene la URL de la portada del libro
     */
    fun getCoverUrl(): String {
        return if (cover_i != null) {
            "https://covers.openlibrary.org/b/id/$cover_i-M.jpg"
        } else {
            ""
        }
    }

    /**
     * Devuelve un ID limpio sin el slash inicial para usar con la API de Backend
     */
    fun getCleanBookId(): String {
        // Obtener el ID eliminando el slash inicial si existe
        val id = key ?: ""
        return if (id.startsWith("/")) id.substring(1) else id
    }
}