package ovh.gabrielhuav.restopenlibrary.api

import ovh.gabrielhuav.restopenlibrary.models.Favorite
import retrofit2.Call
import retrofit2.http.*

interface FavoriteService {
    // Verificar si un libro está en favoritos
    @GET("api/libros/favoritos/verificar/{libroId}")
    fun checkFavorite(@Path("libroId") bookId: String): Call<String>

    // Añadir libro a favoritos - usando application/x-www-form-urlencoded
    @FormUrlEncoded
    @POST("api/libros/favoritos/agregar")
    fun addFavorite(
        @Field("libroId") bookId: String,
        @Field("titulo") title: String,
        @Field("autor") author: String,
        @Field("imagenUrl") imageUrl: String
    ): Call<String>

    // Versión JSON alternativa para agregar favoritos
    @Headers("Content-Type: application/json")
    @POST("api/libros/favoritos/agregar")
    fun addFavoriteJson(@Body favoriteRequest: AddFavoriteRequest): Call<String>

    // Eliminar libro de favoritos
    @DELETE("api/libros/favoritos/eliminar/{libroId}")
    fun removeFavorite(@Path("libroId") bookId: String): Call<String>

    // Obtener todos los favoritos del usuario
    @GET("api/libros/favoritos/lista")
    fun getFavorites(): Call<List<Favorite>>
}

data class AddFavoriteRequest(
    val libroId: String,
    val titulo: String,
    val autor: String,
    val imagenUrl: String
)