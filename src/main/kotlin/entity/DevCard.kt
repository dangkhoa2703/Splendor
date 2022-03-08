package entity

/**
 *  class for the development cards
 *  @param PrestigePoints: points that the player receives when buying the card
 *  @param price: cost of the card (in gems)
 *  @param bonus: additional permanent gem that the player receives when buying the card
 *  @param level: specification of the stack to which the card belongs
 * */
class DevCard (
    val PrestigePoints: Int,
    val price: Map<GemType,Int> = mapOf(),
    val bonus: GemType = GemType.YELLOW,
    val level: Int = 0
)
