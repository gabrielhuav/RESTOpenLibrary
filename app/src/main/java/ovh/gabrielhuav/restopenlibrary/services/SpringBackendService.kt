package ovh.gabrielhuav.restopenlibrary.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ovh.gabrielhuav.restopenlibrary.R
import ovh.gabrielhuav.restopenlibrary.models.Book

class BookAdapter(private var books: List<Book> = emptyList()) :
    RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    class BookViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.bookTitle)
        val authorTextView: TextView = view.findViewById(R.id.bookAuthor)
        val yearTextView: TextView = view.findViewById(R.id.bookYear)
        val coverImageView: ImageView = view.findViewById(R.id.bookCover)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.book_item, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]

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
    }

    override fun getItemCount() = books.size

    fun updateBooks(newBooks: List<Book>) {
        books = newBooks
        notifyDataSetChanged()
    }
}