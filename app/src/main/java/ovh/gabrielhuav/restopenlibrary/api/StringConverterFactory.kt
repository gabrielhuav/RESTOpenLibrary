package ovh.gabrielhuav.restopenlibrary.api

import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * Un conversor personalizado para manejar respuestas de texto plano.
 * Esto es útil cuando el servidor devuelve texto en lugar de JSON.
 */
class StringConverterFactory : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        return if (String::class.java == type) {
            Converter<ResponseBody, String> { it.string() }
        } else {
            null
        }
    }

    companion object {
        fun create(): StringConverterFactory = StringConverterFactory()
    }
}