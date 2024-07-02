package eramo.tahoon.data.remote.dto


import com.google.gson.annotations.SerializedName

data class NotificationResponse(
    @SerializedName("status")
    val status: Int?,
    @SerializedName("data")
    val `data`: List<Data?>?,
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
//        @SerializedName("admin_notification_id")
//        val adminNotificationId: Int?,
        @SerializedName("created_at")
        val createdAt: String?,
        @SerializedName("image")
        val image: String?
    )
}