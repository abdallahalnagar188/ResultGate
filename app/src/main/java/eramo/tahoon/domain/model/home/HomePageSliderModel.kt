package eramo.tahoon.domain.model.home

data class HomePageSliderModel(
    val status: Int?,
    val `data`: List<Data?>?,
    val message: String?
){
    data class Data(
        val image: String?,
        val link: String?,
        val imageUrl: String?
    )
}
