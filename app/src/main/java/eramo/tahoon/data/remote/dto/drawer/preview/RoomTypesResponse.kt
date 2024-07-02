package eramo.tahoon.data.remote.dto.drawer.preview

import com.google.gson.annotations.SerializedName
import eramo.tahoon.domain.model.drawer.preview.RoomTypesModel
import eramo.tahoon.util.LocalHelperUtil

data class RoomTypesResponse(
    @SerializedName("room_types") var roomTypeDtos: ArrayList<RoomTypesDto> = arrayListOf()
)

data class RoomTypesDto(
    @SerializedName("room_id") var roomId: String? = null,
    @SerializedName("room_en_name") var roomEnName: String? = null,
    @SerializedName("room_ar_name") var roomArName: String? = null
) {
    fun toRoomTypesModel(): RoomTypesModel {
        return RoomTypesModel(
            roomId = roomId ?: "",
            roomName = if (LocalHelperUtil.isEnglish()) roomEnName ?: "" else roomArName ?: ""
        )
    }
}