package eramo.tahoon.data.remote.dto.drawer.preview

import com.google.gson.annotations.SerializedName
import eramo.tahoon.domain.model.drawer.preview.WindowInternalShapesModel
import eramo.tahoon.util.LocalHelperUtil

data class InternalShapesResponse(
    @SerializedName("window_internal_shapes") var windowInternalShapeDtos: ArrayList<WindowInternalShapesDto> = arrayListOf()
)

data class WindowInternalShapesDto(
    @SerializedName("window_id") var windowId: String? = null,
    @SerializedName("window_en_name") var windowEnName: String? = null,
    @SerializedName("window_ar_name") var windowArName: String? = null,
    @SerializedName("image") var image: String? = null,
    @SerializedName("img_url") var imgUrl: String? = null
) {
    fun toWindowInternalShapesModel(): WindowInternalShapesModel {
        return WindowInternalShapesModel(
            windowId = windowId ?: "",
            windowName =
            if (LocalHelperUtil.isEnglish()) windowEnName ?: "" else windowArName ?: "",
            image = image ?: "",
            imgUrl = imgUrl ?: ""
        )
    }
}