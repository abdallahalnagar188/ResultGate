package eramo.tahoon.domain.model.drawer.myaccount

data class RequestModel(
    var requestId: String,
    var requestTypeIdFk: String,
    var installationIdFk: String,
    var orderFrom: String,
    var numAcs: String,
    var requestDate: String,
    var requestTime: String,
    var requestDateS: String,
    var customerId: String,
    var customerName: String,
    var customerMob: String,
    var customerEmail: String,
    var customerAddress: String,
    var customAddress: String,
    var status: String,
    var created: String,
    var updated: String,
    var actionRepliedDate: String,
    var actionRepliedTime: String,
    var actionRepliedUser: String,
    var actionCancelledDate: String,
    var actionCancelledTime: String,
    var actionCancelledUser: String,
    var actionCancelledFrom: String,
    var message: String,
    var notes: String,
    var notesUserId: String,
    var notesDateAr: String,
    var notesTime: String,
    var reply: String,
    var replyDateAr: String,
    var replyTime: String,
    var replyUserId: String,
    var installationName: String,
    var requestTypeName: String
)