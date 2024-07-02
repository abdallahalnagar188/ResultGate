package eramo.resultgate.domain.model

data class AuthApiResponseModel(
    val profile: Profile,
    val token: String
)