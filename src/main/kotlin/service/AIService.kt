package service

import entity.*

/**
 *  Class for artificial intelligence
 * */
class AIService(private val rootService: RootService): AbstractRefreshingService()
{

    /** calculates the best possible Turn for the current player and returns an object of type Turn which includes
     *  a map of gems and a list of at most one card whose contents depend on the TurnType */
    fun calculateBestTurn(player : Player, gameState: GameState) : Turn?
    {
        return null
    }

    /**
     * Help-function that calculates cost-scores (specified here: https://sopra-gitlab.cs.tu-dortmund.de/sopra22A/gruppe03/Projekt2/-/wikis/3-Produkt/KI-Gruppe#strategie-f%C3%BCr-minimax-algorithmus) for each DevCard
     * @return Map of all cards with respective scores
     */
    fun calculateDevCardCostScores(devCard: DevCard, board: Board, player: Player, enemyPlayer: List<Player>) : Map<DevCard, Double> {
        val cardsOnBoard: MutableList<DevCard> = mutableListOf()
        cardsOnBoard.addAll(board.levelOneOpen)
        cardsOnBoard.addAll(board.levelTwoOpen)
        cardsOnBoard.addAll(board.levelThreeOpen)
        cardsOnBoard.sortBy { it.calculateGemPrice() }
        val deltaCost: Int = cardsOnBoard[cardsOnBoard.size - 1].calculateGemPrice() - cardsOnBoard[0].calculateGemPrice()
        var result: MutableMap<DevCard, Double> = mutableMapOf()
        result[cardsOnBoard[0]] = 1.0
        cardsOnBoard.forEachIndexed { index, devCard ->
            if(index >= 1) {
                val previousCard = cardsOnBoard[index - 1]
                val previousCardScore: Double =  result[previousCard]!!
                if(devCard.calculateGemPrice() == previousCard.calculateGemPrice()) {
                    result[devCard] = previousCardScore
                } else {
                    result[devCard] = previousCardScore - (devCard.calculateGemPrice() - previousCard.calculateGemPrice()) * 1 / deltaCost
                }
            }
        }
        return result
    }

    /**
     * Help-function that calculates purchasing-power-scores (specified here: https://sopra-gitlab.cs.tu-dortmund.de/sopra22A/gruppe03/Projekt2/-/wikis/3-Produkt/KI-Gruppe#strategie-f%C3%BCr-minimax-algorithmus) for each DevCard
     * @return Map of all cards with respective scores
     */
    fun calculateDevCardPurchasingPowerScores(devCard: DevCard, board: Board, player: Player, enemyPlayer: List<Player>) : Map<DevCard, Double> {
        val cardsOnBoard: MutableList<DevCard> = mutableListOf()
        cardsOnBoard.addAll(board.levelOneOpen)
        cardsOnBoard.addAll(board.levelTwoOpen)
        cardsOnBoard.addAll(board.levelThreeOpen)
        var mapOfRoundsNeeded: MutableMap<DevCard, Pair<Int, Int>> = mutableMapOf()
        cardsOnBoard.forEach {
            mapOfRoundsNeeded[it] = calculateAmountOfRoundsNeededToBuy(player, it)
        }
        cardsOnBoard.sortWith(compareBy<DevCard> { mapOfRoundsNeeded[it]!!.first }.thenByDescending { mapOfRoundsNeeded[it]!!.second })
        var result: MutableMap<DevCard, Double> = mutableMapOf()
        result[cardsOnBoard[0]] = 1.0
        cardsOnBoard.forEachIndexed { index, devCard ->
            if(index >= 1) {
                val previousCard = cardsOnBoard[index - 1]
                val previousCardScore: Double =  result[previousCard]!!
                if(mapOfRoundsNeeded[devCard] == mapOfRoundsNeeded[previousCard]) {
                    result[devCard] = previousCardScore
                } else {
                    result[devCard] = previousCardScore - (1 / (cardsOnBoard.size - 1))
                }
            }
        }
        return result
    }

    /**
     * Help-function that calculates amount of gems needed to buy the card
     */
    fun DevCard.calculateGemPrice(): Int {
        var result: Int = 0
        price.forEach {
            result += it.value
        }
        return result
    }

    /**
     * Help-function that calculates amount of rounds needed to buy the card and therefore additional gems
     */
    fun calculateAmountOfRoundsNeededToBuy(player: Player, card: DevCard): Pair<Int, Int> {
        var result: Int = 0
        var leftOverGems: Int = 0
        var missingGems: MutableMap<GemType, Int> = calculateMissingGems(player, card.price)
        if(missingGems.size <= 0)
            return Pair(0, 0)
        card.price.keys.forEach {
            val value: Int = card.price[it]!!
            if(value % 2 == 0) {
                result += value /2
                missingGems[it] = 0
            } else {
                result += (value / 2) + 1
                var remainingGems: Int = 2
                missingGems.forEach {
                    if(remainingGems <= 0) {
                        if(it.value > 0)
                            missingGems[it.key] = missingGems[it.key]!! - 1
                        remainingGems -= 1
                    }
                }
                leftOverGems += remainingGems
            }
        }
        return Pair(result, leftOverGems)
    }

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

}