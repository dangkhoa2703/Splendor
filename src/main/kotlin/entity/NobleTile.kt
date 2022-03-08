package entity

/**
 *  class for nobleTiles who can visit the players
 *  @param prestigePoints: points that the player receives from being visited by nobles
 *  @param condition: required bonuses to be visited
 * */
class NobleTile (
    val id: Int,
    val condition: Map<GemType,Int>,
    val prestigePoints: Int

)