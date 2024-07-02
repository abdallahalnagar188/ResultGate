package eramo.tahoon.data.remote.dto.cart


import com.google.gson.annotations.SerializedName

data class RemoveCartItemResponse(
    @SerializedName("status")
    val status: Int?,
    @SerializedName("message")
    val message: String?
)