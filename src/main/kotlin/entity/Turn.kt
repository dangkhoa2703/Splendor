package entity

/**
 *  Class for a turn object
 *  @property gems: gems required for the turn
 *  @property card: card required for the turn
 *  @property turnType: see enum class TurnType
 */
class Turn (
    val gems: Map<GemType, Int>,
    val card: List<DevCard>,
    val turnType: TurnType,
    val takeThreeDifferentGems: Boolean = false) {

    /** Variable to temporarily save calculated score for turn */
    var evaluation: Double = -1.0

    /** Equals implementation */
    override fun equals(other: Any?): Boolean {
        if (other !is Turn)
            return false
        val otherTurn: Turn = other
        return otherTurn.gems.equals(gems) && otherTurn.card.equals(card)
                && otherTurn.turnType.equals(turnType)
                && otherTurn.takeThreeDifferentGems.equals(takeThreeDifferentGems)
    }

}