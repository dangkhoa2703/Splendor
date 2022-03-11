package entity

/**
 *  class for a turn object
 *  @property gems: gems required for the turn
 *  @property card: card required for the turn
 *  @property turnType: see enum class TurnType
 *  */
class Turn (
    val gems: Map<GemType, Int>,
    val card: List<DevCard>,
    val turnType: TurnType)