package eramo.resultgate.domain.model.auth

data class SignUpModel(
    val id: Int,
    val userToken :String,

    val name: String,
    val firstName: String,
    val lastName: String,

    val email: String,
    val phone: String,
    val birthDate: String,
    val gender: String,
    val language: String,
    val imageUrl: String,

    val countryId: Int,
    val countryTitle: String,
    val cityId: Int,
    val cityTitle: String,
    val regionId: Int,
    val regionTitle: String,

    val responseMessage:String
)