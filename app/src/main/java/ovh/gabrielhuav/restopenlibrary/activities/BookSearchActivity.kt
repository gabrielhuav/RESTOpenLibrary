package ovh.gabrielhuav.restopenlibrary.activities

import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ovh.gabrielhuav.restopenlibrary.R
import ovh.gabrielhuav.restopenlibrary.adapters.BookAdapter
import ovh.gabrielhuav.restopenlibrary.api.ApiClient
import ovh.gabrielhuav.restopenlibrary.models.SearchResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookSearchActivity : AppCompatActivity() {

    private lateinit var searchEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var resultsRecyclerView: RecyclerView
    private lateinit var bookAdapter: BookAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_search)

        // Mostrar botón de volver en ActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Búsqueda de libros"

        // Inicializar vistas
        searchEditText = findViewById(R.id.searchEditText)
        searchButton = findViewById(R.id.searchButton)
        resultsRecyclerView = findViewById(R.id.resultsRecyclerView)

        // Configurar RecyclerView
        bookAdapter = BookAdapter()
        resultsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@BookSearchActivity)
            adapter = bookAdapter
        }

        // Configurar click del botón de búsqueda
        searchButton.setOnClickListener {
            val query = searchEditText.text.toString().trim()
            if (query.isNotEmpty()) {
                searchBooks(query)
                hideKeyboard()
            } else {
                Toast.makeText(this, "Por favor ingresa un término de búsqueda", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun searchBooks(query: String) {
        // Mostrar indicador de carga
        Toast.makeText(this, "Buscando libros...", Toast.LENGTH_SHORT).show()

        // Realizar la llamada a la API
        ApiClient.openLibraryService.searchBooks(query).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                if (response.isSuccessful) {
                    val books = response.body()?.docs ?: emptyList()
                    bookAdapter.updateBooks(books)

                    if (books.isEmpty()) {
                        Toast.makeText(this@BookSearchActivity, "No se encontraron resultados", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@BookSearchActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                Toast.makeText(this@BookSearchActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            // Manejar el botón de regreso en la barra de acción
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}