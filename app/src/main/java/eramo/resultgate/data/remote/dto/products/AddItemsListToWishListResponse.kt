package eramo.resultgate.data.remote.dto.products


import com.google.gson.annotations.SerializedName

data class AddItemsListToWishListResponse(
    @SerializedName("status")
    val status: Int?,
    @SerializedName("message")
    val message: String?
)