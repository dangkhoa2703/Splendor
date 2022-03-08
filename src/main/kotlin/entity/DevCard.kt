package entity

class DevCard(
    val id: Int,
    val price: Map<GemType,Int>,
    val level: Int = 0,
    val PrestigePoints: Int,
    val bonus: GemType )
