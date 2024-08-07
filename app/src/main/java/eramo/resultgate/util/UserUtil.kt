package eramo.resultgate.util

import android.content.Context
import android.content.SharedPreferences
import java.util.regex.Pattern

object UserUtil {

    private lateinit var sharedPreferences: SharedPreferences
    private const val IS_FIRST_TIME = "isFirsTime"
    private const val REMEMBER = "remember"
    private const val USER_ID = "user_id"
    private const val USER_TOKEN = "token"
    private const val USERNAME = "username"
    private const val FIRST_NAME = "firstname"
    private const val LAST_NAME = "lastname"
    private const val USER_PASS = "userPass"
    private const val ADDRESS = "address"
    private const val LANGUAGE = "language"

    private const val BIRTH_DATE = "birthDate"
    private const val GENDER = "gender"
    private const val STATUS = "status"

    private const val COUNTRY_ID = "countryId"
    private const val COUNTRY_TITLE = "countryTitle"
    private const val CITY_ID = "cityId"
    private const val CITY_TITLE = "cityTitle"
    private const val REGION_ID = "regionId"
    private const val REGION_TITLE = "regionTitle"

    private const val CURRENCY = "currency"
    private const val BRAND_ID = "brandId"
    private const val CITY_FILTRATION_ID = "city_filtration"
    private const val CITY_FILTRATION_TITLE_EN = "city_filtration_en"
    private const val CITY_FILTRATION_TITLE_AR = "city_filtration_ar"


    private const val CountryName = "country_en_name"
    private const val CityName = "city_en_name"
    private const val JOB = "job"
    private const val JOB_LOCATION = "job_location"
    private const val VENDOR_TYPE = "vendor_type"
    private const val ACADEMIC_DEGREE = "academic_degree"
    private const val RESEARCH_INTERESTS = "research_interests"

