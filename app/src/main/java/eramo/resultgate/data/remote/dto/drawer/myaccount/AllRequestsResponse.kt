package eramo.resultgate.data.remote.dto.drawer.myaccount

import com.google.gson.annotations.SerializedName
import eramo.resultgate.domain.model.drawer.myaccount.RequestModel
import eramo.resultgate.util.LocalHelperUtil

data class AllRequestsResponse(
    @SerializedName("all_requests") var allRequestDtos: ArrayList<AllRequestsDto> = arrayListOf()
)

data class AllRequestsDto(
    @SerializedName("request_id") var requestId: String? = null,
    @SerializedName("request_type_id_fk") var requestTypeIdFk: String? = null,
    @SerializedName("installation_id_fk") var installationIdFk: String? = null,
    @SerializedName("order_from") var orderFrom: String? = null,
    @SerializedName("num_acs") var numAcs: String? = null,
    @SerializedName("request_date") var requestDate: String? = null,
    @SerializedName("request_date_ar") var requestDateAr: String? = null,
    @SerializedName("request_time") var requestTime: String? = null,
    @SerializedName("request_date_s") var requestDateS: String? = null,
    @SerializedName("customer_id") var customerId: String? = null,
    @SerializedName("customer_name") var customerName: String? = null,
    @SerializedName("customer_mob") var customerMob: String? = null,
    @SerializedName("customer_email") var customerEmail: String? = null,
    @SerializedName("customer_address") var customerAddress: String? = null,
    @SerializedName("custom_address") var customAddress: String? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("created") var created: String? = null,
    @SerializedName("updated") var updated: String? = null,
    @SerializedName("action_replied_date") var actionRepliedDate: String? = null,
    @SerializedName("action_replied_time") var actionRepliedTime: String? = null,
    @SerializedName("action_replied_user") var actionRepliedUser: String? = null,
    @SerializedName("action_cancelled_date") var actionCancelledDate: String? = null,
    @SerializedName("action_cancelled_time") var actionCancelledTime: String? = null,
    @SerializedName("action_cancelled_user") var actionCancelledUser: String? = null,
    @SerializedName("action_cancelled_from") var actionCancelledFrom: String? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("notes") var notes: String? = null,
    @SerializedName("notes_user_id") var notesUserId: String? = null,
    @SerializedName("notes_date_ar") var notesDateAr: String? = null,
    @SerializedName("notes_time") var notesTime: String? = null,
    @SerializedName("reply") var reply: String? = null,
    @SerializedName("reply_date_ar") var replyDateAr: String? = null,
    @SerializedName("reply_time") var replyTime: String? = null,
    @SerializedName("reply_user_id") var replyUserId: String? = null,
    @SerializedName("installation_en_name") var installationEnName: String? = null,
    @SerializedName("installation_ar_name") var installationArName: String? = null,
    @SerializedName("request_type_ar_name") var requestTypeArName: String? = null,
    @SerializedName("request_type_en_name") var requestTypeEnName: String? = null
) {
    fun toRequestModel(): RequestModel {
        return RequestModel(
            requestId = requestId ?: "",
            requestTypeIdFk = requestTypeIdFk ?: "",
            installationIdFk = installationIdFk ?: "",
            orderFrom = orderFrom ?: "",
            numAcs = numAcs ?: "",
            requestDate =
            if (LocalHelperUtil.isEnglish()) requestDate ?: "" else requestDateAr ?: "",
            requestTime = requestTime ?: "",
            requestDateS = requestDateS ?: "",
            customerId = customerId ?: "",
            customerName = customerName ?: "",
            customerMob = customerMob ?: "",
            customerEmail = customerEmail ?: "",
            customerAddress = customerAddress ?: "",
            customAddress = customAddress ?: "",
            status = status ?: "",
            created = created ?: "",
            updated = updated ?: "",
            actionRepliedDate = actionRepliedDate ?: "",
            actionRepliedTime = actionRepliedTime ?: "",
            actionRepliedUser = actionRepliedUser ?: "",
            actionCancelledDate = actionCancelledDate ?: "",
            actionCancelledTime = actionCancelledTime ?: "",
            actionCancelledUser = actionCancelledUser ?: "",
            actionCancelledFrom = actionCancelledFrom ?: "",
            message = message ?: "",
            notes = notes ?: "",
            notesUserId = notesUserId ?: "",
            notesDateAr = notesDateAr ?: "",
            notesTime = notesTime ?: "",
            reply = reply ?: "",
            replyDateAr = replyDateAr ?: "",
            replyTime = replyTime ?: "",
            replyUserId = replyUserId ?: "",
            installationName =
            if (LocalHelperUtil.isEnglish()) installationEnName ?: "" else installationArName ?: "",
            requestTypeName =
            if (LocalHelperUtil.isEnglish()) requestTypeEnName ?: "" else requestTypeArName ?: ""
        )
    }
}