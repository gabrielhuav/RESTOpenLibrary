package ovh.gabrielhuav.restopenlibrary.models

data class SearchResponse(
    val numFound: Int,
    val start: Int,
    val docs: List<Book>
)

data class Book(
    val key: String? = "",
    val title: String? = "",
    val author_name: List<String>? = emptyList(),
    val first_publish_year: Int? = 0,
    val cover_i: Int? = null
) {
    fun getAuthorText(): String = author_name?.joinToString(", ") ?: "Unknown Author"

    fun getCoverUrl(): String {
        return if (cover_i != null) {
            "https://covers.openlibrary.org/b/id/$cover_i-M.jpg"
        } else {
            ""
        }
    }
}