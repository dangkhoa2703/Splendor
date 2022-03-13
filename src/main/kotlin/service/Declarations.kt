package service

import entity.*

/**
 * Help-function that calculates the amount of missing gems from the gems of the player and the given map of gems
 */
fun calculateMissingGems(player: Player, costs: Map<GemType,Int>): MutableMap<GemType, Int> {
    val result = mutableMapOf<GemType, Int>()
    player.gems.forEach {
        val costsForIndividualGemType: Int = costs[it.key] ?: 0
        val difference = it.value - costsForIndividualGemType
        if(difference < 0)
            result[it.key] = -1 * difference
    }
    return result
}