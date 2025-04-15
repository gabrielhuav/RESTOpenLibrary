package ovh.gabrielhuav.restopenlibrary.models

import java.time.LocalDateTime

data class Favorite(
    val id: Long,
    val libroId: String,
    val titulo: String,
    val autor: String,
    val imagenUrl: String,
    val fechaAgregado: String // Usaremos String para simplificar la deserialización
)