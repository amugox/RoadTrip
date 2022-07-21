package co.ke.jamboapps.roadtrip.model

data class ItemStat(
    var title: String = "",
    var status: StatusType = StatusType.SUCCESS
)

enum class StatusType {
    SUCCESS, WARNING, DANGER,
}