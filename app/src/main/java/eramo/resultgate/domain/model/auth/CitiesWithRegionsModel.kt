package eramo.resultgate.domain.model.auth

data class CitiesWithRegionsModel(
    val id: Int,
    val title: String,
    val titleEn: String,
    val titleAr: String,
    val countryId: Int,
    val regionsList: List<RegionsInCitiesModel>,
)

data class RegionsInCitiesModel(
    val id: Int,
    val title: String,
    val titleEn: String,
    val titleAr: String
)