
import retrofit2.http.GET

interface Usuario {
        @GET("joycenick/ApiTestNoticias/main/usuario.json")
        suspend fun getUsers(): List<UserItem>
}
