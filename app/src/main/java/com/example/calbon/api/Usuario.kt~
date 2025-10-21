
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface Usuario {
        @GET("joycenick/ApiTestNoticias/main/usuario.json")
        suspend fun getUsers(): List<UserItem>
        // ApiService.kt
        interface ApiService {

                // Endpoint para enviar o e-mail de recuperação
                @POST("api/forgot-password")
                // Enviamos o EmailRequest e esperamos uma Response de sucesso (sem corpo específico)
                suspend fun requestRecoveryEmail(@Body request: EmailRequest): Response<Unit>

        }
}
