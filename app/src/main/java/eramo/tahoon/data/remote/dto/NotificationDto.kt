package eramo.tahoon.data.remote.dto

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class NotificationDto(
    @SerializedName("title") var title: String? = null,
    @SerializedName("body") var body: String? = null,
    @SerializedName("image") var image: String? = null,
    @SerializedName("link") var link: String? = null,
    @SerializedName("time") var time: String? = null,
    @SerializedName("orderId") var orderId: String?=null
) : Parcelable