package eramo.tahoon.data.remote.dto.auth


import com.google.gson.annotations.SerializedName

data class UpdatePasswordResponse(
    @SerializedName("status")
    val status: Int?,
    @SerializedName("message")
    val message: String?
)