package eramo.resultgate.data.remote.dto.drawer


import com.google.gson.annotations.SerializedName

class ContactUsResponse : ArrayList<ContactUsResponse.ContactUsResponseItem>(){
    data class ContactUsResponseItem(
        @SerializedName("address")
        val address: String?,
        @SerializedName("created_at")
        val createdAt: String?,
        @SerializedName("email")
        val email: String?,
        @SerializedName("facebook")
        val facebook: String?,
        @SerializedName("id")
        val id: Int?,
        @SerializedName("instegram")
        val instegram: String?,
        @SerializedName("linkedIn")
        val linkedIn: String?,
        @SerializedName("phone1")
        val phone1: String?,
        @SerializedName("phone2")
        val phone2: String?,
        @SerializedName("twitter")
        val twitter: String?,
        @SerializedName("updated_at")
        val updatedAt: String?
    )
}