package eramo.tahoon.data.remote.dto


import com.google.gson.annotations.SerializedName

data class NotificationDetailsResponse(
    @SerializedName("status")
    val status: Int?,
    @SerializedName("data")
    val `data`: Data?,
    @SerializedName("message")
    val message: String?
) {
    data class Data(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("order_id")
        val orderId: String?,
        @SerializedName("title")
        val title: String?,
        @SerializedName("body")
        val body: String?,
        @SerializedName("type")
        val type: String?,
        @SerializedName("seen")
        val seen: Int?,
        @SerializedName("updated_at")
        val updatedAt: String?,
        @SerializedName("image")
        val image: String?
    )
}