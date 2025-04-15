package ovh.gabrielhuav.restopenlibrary.api

import android.util.Log
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val TAG = "ApiClient"

    // Cambiar a la URL de tu servidor backend Spring
    private const val BACKEND_URL = "http://192.168.0.108:8080/" // Asume localhost (emulador Android)
    private const val OPEN_LIBRARY_URL = "https://openlibrary.org/"

    // Interceptor para añadir token de autenticación cuando sea necesario
    private class AuthInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()

            // Solo modificamos solicitudes al backend, no a OpenLibrary
            if (!originalRequest.url.toString().contains("openlibrary.org")) {
                val requestBuilder = originalRequest.newBuilder()

                // Solo añadir el token si estamos autenticados
                if (SessionManager.isLoggedIn()) {
                    // Añadir credenciales en el encabezado
                    // Opción 1: Usando Authorization con Bearer token (más común)
                    val token = SessionManager.getAuthToken() ?: "session-token"
                    requestBuilder.header("Authorization", "Bearer $token")

                    // Opción 2: Añadiendo un header personalizado de usuario (por si acaso)
                    val username = SessionManager.getUsername() ?: ""
                    requestBuilder.header("X-User-Email", username)

                    // Loguear información de headers para depuración
                    Log.d(TAG, "Headers enviados: Authorization=Bearer $token, X-User-Email=$username")
                }

                return chain.proceed(requestBuilder.build())
            }

            // Para solicitudes a OpenLibrary, dejamos pasar sin modificar
            return chain.proceed(originalRequest)
        }
    }

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor(AuthInterceptor())
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    // Crear un Gson tolerante a JSON malformado
    private val gson = GsonBuilder()
        .setLenient()
        .create()

    // Retrofit para OpenLibrary
    private val openLibraryRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(OPEN_LIBRARY_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    // Retrofit para nuestro backend
    private val backendRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BACKEND_URL)
            .client(httpClient)
            // Primero intentamos con String converter para manejar respuestas de texto
            .addConverterFactory(StringConverterFactory.create())
            // Luego intentamos con Gson para manejar respuestas JSON
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    // Servicios
    val openLibraryService: OpenLibraryService by lazy {
        openLibraryRetrofit.create(OpenLibraryService::class.java)
    }

    val authService: AuthService by lazy {
        backendRetrofit.create(AuthService::class.java)
    }

    val registerService: RegisterService by lazy {
        backendRetrofit.create(RegisterService::class.java)
    }

    val favoriteService: FavoriteService by lazy {
        backendRetrofit.create(FavoriteService::class.java)
    }
}