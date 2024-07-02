package eramo.tahoon.data.remote.dto.drawer

import com.google.gson.annotations.SerializedName
import eramo.tahoon.domain.model.drawer.PolicyInfoModel

data class PolicyInfoDto(
    @SerializedName("term_and_condition_") var term_and_condition_: String? = null,
) {
    fun toPolicyInfoModel(): PolicyInfoModel {
        return PolicyInfoModel(
            idConfig =  "",
            policy = term_and_condition_ ?: ""
        )
    }
}


// Old one
//data class PolicyInfoResponse(
//    @SerializedName("policy_info") var policyInfoDto: ArrayList<PolicyInfoDto> = arrayListOf()
//)
//
//data class PolicyInfoDto(
//    @SerializedName("id_config") var idConfig: String? = null,
//    @SerializedName("policy_en") var policyEn: String? = null,
//    @SerializedName("policy_ar") var policyAr: String? = null
//) {
//    fun toPolicyInfoModel(): PolicyInfoModel {
//        return PolicyInfoModel(
//            idConfig = idConfig ?: "",
//            policy = if (LocalHelperUtil.isEnglish()) policyEn ?: "" else policyAr ?: ""
//        )
//    }
//}