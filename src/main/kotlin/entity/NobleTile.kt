package entity

/**
 *  class for nobleTiles who can visit the players
 *  @param prestigePoints: points that the player receives from being visited by nobles
 *  @param condition: required bonuses to be visited
 * */
class NobleTile (
    val prestigePoints: Int,
    val condition: Map<GemType,Int>
)