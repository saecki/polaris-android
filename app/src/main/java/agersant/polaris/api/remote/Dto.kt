package agersant.polaris.api.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Credentials(
    @SerialName("username") val username: String,
    @SerialName("password") val password: String,
)

@Serializable
data class Authorization(
    @SerialName("username") val username: String,
    @SerialName("token") val token: String,
    @SerialName("is_admin") val isAdmin: Boolean,
)
