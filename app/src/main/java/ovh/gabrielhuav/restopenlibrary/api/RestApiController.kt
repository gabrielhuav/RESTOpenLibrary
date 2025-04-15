package ovh.gabrielhuav.restopenlibrary.api

import okhttp3.ResponseBody
import ovh.gabrielhuav.restopenlibrary.models.RegisterRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RestApiController {
    // Corrigiendo el tipo de retorno a ResponseBody para que coincida con el callback
    @POST("api/register")
    fun register(@Body registerRequest: RegisterRequest): Call<ResponseBody>
}