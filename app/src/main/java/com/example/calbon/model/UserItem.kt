import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserItem(
    val nome_completo: String,
    val email: String,
    val num_cracha: String,
    val localizacao: String,
    val codigo_empresa: String
) : Parcelable
