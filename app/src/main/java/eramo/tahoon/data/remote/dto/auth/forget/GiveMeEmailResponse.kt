package eramo.tahoon.data.remote.dto.auth.forget


import com.google.gson.annotations.SerializedName

data class GiveMeEmailResponse(
    @SerializedName("status")
    val status: Int?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("code")
    val code: Int?
)