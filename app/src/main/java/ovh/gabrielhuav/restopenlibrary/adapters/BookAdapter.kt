package ovh.gabrielhuav.restopenlibrary.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ovh.gabrielhuav.restopenlibrary.R
import ovh.gabrielhuav.restopenlibrary.api.AddFavoriteRequest
import ovh.gabrielhuav.restopenlibrary.api.ApiClient
import ovh.gabrielhuav.restopenlibrary.api.SessionManager
import ovh.gabrielhuav.restopenlibrary.models.Book
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookAdapter(private var books: List<Book> = emptyList()) :
    RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    private val TAG = "BookAdapter"

    // Mapa para almacenar el estado de favorito de cada libro
    private val favoriteStatus = mutableMapOf<String, Boolean>()

    class BookViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.bookTitle)
        val authorTextView: TextView = view.findViewById(R.id.bookAuthor)
        val yearTextView: TextView = view.findViewById(R.id.bookYear)
        val coverImageView: ImageView = view.findViewById(R.id.bookCover)
        val favoriteButton: Button = view.findViewById(R.id.favoriteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.book_item, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]
        val bookId = book.getCleanBookId() // Usar el ID limpio

        holder.titleTextView.text = book.title ?: "Unknown Title"
        holder.authorTextView.text = book.getAuthorText()
        holder.yearTextView.text = book.first_publish_year?.toString() ?: "Unknown Year"

        // Cargar la imagen de la portada con Glide
        if (book.cover_i != null) {
            Glide.with(holder.coverImageView.context)
                .load(book.getCoverUrl())
                .placeholder(R.drawable.book_placeholder)
                .error(R.drawable.book_placeholder)
                .into(holder.coverImageView)
        } else {
            holder.coverImageView.setImageResource(R.drawable.book_placeholder)
        }

        // Configuración del botón de favoritos según estado de login
        if (SessionManager.isLoggedIn()) {
            holder.favoriteButton.visibility = View.VISIBLE

            // Verificar si ya es favorito
            if (!favoriteStatus.containsKey(bookId) && !bookId.isEmpty()) {
                checkFavoriteStatus(bookId)
            }

            // Actualizar apariencia del botón según estado
            updateFavoriteButton(holder.favoriteButton, favoriteStatus[bookId] == true)

            // Configurar click del botón
            holder.favoriteButton.setOnClickListener {
                toggleFavorite(holder, book)
            }
        } else {
            holder.favoriteButton.visibility = View.GONE
        }
    }

    private fun updateFavoriteButton(button: Button, isFavorite: Boolean) {
        if (isFavorite) {
            button.text = "Quitar de favoritos"
            button.setBackgroundResource(R.drawable.favorite_button_remove)
        } else {
            button.text = "Añadir a favoritos"
            button.setBackgroundResource(R.drawable.favorite_button_add)
        }
    }

    private fun checkFavoriteStatus(bookId: String) {
        if (!SessionManager.isLoggedIn() || bookId.isEmpty()) return

        ApiClient.favoriteService.checkFavorite(bookId).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val responseText = response.body() ?: ""
                    Log.d(TAG, "CheckFavorite response: $responseText")

                    // Verificar si la respuesta contiene "true" o alguna palabra clave que indique que es favorito
                    val isFavorite = responseText.contains("true", ignoreCase = true) ||
                            responseText.contains("esFavorito", ignoreCase = true)

                    favoriteStatus[bookId] = isFavorite
                    notifyDataSetChanged()
                } else {
                    // En caso de error, asumimos que no es favorito
                    Log.e(TAG, "Error checking favorite status: ${response.code()}")
                    favoriteStatus[bookId] = false
                    notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                // En caso de error, asumimos que no es favorito
                Log.e(TAG, "Network error checking favorite: ${t.message}")
                favoriteStatus[bookId] = false
                notifyDataSetChanged()
            }
        })
    }

    private fun toggleFavorite(holder: BookViewHolder, book: Book) {
        val bookId = book.getCleanBookId() // Usar el ID limpio
        if (bookId.isEmpty()) return

        val currentStatus = favoriteStatus[bookId] ?: false

        if (currentStatus) {
            // Eliminar de favoritos
            ApiClient.favoriteService.removeFavorite(bookId).enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        val responseText = response.body() ?: ""
                        Log.d(TAG, "RemoveFavorite response: $responseText")

                        // Si la respuesta contiene palabras clave de éxito
                        val isSuccess = responseText.contains("eliminado", ignoreCase = true) ||
                                responseText.contains("success", ignoreCase = true)

                        if (isSuccess) {
                            favoriteStatus[bookId] = false
                            updateFavoriteButton(holder.favoriteButton, false)
                            Toast.makeText(holder.itemView.context, "Eliminado de favoritos", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(holder.itemView.context, "Error: $responseText", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Log.e(TAG, "Error removing favorite: ${response.code()}")
                        Toast.makeText(holder.itemView.context, "Error al eliminar de favoritos", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.e(TAG, "Network error removing favorite: ${t.message}")
                    Toast.makeText(holder.itemView.context, "Error de conexión", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            // Añadir a favoritos - Intenta primero con FormUrlEncoded
            try {
                addFavoriteWithForm(holder, book, bookId)
            } catch (e: Exception) {
                Log.e(TAG, "Error with form method, trying JSON", e)
                addFavoriteWithJson(holder, book, bookId)
            }
        }
    }

    private fun addFavoriteWithForm(holder: BookViewHolder, book: Book, bookId: String) {
        ApiClient.favoriteService.addFavorite(
            bookId,
            book.title ?: "Sin título",
            book.getAuthorText(),
            book.getCoverUrl()
        ).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                handleAddFavoriteResponse(response, holder, bookId)
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e(TAG, "Network error adding favorite (form): ${t.message}")
                // Intentar con el método JSON como respaldo
                addFavoriteWithJson(holder, book, bookId)
            }
        })
    }

    private fun addFavoriteWithJson(holder: BookViewHolder, book: Book, bookId: String) {
        val request = AddFavoriteRequest(
            libroId = bookId,
            titulo = book.title ?: "Sin título",
            autor = book.getAuthorText(),
            imagenUrl = book.getCoverUrl()
        )

        ApiClient.favoriteService.addFavoriteJson(request).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                handleAddFavoriteResponse(response, holder, bookId)
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e(TAG, "Network error adding favorite (JSON): ${t.message}")
                Toast.makeText(holder.itemView.context, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun handleAddFavoriteResponse(response: Response<String>, holder: BookViewHolder, bookId: String) {
        if (response.isSuccessful) {
            val responseText = response.body() ?: ""
            Log.d(TAG, "AddFavorite response: $responseText")

            // Si la respuesta contiene palabras clave de éxito
            val isSuccess = responseText.contains("agregado", ignoreCase = true) ||
                    responseText.contains("success", ignoreCase = true) ||
                    responseText.contains("añadido", ignoreCase = true) ||
                    responseText.contains("favorito", ignoreCase = true)

            if (isSuccess) {
                favoriteStatus[bookId] = true
                updateFavoriteButton(holder.favoriteButton, true)
                Toast.makeText(holder.itemView.context, "Añadido a favoritos", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(holder.itemView.context, "Error: $responseText", Toast.LENGTH_SHORT).show()
            }
        } else {
            try {
                val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                Log.e(TAG, "Error adding favorite: $errorBody")
                Toast.makeText(holder.itemView.context, "Error al añadir a favoritos: $errorBody", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing error response", e)
                Toast.makeText(holder.itemView.context, "Error al añadir a favoritos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount() = books.size

    fun updateBooks(newBooks: List<Book>) {
        books = newBooks
        notifyDataSetChanged()
    }
}