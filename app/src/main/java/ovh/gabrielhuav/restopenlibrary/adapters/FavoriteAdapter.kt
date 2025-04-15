package ovh.gabrielhuav.restopenlibrary.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ovh.gabrielhuav.restopenlibrary.R
import ovh.gabrielhuav.restopenlibrary.models.Favorite
import java.text.SimpleDateFormat
import java.util.Locale

class FavoriteAdapter(
    private val onRemoveClick: (Favorite) -> Unit,
    private var favorites: MutableList<Favorite> = mutableListOf()
) : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    class FavoriteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.favoriteTitle)
        val authorTextView: TextView = view.findViewById(R.id.favoriteAuthor)
        val dateTextView: TextView = view.findViewById(R.id.favoriteDate)
        val coverImageView: ImageView = view.findViewById(R.id.favoriteCover)
        val removeButton: Button = view.findViewById(R.id.removeButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.favorite_item, parent, false)
        return FavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val favorite = favorites[position]

        holder.titleTextView.text = favorite.titulo
        holder.authorTextView.text = favorite.autor

        // Formatear la fecha si es necesario
        try {
            // Suponiendo que el formato de fecha es ISO 8601 (2023-04-15T10:30:45)
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = inputFormat.parse(favorite.fechaAgregado)
            val formattedDate = date?.let { outputFormat.format(it) }
            holder.dateTextView.text = "Agregado: $formattedDate"
        } catch (e: Exception) {
            // Si hay un error al parsear la fecha, mostrar el texto sin formato
            holder.dateTextView.text = "Agregado: ${favorite.fechaAgregado}"
        }

        // Cargar la imagen con Glide
        Glide.with(holder.coverImageView.context)
            .load(favorite.imagenUrl)
            .placeholder(R.drawable.book_placeholder)
            .error(R.drawable.book_placeholder)
            .into(holder.coverImageView)

        // Configurar el botón de eliminar
        holder.removeButton.setOnClickListener {
            onRemoveClick(favorite)
        }
    }

    override fun getItemCount() = favorites.size

    fun updateFavorites(newFavorites: List<Favorite>) {
        favorites.clear()
        favorites.addAll(newFavorites)
        notifyDataSetChanged()
    }

    fun removeFavorite(favorite: Favorite) {
        val position = favorites.indexOf(favorite)
        if (position != -1) {
            favorites.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}