package service

import entity.*

/**
 * Help-function that calculates amount of gems needed to buy the card
 */
fun DevCard.calculateGemPrice(): Int {
    var amount: Int = 0
    price.forEach {
        amount += it.value
    }
    return amount
}

/**
 * Clones a list of players
 */
fun List<Player>.clone(): List<Player> {
    var playerList: MutableList<Player> = mutableListOf()
    this.forEach {
        playerList.add(it.clone())
    }
    return playerList
}

/**
 * Combines two maps by adding or subtracting the respective int-values
 */
fun <T> Map<T, Int>.combine(secondMap: Map<T, Int>, subtract: Boolean = false, allowNegativeValues: Boolean = false) : MutableMap<T, Int> {
    val result: MutableMap<T, Int> = toMutableMap()
    secondMap.forEach {
        val oldValue: Int? = get(it.key)
        if(oldValue == null) {
            if (!subtract)  result[it.key] = it.value
            else if(allowNegativeValues) result[it.key] = -it.value
            else result[it.key] = 0
        } else {
            if (!subtract) result[it.key] = oldValue + it.value
            else {
                result[it.key] = oldValue - it.value
                if (result[it.key]!! < 0)
                    result[it.key] = 0
            }
        }
    }
    return result
}