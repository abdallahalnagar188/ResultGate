package eramo.resultgate.domain.model.products

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import eramo.resultgate.data.remote.dto.products.common.AllImagesDto
import eramo.resultgate.domain.model.products.common.ProductExtrasModel
import eramo.resultgate.domain.model.products.common.ProductFeaturesModel
import eramo.resultgate.util.state.UiText
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class ProductModel(
    @SerializedName("id") var productId: String,
    @SerializedName("title_en") var productName: String,
    @SerializedName("slug_en") var slugTitle: String,
    @SerializedName("primary_image") var primaryimage: String,
    var mainCatId: String,
    var subCatId: String,
    var manufacturerIdFk: String,
    var supplierIdFk: String,
    var mainTypeIdFk: String,
    var subTypeIdFk: String,
    var modelIdFk: String,
    var powerIdFk: String,
    var installationIdFk: String,
    @SerializedName("real_price") var realPrice: String,
    @SerializedName("fake_price") var displayPrice: String,
    var totalTax: String,
    var displayTodate: String,
    var displayTodateS: String,
    @SerializedName("descrioption_en") var description: String,
    var comments: String,
    var updatedAt: String,
    var status: @RawValue UiText,
    var statusText: String = "",
    var featured: String,
    var hotCold: String,
    var hotColdName: String,
    var inStockValue: String,
    var inStockText: @RawValue UiText,
    var availableAmount: String,
    var userId: String,
    var shipping: String,
    var features: String,
    var extras: String,
    var instructionsUse: String,
    var modelNumber: String,
    var sku: String,
    var mainCat: String,
    var subCat: String,
    var mainActypeName: String,
    var subActypeName: String,
    var manufacturerName: String,
    var modelName: String,
    var powerName: String,
    var installationName: String,
    var installationCost: String,
    var coverFromArea: String,
    var coverToArea: String,
    var taxesPrice: Double,
    var allImageDtos: List<AllImagesDto>,
    var isNew: Boolean,
    var discount: Int,
    var productExtraModels: List<ProductExtrasModel>,
    var productFeatureModels: List<ProductFeaturesModel>,
    var isFav: Boolean,
    var extraProducts: List<ProductModel>?=null,
    var productSizes: List<ProductSizesModel>? = null,
    var productColors: List<ProductColorModel>? = null
) : Parcelable

@Parcelize
data class ProductSizesModel(
    var id: String? = null,
    var productIdFk: String? = null,
    var extraIdFk: String? = null,
    var ttype: String? = null,
    var sizeName: String? = null
) : Parcelable

@Parcelize
data class ProductColorModel(
    var id: String? = null,
    var productIdFk: String? = null,
    var extraIdFk: String? = null,
    var ttype: String? = null,
    var colorName: String? = null,
    var hexCode: String? = null
) : Parcelable