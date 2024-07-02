package eramo.tahoon.data.remote.dto.drawer.preview

import com.google.gson.annotations.SerializedName
import eramo.tahoon.domain.model.drawer.preview.AcTypesModel
import eramo.tahoon.util.LocalHelperUtil

data class AcTypesResponse(
    @SerializedName("ac_types") var acTypeDtos: ArrayList<AcTypesDto> = arrayListOf()
)

data class AcTypesDto(
    @SerializedName("type_id") var typeId: String? = null,
    @SerializedName("title_ar") var titleAr: String? = null,
    @SerializedName("title_en") var titleEn: String? = null,
    @SerializedName("from_id") var fromId: String? = null,
    @SerializedName("desc_ar") var descAr: String? = null,
    @SerializedName("desc_en") var descEn: String? = null
) {
    fun toAcTypesModel(): AcTypesModel {
        return AcTypesModel(
            typeId = typeId ?: "",
            title = if (LocalHelperUtil.isEnglish()) titleEn ?: "" else titleAr ?: "",
            fromId = fromId ?: "",
            desc = if (LocalHelperUtil.isEnglish()) descEn ?: "" else descAr ?: "",
        )
    }
}