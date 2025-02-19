package eramo.resultgate.domain.model.auth

import eramo.resultgate.data.remote.dto.auth.LoginResponse

data class LoginModel(
    var member: LoginResponse.Member? = null,
    var token: String? = null
)

//data class LoginModel(
//    var member: Member? = null,
//    var token: String? = null,
//    var success: Int? = null,
//    var message: String? = null
//)