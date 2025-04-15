package ovh.gabrielhuav.restopenlibrary.activities

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ovh.gabrielhuav.restopenlibrary.R
import ovh.gabrielhuav.restopenlibrary.adapters.FavoriteAdapter
import ovh.gabrielhuav.restopenlibrary.api.ApiClient
import ovh.gabrielhuav.restopenlibrary.models.Favorite
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoritesActivity : AppCompatActivity() {

    private val TAG = "FavoritesActivity"
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyView: TextView
    private lateinit var favoriteAdapter: FavoriteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        // Configurar la barra de acción para mostrar el botón de volver
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Mis Libros Favoritos"

        // Inicializar vistas
        recyclerView = findViewById(R.id.favoritesRecyclerView)
        progressBar = findViewById(R.id.progressBar)
        emptyView = findViewById(R.id.emptyView)

        // Configurar RecyclerView
        favoriteAdapter = FavoriteAdapter(
            onRemoveClick = { favorite ->
                // Implementar la eliminación de favoritos
                removeFavorite(favorite)
            }
        )

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@FavoritesActivity)
            adapter = favoriteAdapter
        }

        // Cargar los favoritos
        loadFavorites()
    }

    private fun loadFavorites() {
        progressBar.visibility = View.VISIBLE
        emptyView.visibility = View.GONE

        ApiClient.favoriteService.getFavorites().enqueue(object : Callback<List<Favorite>> {
            override fun onResponse(call: Call<List<Favorite>>, response: Response<List<Favorite>>) {
                progressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    val favorites = response.body() ?: emptyList()
                    favoriteAdapter.updateFavorites(favorites)

                    if (favorites.isEmpty()) {
                        emptyView.visibility = View.VISIBLE
                    } else {
                        emptyView.visibility = View.GONE
                    }
                } else {
                    Log.e(TAG, "Error loading favorites: ${response.code()}")
                    Toast.makeText(
                        this@FavoritesActivity,
                        "Error al cargar favoritos: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                    emptyView.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<List<Favorite>>, t: Throwable) {
                progressBar.visibility = View.GONE
                emptyView.visibility = View.VISIBLE

                Log.e(TAG, "Network error loading favorites", t)
                Toast.makeText(
                    this@FavoritesActivity,
                    "Error de conexión: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun removeFavorite(favorite: Favorite) {
        progressBar.visibility = View.VISIBLE

        // Usamos el método que devuelve Call<String>
        ApiClient.favoriteService.removeFavorite(favorite.libroId).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                progressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    // Obtener la respuesta de texto
                    val responseText = response.body() ?: ""
                    Log.d(TAG, "Remove favorite response: $responseText")

                    // Si la respuesta contiene palabras clave de éxito
                    val isSuccess = responseText.contains("eliminado", ignoreCase = true) ||
                            responseText.contains("success", ignoreCase = true) ||
                            responseText.contains("removed", ignoreCase = true)

                    if (isSuccess) {
                        // Eliminar el favorito de la lista y actualizar la interfaz
                        favoriteAdapter.removeFavorite(favorite)

                        // Mostrar mensaje de éxito
                        Toast.makeText(
                            this@FavoritesActivity,
                            "Libro eliminado de favoritos",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Comprobar si ahora la lista está vacía
                        if (favoriteAdapter.itemCount == 0) {
                            emptyView.visibility = View.VISIBLE
                        }
                    } else {
                        // Mostrar mensaje de error
                        Toast.makeText(
                            this@FavoritesActivity,
                            "Error: $responseText",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    try {
                        val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                        Log.e(TAG, "Error removing favorite: $errorBody")
                        Toast.makeText(
                            this@FavoritesActivity,
                            "Error al eliminar favorito: $errorBody",
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing error response", e)
                        Toast.makeText(
                            this@FavoritesActivity,
                            "Error al eliminar favorito: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                progressBar.visibility = View.GONE

                Log.e(TAG, "Network error removing favorite", t)
                Toast.makeText(
                    this@FavoritesActivity,
                    "Error de conexión: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Volver atrás al presionar el botón de navegación
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}