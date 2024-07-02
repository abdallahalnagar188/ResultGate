package eramo.tahoon.domain.model.checkout

import eramo.tahoon.domain.model.BillingData

data class CardPaymentKeyBodySendModel(
    val amount_cents: String,
    val auth_token: String,
    val billing_data: BillingData,
    val currency: String,
    val expiration: Int,
    val integration_id: Int,
    val lock_order_when_paid: String,
    val order_id: String
)