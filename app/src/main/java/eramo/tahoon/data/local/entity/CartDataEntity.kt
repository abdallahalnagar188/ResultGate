package eramo.tahoon.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import eramo.tahoon.R
import eramo.tahoon.domain.model.products.orders.CartDataModel
import eramo.tahoon.domain.model.request.OrderItemList
import eramo.tahoon.util.LocalHelperUtil
import eramo.tahoon.util.state.UiText

@Entity
data class CartDataEntity(
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    var productIdFk: String? = null,
    var productName: String? = null,
    var mainCat: String? = null,
    var statusText: String? = null,
    var modelNumber: String? = null,
    var productQty: Int? = null,
    var productPrice: String? = null,
    var withInstallation: String? = null,
    var userId: String? = null,
    var dateAr: String? = null,
    var dateS: String? = null,
    var manufacturerArName: String? = null,
    var manufacturerEnName: String? = null,
    var modelArName: String? = null,
    var modelEnName: String? = null,
    var powerArName: String? = null,
    var powerEnName: String? = null,
    var inStock: String? = null,
    var availableAmount: String? = null,
    var status: String? = null,
    var shippingPrice: String? = null,
    var productNameEn: String? = null,
    var productNameAr: String? = null,
    var modelRkm: String? = null,
    var installation_cost: String? = null,
    var mainCatArName: String? = null,
    var mainCatEnName: String? = null,
    var subCatArName: String? = null,
    var subCatEnName: String? = null,
    var allImageEntity: List<AllImagesEntity>? = null,
    var quantityPrice: String? = null,
    var productColorId: String,
    var productSizeId: String,
) {
    fun toCartDataModel(): CartDataModel {
        return CartDataModel(
            id = id.toString(),
            productIdFk = productIdFk ?: "",
            mainCat = mainCat,
            productQty = productQty.toString(),
            productPrice = productPrice ?: "",
            userId = userId ?: "",
            dateAr = dateAr ?: "",
            dateS = dateS ?: "",
            manufacturerName = if (LocalHelperUtil.isEnglish()) manufacturerEnName
                ?: "" else manufacturerArName ?: "",
            modelName = if (LocalHelperUtil.isEnglish()) modelEnName
                ?: "" else modelArName ?: "",
            powerName = if (LocalHelperUtil.isEnglish()) powerEnName
                ?: "" else powerArName ?: "",
            inStock = UiText.StringResource(R.string.in_stock_value, inStock as String),
            productColor = productColorId,
            productSize = productSizeId,
            availableAmount = availableAmount,
            statusText = status,
            shippingPrice = shippingPrice,
            productName = productName,
            modelNumber = modelNumber,
            installation_cost = installation_cost,
            subCatName = if (LocalHelperUtil.isEnglish())
                subCatEnName else subCatArName,
            allImageDtos = allImageEntity?.map { it.toAllImagesDto() } ?: emptyList(),
        )
    }

    fun toOrderItem(): OrderItemList {
        return OrderItemList(
            productId = productIdFk?.toInt(),
            productQty = productQty,
            productPrice = productPrice?.toFloat(),
            productSize = productSizeId,
            productColor = productColorId,
        )
    }
}