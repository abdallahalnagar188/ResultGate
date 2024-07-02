package eramo.tahoon.data.remote.dto.drawer


import com.google.gson.annotations.SerializedName

data class ContactUsSendMessageResponse(
    @SerializedName("msg")
    val msg: String?
)