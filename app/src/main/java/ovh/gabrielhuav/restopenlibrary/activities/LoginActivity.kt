package ovh.gabrielhuav.restopenlibrary.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ovh.gabrielhuav.restopenlibrary.R
import ovh.gabrielhuav.restopenlibrary.api.ApiClient
import ovh.gabrielhuav.restopenlibrary.api.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private val TAG = "LoginActivity"
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializar vistas
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerButton)
        progressBar = findViewById(R.id.progressBar)

        // Si ya estamos logueados, ir directamente a HomeActivity
        if (SessionManager.isLoggedIn()) {
            startHomeActivity()
            return
        }

        // Configurar click del botón de login
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            performLogin(email, password)
        }

        // Configurar click del botón de registro
        registerButton.setOnClickListener {
            // Navegar a la pantalla de registro
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun performLogin(email: String, password: String) {
        // Mostrar progress bar
        progressBar.visibility = View.VISIBLE
        loginButton.isEnabled = false

        val credentials = LoginCredentials(email, password)

        try {
            ApiClient.authService.login(credentials).enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    // Ocultar progress bar
                    progressBar.visibility = View.GONE
                    loginButton.isEnabled = true

                    if (response.isSuccessful) {
                        // Login exitoso
                        val responseBody = response.body() ?: ""
                        Log.d(TAG, "Login response: $responseBody")

                        // Si la respuesta contiene "success" o alguna palabra clave de éxito
                        // En un sistema real, verificarías el token o respuesta específica
                        if (responseBody.contains("success", ignoreCase = true) ||
                            responseBody.contains("exitoso", ignoreCase = true) ||
                            responseBody.contains("Login successful", ignoreCase = true) ||
                            response.code() == 200) { // A veces solo el código 200 es suficiente

                            // Extraer el token si está disponible
                            // Este es un ejemplo simple - ajusta según tu respuesta real
                            val token = if (responseBody.contains("token")) {
                                responseBody.substringAfter("token").trim()
                            } else {
                                "session-token" // Token por defecto si no hay uno en la respuesta
                            }

                            // Guardar datos de sesión incluyendo la contraseña para autenticación
                            SessionManager.login(token, email, password)
                            Toast.makeText(this@LoginActivity, "Login exitoso", Toast.LENGTH_SHORT).show()
                            startHomeActivity()
                        } else {
                            // Aun con respuesta exitosa, podría tener un mensaje de error
                            Toast.makeText(
                                this@LoginActivity,
                                "Respuesta: $responseBody",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        // Login fallido
                        try {
                            val errorBody = response.errorBody()?.string() ?: "Credenciales incorrectas"
                            Log.e(TAG, "Error en login: $errorBody")
                            Toast.makeText(this@LoginActivity, errorBody, Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing error response", e)
                            Toast.makeText(
                                this@LoginActivity,
                                "Error en login: ${response.code()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    // Ocultar progress bar
                    progressBar.visibility = View.GONE
                    loginButton.isEnabled = true

                    Log.e(TAG, "Network error", t)

                    // Para desarrollo, puedes descomentar esto para simular login exitoso
                    /*
                    SessionManager.login("dummy-token", email, password)
                    startHomeActivity()
                    return
                    */

                    Toast.makeText(
                        this@LoginActivity,
                        "Error de conexión: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } catch (e: Exception) {
            // Capturar cualquier excepción que pueda ocurrir al crear/ejecutar la llamada
            progressBar.visibility = View.GONE
            loginButton.isEnabled = true
            Log.e(TAG, "Exception during login", e)
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish() // Cerramos LoginActivity para que no se pueda volver atrás
    }
}