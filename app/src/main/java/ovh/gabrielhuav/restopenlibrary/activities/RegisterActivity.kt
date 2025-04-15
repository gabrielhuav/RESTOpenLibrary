package ovh.gabrielhuav.restopenlibrary.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ovh.gabrielhuav.restopenlibrary.R
import ovh.gabrielhuav.restopenlibrary.api.ApiClient
import ovh.gabrielhuav.restopenlibrary.api.RegisterRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private val TAG = "RegisterActivity"
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Configurar la barra de acción para mostrar el botón de volver
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Registro de Usuario"

        // Inicializar vistas
        nameEditText = findViewById(R.id.nameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)
        registerButton = findViewById(R.id.registerButton)
        progressBar = findViewById(R.id.progressBar)

        // Configurar click del botón de registro
        registerButton.setOnClickListener {
            if (validateInputs()) {
                registerUser()
            }
        }
    }

    private fun validateInputs(): Boolean {
        val name = nameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString()
        val confirmPassword = confirmPasswordEditText.text.toString()

        // Validar nombre
        if (name.isEmpty()) {
            nameEditText.error = "El nombre es requerido"
            nameEditText.requestFocus()
            return false
        }

        // Validar email
        if (email.isEmpty()) {
            emailEditText.error = "El email es requerido"
            emailEditText.requestFocus()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.error = "Por favor ingresa un email válido"
            emailEditText.requestFocus()
            return false
        }

        // Validar contraseña
        if (password.isEmpty()) {
            passwordEditText.error = "La contraseña es requerida"
            passwordEditText.requestFocus()
            return false
        }

        if (password.length < 6) {
            passwordEditText.error = "La contraseña debe tener al menos 6 caracteres"
            passwordEditText.requestFocus()
            return false
        }

        // Validar confirmación de contraseña
        if (confirmPassword.isEmpty()) {
            confirmPasswordEditText.error = "Confirma tu contraseña"
            confirmPasswordEditText.requestFocus()
            return false
        }

        if (password != confirmPassword) {
            confirmPasswordEditText.error = "Las contraseñas no coinciden"
            confirmPasswordEditText.requestFocus()
            return false
        }

        return true
    }

    private fun registerUser() {
        // Mostrar progress bar
        progressBar.visibility = View.VISIBLE
        registerButton.isEnabled = false

        val name = nameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString()

        val registerRequest = RegisterRequest(
            nombre = name,
            email = email,
            password = password
        )

        try {
            ApiClient.registerService.registerUser(registerRequest).enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    // Ocultar progress bar
                    progressBar.visibility = View.GONE
                    registerButton.isEnabled = true

                    if (response.isSuccessful) {
                        val responseBody = response.body() ?: ""
                        Log.d(TAG, "Register response: $responseBody")

                        // Si la respuesta contiene palabras clave de éxito
                        if (responseBody.contains("success", ignoreCase = true) ||
                            responseBody.contains("exitoso", ignoreCase = true) ||
                            responseBody.contains("registrado", ignoreCase = true)) {

                            // Registro exitoso
                            Toast.makeText(
                                this@RegisterActivity,
                                "Registro exitoso. Ya puedes iniciar sesión.",
                                Toast.LENGTH_LONG
                            ).show()

                            // Redirigir a LoginActivity
                            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            // Mostrar la respuesta tal como viene del servidor
                            Toast.makeText(
                                this@RegisterActivity,
                                "Respuesta: $responseBody",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        // Error de registro
                        try {
                            val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                            Log.e(TAG, "Error en registro: $errorBody")
                            Toast.makeText(
                                this@RegisterActivity,
                                "Error en el registro: $errorBody",
                                Toast.LENGTH_SHORT
                            ).show()
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing error response", e)
                            Toast.makeText(
                                this@RegisterActivity,
                                "Error en el registro: ${response.code()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    // Ocultar progress bar
                    progressBar.visibility = View.GONE
                    registerButton.isEnabled = true

                    Log.e(TAG, "Network error", t)

                    Toast.makeText(
                        this@RegisterActivity,
                        "Error de conexión: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } catch (e: Exception) {
            // Capturar cualquier excepción que pueda ocurrir al crear/ejecutar la llamada
            progressBar.visibility = View.GONE
            registerButton.isEnabled = true
            Log.e(TAG, "Exception during registration", e)
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
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