    private const val USER_PHONE = "user_phone"
    private const val USER_EMAIL = "user_email"
    private const val USER_PROFILE_IMAGE = "user_profile"
    private const val HAS_DEEP_LINK = "HAS_DEEP_LINK"
    val PASSWORD_PATTERN: Pattern = Pattern.compile(
        "^" + "(?=.*[0-9])" +         //at least 1 digit
                "(?=.*[a-z])" +         //at least 1 lower case letter
                "(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +  //any letter
                "(?=.*[@#$%^&+=])" +  //at least 1 special character
                "(?=\\S+$)" +  //no white spaces
                ".{6,}" +  //at least 4 characters
                "$"
    )

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences("user_util", Context.MODE_PRIVATE)
    }

    fun saveUserInfo(
        isRemember: Boolean,
        userPass: String,
        userID: String,
        userToken: String,
        username: String,
        firstName: String,
        lastName: String,
        language: String,
        userEmail: String,
        userPhone: String,
        birthDate: String,
        gender: String,
        status: String,
        countryId: String,
        countryTitle: String,
        cityId: String,
        cityTitle: String,
        regionId: String,
        regionTitle: String,

        address: String,
        m_image: String,
//        job: String,
//        jobLocation: String,
//        vendorType: String,
//        academicDegree: String,
//        researchInterests: String
    ) {
        sharedPreferences.edit().putBoolean(REMEMBER, isRemember).apply()

        sharedPreferences.edit().putString(USER_PASS, userPass).apply()

        sharedPreferences.edit().putString(USER_ID, userID).apply()
        sharedPreferences.edit().putString(USER_TOKEN, userToken).apply()

        sharedPreferences.edit().putString(USERNAME, username).apply()
        sharedPreferences.edit().putString(FIRST_NAME, firstName).apply()
        sharedPreferences.edit().putString(LAST_NAME, lastName).apply()
        sharedPreferences.edit().putString(LANGUAGE, language).apply()
        sharedPreferences.edit().putString(USER_EMAIL, userEmail).apply()
        sharedPreferences.edit().putString(USER_PHONE, userPhone).apply()

        sharedPreferences.edit().putString(BIRTH_DATE, birthDate).apply()
        sharedPreferences.edit().putString(GENDER, gender).apply()
        sharedPreferences.edit().putString(STATUS, status).apply()

        sharedPreferences.edit().putString(ADDRESS, address).apply()

        sharedPreferences.edit().putString(COUNTRY_ID, countryId).apply()
        sharedPreferences.edit().putString(COUNTRY_TITLE, countryTitle).apply()
        sharedPreferences.edit().putString(CITY_ID, cityId).apply()
        sharedPreferences.edit().putString(CITY_TITLE, cityTitle).apply()
        sharedPreferences.edit().putString(REGION_ID, regionId).apply()
        sharedPreferences.edit().putString(REGION_TITLE, regionTitle).apply()

        sharedPreferences.edit().putString(USER_PROFILE_IMAGE, m_image).apply()
//        sharedPreferences.edit().putString(JOB, job).apply()
//        sharedPreferences.edit().putString(JOB_LOCATION, jobLocation).apply()
//        sharedPreferences.edit().putString(VENDOR_TYPE, vendorType).apply()
//        sharedPreferences.edit().putString(ACADEMIC_DEGREE, academicDegree).apply()
//        sharedPreferences.edit().putString(RESEARCH_INTERESTS, researchInterests).apply()
    }

    fun saveUserProfile(m_image: String) {
        sharedPreferences.edit().putString(USER_PROFILE_IMAGE, m_image).apply()
    }

    fun saveUserCityFiltrationId(cityId: String) {
        sharedPreferences.edit().putString(CITY_FILTRATION_ID, cityId).apply()
    }

    fun saveUserCityFiltrationTitleEn(cityTitleEn: String) {
        sharedPreferences.edit().putString(CITY_FILTRATION_TITLE_EN, cityTitleEn).apply()
    }

    fun saveUserCityFiltrationTitleAr(cityTitleAr: String) {
        sharedPreferences.edit().putString(CITY_FILTRATION_TITLE_AR, cityTitleAr).apply()
    }

    fun saveBrandId(brandId: String) {
        sharedPreferences.edit().putString(BRAND_ID, brandId).apply()
    }

    fun clearUserInfo() {
        sharedPreferences.edit().putBoolean(REMEMBER, false).apply()

        sharedPreferences.edit().putString(USER_PASS, "").apply()

        sharedPreferences.edit().putString(USER_ID, "").apply()
        sharedPreferences.edit().putString(USER_TOKEN, "").apply()

        sharedPreferences.edit().putString(USERNAME, "").apply()
        sharedPreferences.edit().putString(FIRST_NAME, "").apply()
        sharedPreferences.edit().putString(LAST_NAME, "").apply()
        sharedPreferences.edit().putString(LANGUAGE, "").apply()
        sharedPreferences.edit().putString(USER_EMAIL, "").apply()
        sharedPreferences.edit().putString(USER_PHONE, "").apply()

        sharedPreferences.edit().putString(BIRTH_DATE, "").apply()
        sharedPreferences.edit().putString(GENDER, "").apply()
        sharedPreferences.edit().putString(STATUS, "").apply()

        sharedPreferences.edit().putString(ADDRESS, "").apply()

        sharedPreferences.edit().putString(COUNTRY_ID, "").apply()
        sharedPreferences.edit().putString(COUNTRY_TITLE, "").apply()
        sharedPreferences.edit().putString(CITY_ID, "").apply()
        sharedPreferences.edit().putString(CITY_TITLE, "").apply()
        sharedPreferences.edit().putString(REGION_ID, "").apply()
        sharedPreferences.edit().putString(REGION_TITLE, "").apply()

        sharedPreferences.edit().putString(USER_PROFILE_IMAGE, "").apply()

        sharedPreferences.edit().putString(CITY_FILTRATION_ID, "-1").apply()
        sharedPreferences.edit().putString(CITY_FILTRATION_TITLE_EN, "").apply()
        sharedPreferences.edit().putString(CITY_FILTRATION_TITLE_AR, "").apply()
        sharedPreferences.edit().putString(JOB, "").apply()
        sharedPreferences.edit().putString(JOB_LOCATION, "").apply()
        sharedPreferences.edit().putString(VENDOR_TYPE, "").apply()
        sharedPreferences.edit().putString(ACADEMIC_DEGREE, "").apply()
        sharedPreferences.edit().putString(RESEARCH_INTERESTS, "").apply()
    }

    fun saveFirstTime() = sharedPreferences.edit().putBoolean(IS_FIRST_TIME, false).apply()

    fun isFirstTime() = sharedPreferences.getBoolean(IS_FIRST_TIME, true)

    fun saveCurrency(currency: String) {
        sharedPreferences.edit().putString(CURRENCY, currency).apply()
    }

    fun getCurrency(): String {
        return sharedPreferences.getString(CURRENCY, Constants.CURRENCY_EGP)!!
    }

    fun getBrandId(): String {
        return sharedPreferences.getString(BRAND_ID, "4")!!
    }

    fun getCityFiltrationId(): String {
        return sharedPreferences.getString(CITY_FILTRATION_ID, "-1")!!
    }

    fun getCityFiltrationTitleEn(): String {
        return sharedPreferences.getString(CITY_FILTRATION_TITLE_EN, "")!!
    }

    fun getCityFiltrationTitleAr(): String {
        return sharedPreferences.getString(CITY_FILTRATION_TITLE_AR, "")!!
    }

    fun isUserLogin() = getUserId().isNotEmpty()

    fun isRememberUser() = sharedPreferences.getBoolean(REMEMBER, false)

    fun getUserId() = sharedPreferences.getString(USER_ID, "") ?: ""

    fun getUserToken() = sharedPreferences.getString(USER_TOKEN, "") ?: ""

    fun getUserName() = sharedPreferences.getString(USERNAME, "") ?: ""
    fun getUserFirstName() = sharedPreferences.getString(FIRST_NAME, "") ?: ""
    fun getUserLastName() = sharedPreferences.getString(LAST_NAME, "") ?: ""

    fun getUserPass() = sharedPreferences.getString(USER_PASS, "") ?: ""

    fun getUserAddress() = sharedPreferences.getString(ADDRESS, "") ?: ""

    fun getCountryId() = sharedPreferences.getString(COUNTRY_ID, "") ?: ""
    fun getCountryTitle() = sharedPreferences.getString(COUNTRY_TITLE, "") ?: ""

    fun getCityId() = sharedPreferences.getString(CITY_ID, "") ?: ""
    fun getCityTitle() = sharedPreferences.getString(CITY_TITLE, "") ?: ""

    fun getCityName() = sharedPreferences.getString(CityName, "") ?: ""

    fun getCountryName() = sharedPreferences.getString(CountryName, "") ?: ""

    fun getRegionId() = sharedPreferences.getString(REGION_ID, "") ?: ""
    fun getRegionTitle() = sharedPreferences.getString(REGION_TITLE, "") ?: ""

    fun getUserPhone() = sharedPreferences.getString(USER_PHONE, "") ?: ""

    fun getUserEmail() = sharedPreferences.getString(USER_EMAIL, "") ?: ""

    fun getUserProfileImageUrl() = sharedPreferences.getString(USER_PROFILE_IMAGE, "") ?: ""

    fun setHasDeepLink(hasDeepLink: Boolean) = sharedPreferences.edit().putBoolean(HAS_DEEP_LINK, hasDeepLink).apply()

    fun hasDeepLink() = sharedPreferences.getBoolean(HAS_DEEP_LINK, false)
    fun getJob() = sharedPreferences.getString(JOB, "") ?: ""
    fun getJobLocation() = sharedPreferences.getString(JOB_LOCATION, "") ?: ""

    fun getVendorType() = sharedPreferences.getString(VENDOR_TYPE, "") ?: ""
    fun getAcademicDegree() = sharedPreferences.getString(ACADEMIC_DEGREE, "") ?: ""
    fun getResearchInterests() = sharedPreferences.getString(RESEARCH_INTERESTS, "") ?: ""

}