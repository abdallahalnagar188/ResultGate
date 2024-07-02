package eramo.tahoon.data.remote.dto.drawer


import com.google.gson.annotations.SerializedName

class MyAppInfoResponse : ArrayList<MyAppInfoResponse.MyAppInfoResponseItem>() {
    data class MyAppInfoResponseItem(
        @SerializedName("about_us")
        val aboutUs: String?,
        @SerializedName("video_link")
        val videoLink: String?
    )
}