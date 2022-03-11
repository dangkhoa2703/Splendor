package entity

/**
 *  class for a turn object
 *  @property gems: tbd
 *  @property card: tbd
 *  */
class Turn(
    val gems: Map<GemType,Int>,
    val card: List<DevCard>
)