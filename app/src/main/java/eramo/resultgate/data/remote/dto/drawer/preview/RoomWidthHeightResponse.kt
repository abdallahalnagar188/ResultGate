package eramo.resultgate.data.remote.dto.drawer.preview

import com.google.gson.annotations.SerializedName

data class RoomWidthHeightResponse(
    @SerializedName("width_heights_imgs") var widthHeightsImgs: ArrayList<WidthHeightsImgs> = arrayListOf()
)

data class WidthHeightsImgs(
    @SerializedName("id") var id: String? = null,
    @SerializedName("img") var img: String? = null,
    @SerializedName("img_url") var imgUrl: String? = null
)