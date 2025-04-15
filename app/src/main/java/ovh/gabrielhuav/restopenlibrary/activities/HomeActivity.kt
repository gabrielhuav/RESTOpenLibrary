package ovh.gabrielhuav.restopenlibrary.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ovh.gabrielhuav.restopenlibrary.MainActivity
import ovh.gabrielhuav.restopenlibrary.R
import ovh.gabrielhuav.restopenlibrary.api.SessionManager

class HomeActivity : AppCompatActivity() {

    private lateinit var welcomeTextView: TextView
    private lateinit var searchBooksButton: Button
    private lateinit var logoutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Inicializar vistas
        welcomeTextView = findViewById(R.id.welcomeTextView)
        searchBooksButton = findViewById(R.id.searchBooksButton)
        logoutButton = findViewById(R.id.logoutButton)

        // Configurar mensaje de bienvenida
        val username = SessionManager.getUsername() ?: "Usuario"
        welcomeTextView.text = "Bienvenido, $username"

        // Configurar botón de búsqueda de libros
        searchBooksButton.setOnClickListener {
            val intent = Intent(this, BookSearchActivity::class.java)
            startActivity(intent)
        }

        // Configurar botón de cierre de sesión
        logoutButton.setOnClickListener {
            // Cerrar sesión
            SessionManager.logout()

            // Redirigir a la actividad principal
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()

            Toast.makeText(this, "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_profile -> {
                // Navegar al perfil
                Toast.makeText(this, "Perfil no implementado aún", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_favorites -> {
                // Implementa la navegación a tus favoritos
                // val intent = Intent(this, FavoritesActivity::class.java)
                // startActivity(intent)
                Toast.makeText(this, "Favoritos no implementado aún", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_logout -> {
                // Cerrar sesión
                SessionManager.logout()

                // Redirigir a la actividad principal
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}