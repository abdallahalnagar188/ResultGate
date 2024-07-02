package eramo.resultgate.data.remote.dto.drawer.preview

import com.google.gson.annotations.SerializedName
import eramo.resultgate.domain.model.drawer.preview.FloorTypesModel
import eramo.resultgate.util.LocalHelperUtil

data class FloorTypesResponse(
    @SerializedName("floor_types") var floorTypeDtos: ArrayList<FloorTypesDto> = arrayListOf()
)

data class FloorTypesDto(
    @SerializedName("floor_id") var floorId: String? = null,
    @SerializedName("floor_en_name") var floorEnName: String? = null,
    @SerializedName("floor_ar_name") var floorArName: String? = null
) {
    fun toFloorTypes(): FloorTypesModel {
        return FloorTypesModel(
            floorId = floorId ?: "",
            floorName = if (LocalHelperUtil.isEnglish()) floorEnName ?: "" else floorArName ?: ""
        )
    }
}