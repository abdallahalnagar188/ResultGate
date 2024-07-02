package eramo.resultgate.data.remote.dto.general

import com.google.gson.annotations.SerializedName
import eramo.resultgate.domain.model.NewResultModel

data class NewResultDto(
    @SerializedName("msg") var msg: Msg? = null
) {
    fun toResultModel(): NewResultModel {
        return NewResultModel(
            token = msg?.token ?: "",
            name = msg?.name ?: ""
        )
    }
}

data class Msg(
    @SerializedName("token") var token: String? = null,
    @SerializedName("name") var name: String? = null

)

// Old one
//data class NewResultDto(
//    @SerializedName("token") var token: String? = null,
//    @SerializedName("name") var name: String? = null
//) {
//    fun toResultModel(): NewResultModel {
//        return NewResultModel(
//           token = "",
//            name = ""
//        )
//    }
//}