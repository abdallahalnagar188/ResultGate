package eramo.tahoon.data.remote.dto.general

import com.google.gson.annotations.SerializedName
import eramo.tahoon.domain.model.ResultModel
import eramo.tahoon.util.LocalHelperUtil

data class ResultDto(
    @SerializedName("success") var success: Int? = null,
    @SerializedName("message_en") var messageEn: String? = null,
    @SerializedName("message_ar") var messageAr: String? = null
) {
    fun toResultModel(): ResultModel {
        return ResultModel(
            success = success ?: 0,
            message = if (LocalHelperUtil.isEnglish()) messageEn ?: "" else messageAr ?: ""
        )
    }
}