package ovh.gabrielhuav.restopenlibrary.api

import ovh.gabrielhuav.restopenlibrary.models.SearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenLibraryService {
    @GET("search.json")
    fun searchBooks(@Query("q") query: String): Call<SearchResponse>
}
