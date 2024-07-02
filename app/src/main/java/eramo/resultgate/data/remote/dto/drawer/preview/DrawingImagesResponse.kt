package eramo.resultgate.data.remote.dto.drawer.preview

import com.google.gson.annotations.SerializedName

data class DrawingImagesResponse(
    @SerializedName("measurements_drawings_image") var measurementsDrawingsImage: ArrayList<MeasurementsDrawingsImage> = arrayListOf()
)

data class MeasurementsDrawingsImage(
    @SerializedName("window_id") var windowId: String? = null,
    @SerializedName("image") var image: String? = null,
    @SerializedName("img_url") var imgUrl: String? = null
)