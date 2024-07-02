package eramo.resultgate.data.remote.dto.auth


import com.google.gson.annotations.SerializedName

data class SendVerifyMailResponse(
    @SerializedName("status")
    val status: Int?,
    @SerializedName("message")
    val message: String?
)