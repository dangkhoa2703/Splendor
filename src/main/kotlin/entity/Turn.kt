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
    var turnType: TurnType,
    private val takeThreeDifferentGems: Boolean = false,
    var gemsToDiscard: Map<GemType, Int> = mapOf()) {

    /** Variable to temporarily save calculated score for turn */
    var evaluation: Double? = null

    /** Equals implementation */
    override fun equals(other: Any?): Boolean {
        if (other !is Turn)
            return false
        val otherTurn: Turn = other
        return otherTurn.gems == gems && otherTurn.card == card
                && otherTurn.turnType == turnType
                && otherTurn.gemsToDiscard == gemsToDiscard
                && otherTurn.takeThreeDifferentGems == takeThreeDifferentGems
    }

}