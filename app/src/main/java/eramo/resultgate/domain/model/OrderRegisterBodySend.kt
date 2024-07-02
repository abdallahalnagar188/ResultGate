package eramo.resultgate.domain.model.checkout

data class OrderRegisterBodySend(
    val amount_cents: String,
    val auth_token: String,
    val currency: String,
    val delivery_needed: String
)