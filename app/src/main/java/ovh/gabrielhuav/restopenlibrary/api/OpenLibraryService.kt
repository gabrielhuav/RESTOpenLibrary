package ovh.gabrielhuav.restopenlibrary.api

import ovh.gabrielhuav.restopenlibrary.models.SearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interfaz para interactuar con la API de OpenLibrary
 */
interface OpenLibraryService {
    /**
     * Busca libros en OpenLibrary usando el término de búsqueda proporcionado
     * @param query El término de búsqueda
     * @return Un objeto Call que representa la solicitud asíncrona
     */
    @GET("search.json")
    fun searchBooks(@Query("q") query: String): Call<SearchResponse>
}