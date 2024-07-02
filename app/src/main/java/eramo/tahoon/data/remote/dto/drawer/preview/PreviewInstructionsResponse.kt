package eramo.tahoon.data.remote.dto.drawer.preview

import com.google.gson.annotations.SerializedName
import eramo.tahoon.domain.model.drawer.preview.PreviewInstructionModel
import eramo.tahoon.util.LocalHelperUtil

data class PreviewInstructionsResponse(
    @SerializedName("mo3ayna_instruction") var previewInstructionDto: PreviewInstructionDto? = PreviewInstructionDto()
)

data class PreviewInstructionDto(
    @SerializedName("main_id") var mainId: String? = null,
    @SerializedName("title_en") var titleEn: String? = null,
    @SerializedName("title_ar") var titleAr: String? = null
) {
    fun toPreviewInstructionModel(): PreviewInstructionModel {
        return PreviewInstructionModel(
            mainId = mainId ?: "",
            title = if (LocalHelperUtil.isEnglish()) titleEn ?: "" else titleAr ?: ""
        )
    }
}