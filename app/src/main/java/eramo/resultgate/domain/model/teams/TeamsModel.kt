package eramo.resultgate.domain.model.teams

data class TeamsModel (
    var cap: Int,
    var completed: Int,
    var details: String,
    var fakePrice: Int,
    var grams: Int,
    var gramsBought: Int,
    var id: Int,
    var isCompleted: Boolean,
    var isJoined: Boolean,
    var joinedMembers: Int,
    var name: String?,
    var realPrice: Int,
    var remainingGrams: Int,
    var researcher: String,
    var status: String,
    val primaryImage: String

)
