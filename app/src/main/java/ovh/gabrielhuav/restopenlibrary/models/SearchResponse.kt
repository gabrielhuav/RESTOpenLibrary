package ovh.gabrielhuav.restopenlibrary.models

data class SearchResponse(
    val numFound: Int,
    val start: Int,
    val docs: List<Book>
)