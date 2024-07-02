package eramo.tahoon.domain.model.auth

data class RegionsModel(
    val id : Int,
    val title : String,
    val countryId : Int,
    val cityId : Int,
)