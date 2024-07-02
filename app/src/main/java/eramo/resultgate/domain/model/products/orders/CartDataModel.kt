package eramo.resultgate.domain.model.products.orders

import android.os.Parcelable
import eramo.resultgate.data.remote.dto.products.common.AllImagesDto
import eramo.resultgate.util.state.UiText
import kotlinx.android.parcel.RawValue
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartDataModel(
    var id: String?,
    var productIdFk: String?,
    var mainCat: String?,
    var productQty: String?,
    var productPrice: String?,
    var userId: String?,
    var dateAr: String?,
    var dateS: String?,
    var manufacturerName: String?,
    var modelName: String?,
    var powerName: String?,
    var productSize: String?,
    var productColor: String?,
    var colorName: String? = null,
    var hexCode: String? = null,
    var sizeName: String? = null,
    var inStock: @RawValue UiText,
    var availableAmount: String?,
    var statusText: String?,
    var shippingPrice: String?,
    var productName: String?,
    var modelNumber: String?,
    var installation_cost: String?,
    var subCatName: String?,
    var allImageDtos: List<AllImagesDto>?,
    var quantityPrice: String = ""
) : Parcelable