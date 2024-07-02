package eramo.tahoon.domain.model

data class AuthApiResponseModel(
    val profile: Profile,
    val token: String
)