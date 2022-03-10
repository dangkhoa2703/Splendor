package entity

/**
 *  class for the development cards of the game Splendor
 *  @param id: unique identifier of one card
 *  @param price: costs in gems
 *  @property level: level one = 1, level two = 2, level three = 3
 *  @property prestigePoints:  points that the player receives from buying this card
 *  @property bonus: permanent bonus gem the player receives after buying this card
 */
class DevCard(
    val id: Int,
    val price: Map<GemType,Int>,
    val level: Int = 0,
    val prestigePoints: Int,
    val bonus: GemType )
