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