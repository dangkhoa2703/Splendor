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