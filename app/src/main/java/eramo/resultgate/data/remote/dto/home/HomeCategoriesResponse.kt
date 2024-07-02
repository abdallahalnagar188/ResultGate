package eramo.resultgate.data.remote.dto.home


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class HomeCategoriesResponse(
    @SerializedName("status")
    val status: Int?,
    @SerializedName("data")
    val `data`: List<Data?>?,
    @SerializedName("max")
    val max: Double?
) : Parcelable {

    @Parcelize
    data class Data(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("title")
        val title: String?,
        @SerializedName("image")
        val image: String?,
        @SerializedName("summary")
        val summary: String?,
        @SerializedName("description")
        val description: String?,
        @SerializedName("products_count")
        val productsCount: Int?,
        @SerializedName("primary_image_url")
        val imageUrl: String?,
        @SerializedName("type")
        val type: String?,
        @SerializedName("sub_catagories")
        val subCatagories: List<SubCatagory?>?
    ) : Parcelable {

        @Parcelize
        data class SubCatagory(
            @SerializedName("id")
            val id: Int?,
            @SerializedName("parent_id")
            val parentId: Int?,
            @SerializedName("title")
            val title: String?,
            @SerializedName("primary_image_url")
            val imageUrl: String?,
            @SerializedName("type")
            val type: String?
        ) : Parcelable {
            fun toSubCategoryModel(): SubCategoryModel {
                return SubCategoryModel(
                    id ?: -1, parentId ?: -1, title ?: "", imageUrl ?: "", type ?: ""
                )
            }
        }
    }
}