package entity

class DevCard(
    val PrestigePoints: Int,
    val price: Map<GemType,Int> = mapOf(),
    val bonus: GemType = GemType.YELLOW,
    val level: Int = 0)
