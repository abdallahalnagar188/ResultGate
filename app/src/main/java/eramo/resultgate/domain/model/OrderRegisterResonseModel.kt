package eramo.resultgate.domain.model.checkout

data class OrderRegisterResonseModel(
    val amount_cents: Int,
    val api_source: String,
    val collector: Any,
    val commission_fees: Int,
    val created_at: String,
    val currency: String,
    val `data`: Data,
    val delivery_fees_cents: Int,
    val delivery_needed: Boolean,
    val delivery_vat_cents: Int,
    val id: Int,
    val is_cancel: Boolean,
    val is_canceled: Boolean,
    val is_payment_locked: Boolean,
    val is_return: Boolean,
    val is_returned: Boolean,
    val items: List<Any>,
    val merchant: Merchant,
    val merchant_order_id: Any,
    val merchant_staff_tag: Any,
    val notify_user_with_email: Boolean,
    val order_url: String,
    val paid_amount_cents: Int,
    val payment_method: String,
    val shipping_data: Any,
    val token: String,
    val url: String,
    val wallet_notification: Any
)