package eramo.resultgate.data.remote.dto.general

import com.google.gson.annotations.SerializedName
import eramo.resultgate.domain.model.ResultModel

data class MyResultDto(
    @SerializedName("msg") var msg: String? = null,
) {
    fun toResultModel(): ResultModel {
        return ResultModel(
            success =
            if ("please check your email" in msg!!) {
                1
            } else {
                0
            },
            message = msg ?: ""
        )
    }
}