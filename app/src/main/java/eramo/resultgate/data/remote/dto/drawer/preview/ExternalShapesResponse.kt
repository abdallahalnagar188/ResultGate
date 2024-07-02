package eramo.resultgate.data.remote.dto.drawer.preview

import com.google.gson.annotations.SerializedName

data class ExternalShapesResponse(
    @SerializedName("window_external_shapes") var windowExternalShapes: ArrayList<WindowExternalShapes> = arrayListOf()
)

data class WindowExternalShapes(
    @SerializedName("id") var id: String? = null,
    @SerializedName("image") var image: String? = null,
    @SerializedName("img_url") var imgUrl: String? = null
)