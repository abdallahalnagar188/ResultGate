package eramo.tahoon.data.remote.dto.products


import com.google.gson.annotations.SerializedName

data class FavouriteResponse(
    @SerializedName("status")
    val status: Int?,
    @SerializedName("message")
    val message: String?
)