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
     * Help-function that calculates cost-scores
     * (specified here: https://sopra-gitlab.cs.tu-dortmund.de/sopra22A/gruppe03/Projekt2/-/wikis/3-Produkt/KI-Gruppe#strategie-f%C3%BCr-minimax-algorithmus) for each DevCard
     * @return Map of all cards with respective scores
     */
    fun calculateDevCardCostScores(board: Board) : Map<DevCard, Double> {
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
     * Help-function that calculates purchasing-power-scores
     * (specified here: https://sopra-gitlab.cs.tu-dortmund.de/sopra22A/gruppe03/Projekt2/-/wikis/3-Produkt/KI-Gruppe#strategie-f%C3%BCr-minimax-algorithmus) for each DevCard
     * @return Map of all cards with respective scores
     */
    fun calculateDevCardPurchasingPowerScores(board: Board, player: Player) : Map<DevCard, Double> {
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
     * Help-function that calculates the score of the cards based on the purchasing-power-scores of
     * the enemies
     * (specified here: https://sopra-gitlab.cs.tu-dortmund.de/sopra22A/gruppe03/Projekt2/-/wikis/3-Produkt/KI-Gruppe#strategie-f%C3%BCr-minimax-algorithmus) for each DevCard for the enemies
     * @return Map of all cards with respective scores
     */
    fun calculateDevCardPurchasingPowerScoresForEnemies(board: Board, enemyPlayer: List<Player>) : Map<DevCard, Double> {
        //Map with each player and the card-scores of that player
        var scoresOfEnemies: MutableMap<Player, MutableMap<DevCard, Double>> = mutableMapOf()
        enemyPlayer.forEach {
            scoresOfEnemies[it] = calculateDevCardPurchasingPowerScores(board,it) as MutableMap<DevCard, Double>
        }
        //Map to save the different scores of the enemies for each devCard
        var allScores: MutableMap<DevCard,MutableList<Double>> = mutableMapOf()
        //Map to save the current card with the score of the current player temporary
        var tmp: MutableMap<DevCard, Double>
        scoresOfEnemies.keys.forEach {
            tmp = scoresOfEnemies[it]!!
            tmp.keys.forEach {
                //if the map allScores does not contain the devCard yet
                if (!allScores.containsKey(it)) {
                    //add a new list for the scores of this card
                    allScores[it] = mutableListOf()
                }
                //add the score of the devCard to the list of scores
                tmp[it]?.let { it1 -> allScores[it]!!.add(it1) }
            }
        }
        var result: MutableMap<DevCard, Double> = mutableMapOf()
        //Calculate the average score of the scores in the list and safe it in our final map
        allScores.keys.forEach {
            result[it] = allScores[it]!!.average()
        }
        //Reverse the rank to determine the scores of the cards for our current player and not for the enemies
        result.keys.forEach {
            result[it] = 1 - result[it]!!
        }
        return result
    }

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
     * Help-function that calculates amount of rounds needed to buy the card and therefore additional gems
     */
    fun calculateAmountOfRoundsNeededToBuy(player: Player, card: DevCard): Pair<Int, Int> {
        var result = 0
        var leftOverGems = 0
        var missingGems: MutableMap<GemType, Int> = calculateMissingGems(player, card.price)
        if(missingGems.isEmpty())
            return Pair(0, 0)
        card.price.keys.forEach {
            val value: Int = card.price[it]!!
            if(value % 2 == 0) {
                result += value /2
                missingGems[it] = 0
            } else {
                result += (value / 2) + 1
                var remainingGems = 2
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

}