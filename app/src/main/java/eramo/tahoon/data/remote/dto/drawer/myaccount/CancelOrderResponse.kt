package eramo.tahoon.data.remote.dto.drawer.myaccount


import com.google.gson.annotations.SerializedName

data class CancelOrderResponse(
    @SerializedName("status")
    val status: Int?,
    @SerializedName("message")
    val message: String?
)