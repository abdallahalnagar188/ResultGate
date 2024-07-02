package eramo.tahoon.data.remote.dto.auth

import com.google.gson.annotations.SerializedName
import eramo.tahoon.data.remote.dto.general.Member

data class LoginDto(
    @SerializedName("member") var member: Member? = null,
    @SerializedName("token") var token: String? = null,
    @SerializedName("success") var success: Int? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("errors") var errors: String? = null
) {
//    fun toLoginModel(): LoginModel {
//        return LoginModel(member, token, success, message)
//    }
}