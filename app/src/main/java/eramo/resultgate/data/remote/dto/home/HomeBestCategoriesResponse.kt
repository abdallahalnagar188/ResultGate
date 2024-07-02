package eramo.resultgate.data.remote.dto.home


import com.google.gson.annotations.SerializedName

data class HomeBestCategoriesResponse(
    @SerializedName("data")
    val `data`: List<Data?>?,
    @SerializedName("status")
    val status: Int?
) {
    data class Data(
        @SerializedName("description")
        val description: String?,
        @SerializedName("id")
        val id: Int?,
        @SerializedName("primary_image_url")
        val primaryImageUrl: String?,
        @SerializedName("products_count")
        val productsCount: Int?,
        @SerializedName("sub_catagories")
        val subCatagories: List<SubCatagory?>?,
        @SerializedName("title")
        val title: String?
    ) {
        data class SubCatagory(
            @SerializedName("id")
            val id: Int?,
            @SerializedName("parent_id")
            val parentId: Int?,
            @SerializedName("primary_image_url")
            val primaryImageUrl: String?,
            @SerializedName("title")
            val title: String?
        ){
            fun toSubCategoryModel(): SubCategoryModel {
                return SubCategoryModel(
                    id ?: parentId?:-1, -1  -1, title ?: "", primaryImageUrl ?: "",  ""
                )
            }
        }
    }
}