package service

import entity.*

/**
 *  Class for artificial intelligence
 * */
class AIService(private val rootService: RootService): AbstractRefreshingService()
{

    /**
     * Calculates the best possible Turn for the current player and returns an object of type Turn which includes
     * a map of gems and a list of at most one card whose contents depend on the TurnType
     */
    fun calculateBestTurn(player : Player, gameState: GameState) : Turn?
    {
        return null
    }

    /**
     * Calculates the best possible card to buy based on predefined heuristics (specified here:
     * https://sopra-gitlab.cs.tu-dortmund.de/sopra22A/gruppe03/Projekt2/-/wikis/3-Produkt/KI-Gruppe#strategie-
     *  f%C3%BCr-minimax-algorithmus)
     * for each DevCard
     */
    fun calculateGeneralDevCardScore(board: Board, player: Player, enemyPlayer: List<Player>): Map<DevCard, Double> {
        val weights : DoubleArray = doubleArrayOf(0.4, 0.2, 0.2, 0.2)
        val costScores = calculateDevCardCostScores(board)
        val playerPurchasingPowerScore = calculateDevCardPurchasingPowerScores(board, player)
        val enemyPurchasingPowerScore = calculateDevCardPurchasingPowerScoresForEnemies(board, enemyPlayer)
        val importanceScore = calculateDevCardImportanceScore(board)
        val result: MutableMap<DevCard, Double> = mutableMapOf()
        costScores.forEach {
            result[it.key] = weights[0] * it.value + weights[1] * playerPurchasingPowerScore[it.key]!!
            + weights[2] * enemyPurchasingPowerScore[it.key]!! + weights[3] * importanceScore[it.key]!!
        }
        return result
    }

    /**
     * Help-function that calculates cost-scores for each DevCard
     * @return Map of all cards with respective scores
     */
    fun calculateDevCardCostScores(board: Board) : Map<DevCard, Double> {
        val cardsOnBoard: MutableList<DevCard> = mutableListOf()
        cardsOnBoard.addAll(board.levelOneOpen)
        cardsOnBoard.addAll(board.levelTwoOpen)
        cardsOnBoard.addAll(board.levelThreeOpen)
        cardsOnBoard.sortBy { it.calculateGemPrice() }
        val deltaCost: Int = cardsOnBoard[cardsOnBoard.size - 1].calculateGemPrice() -
                cardsOnBoard[0].calculateGemPrice()
        val result: MutableMap<DevCard, Double> = mutableMapOf()
        result[cardsOnBoard[0]] = 1.0
        cardsOnBoard.forEachIndexed { index, devCard ->
            if(index >= 1) {
                val previousCard = cardsOnBoard[index - 1]
                val previousCardScore: Double =  result[previousCard]!!
                if(devCard.calculateGemPrice() == previousCard.calculateGemPrice()) {
                    result[devCard] = previousCardScore
                } else {
                    result[devCard] = previousCardScore - (devCard.calculateGemPrice() -
                            previousCard.calculateGemPrice()).toDouble() * (1.0 / deltaCost.toDouble())
                }
            }
        }
        return result
    }

    /**
     * Help-function that calculates purchasing-power-scores for each DevCard
     * @return Map of all cards with respective scores
     */
    fun calculateDevCardPurchasingPowerScores(board: Board, player: Player) : Map<DevCard, Double> {
        val cardsOnBoard: MutableList<DevCard> = mutableListOf()
        cardsOnBoard.addAll(board.levelOneOpen)
        cardsOnBoard.addAll(board.levelTwoOpen)
        cardsOnBoard.addAll(board.levelThreeOpen)
        val mapOfRoundsNeeded: MutableMap<DevCard, Pair<Int, Int>> = mutableMapOf()
        cardsOnBoard.forEach {
            mapOfRoundsNeeded[it] = calculateAmountOfRoundsNeededToBuy(player, it)
        }
        val sizeWithoutDuplicates = mapOfRoundsNeeded.values.toSet().size
        cardsOnBoard.sortWith(compareBy<DevCard> { mapOfRoundsNeeded[it]!!.first }.
            thenByDescending { mapOfRoundsNeeded[it]!!.second })
        val result: MutableMap<DevCard, Double> = mutableMapOf()
        result[cardsOnBoard[0]] = 1.0
        cardsOnBoard.forEachIndexed { index, devCard ->
            if(index >= 1) {
                val previousCard = cardsOnBoard[index - 1]
                val previousCardScore: Double =  result[previousCard]!!
                if(mapOfRoundsNeeded[devCard] == mapOfRoundsNeeded[previousCard]) {
                    result[devCard] = previousCardScore
                } else {
                    result[devCard] = previousCardScore - (1.0 / (sizeWithoutDuplicates - 1).toDouble())
                }
            }
        }
        return result
    }

    /**
     * Help-function that calculates the score of the cards based on the purchasing-power-scores of
     * the enemies
     * @return Map of all cards with respective scores
     */
    fun calculateDevCardPurchasingPowerScoresForEnemies(board: Board, enemyPlayer: List<Player>) :
            Map<DevCard, Double> {
        //Map with each player and the card-scores of that player
        val scoresOfEnemies: MutableMap<Player, MutableMap<DevCard, Double>> = mutableMapOf()
        enemyPlayer.forEach {
            scoresOfEnemies[it] = calculateDevCardPurchasingPowerScores(board,it) as MutableMap<DevCard, Double>
        }
        //Map to save the different scores of the enemies for each devCard
        val allScores: MutableMap<DevCard,MutableList<Double>> = mutableMapOf()
        //Map to save the current card with the score of the current player temporary
        var tmp: MutableMap<DevCard, Double>
        scoresOfEnemies.keys.forEach { it ->
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
        val result: MutableMap<DevCard, Double> = mutableMapOf()
        //Calculate the average score of the scores in the list and safe it in our final map
        allScores.keys.forEach {
            result[it] = allScores[it]!!.average()
        }
        //Reverse the rank to determine the scores of the cards for our current player and not for the enemies
        result.keys.forEach {
            result[it] = 1.0 - result[it]!!
        }
        return result
    }

    /**
     * Help-function that calculates the score of the cards based on the usefulness of the card
     * @return Map of all cards with respective scores
     */
    fun calculateDevCardImportanceScore(board: Board) : Map<DevCard, Double> {
        val cardsOnBoard: MutableList<DevCard> = mutableListOf()
        cardsOnBoard.addAll(board.levelOneOpen)
        cardsOnBoard.addAll(board.levelTwoOpen)
        cardsOnBoard.addAll(board.levelThreeOpen)
        val mapOfBuyableDevCards: MutableMap<GemType, Int> = mutableMapOf()
        val mapOfNoblesWithRespectiveBonus: MutableMap<GemType, Int> = mutableMapOf()
        for(gemType in GemType.values()) {
            var amountOfBuyableCards = 0
            for(cardOnBoard in cardsOnBoard) {
                if(cardOnBoard.price.containsKey(gemType) && cardOnBoard.price[gemType]!! > 0) {
                    amountOfBuyableCards++
                }
            }
            mapOfBuyableDevCards[gemType] = amountOfBuyableCards
            var amountOfNoblesWithRespectiveBonus = 0
            for(noble in board.nobleTiles) {
                if(noble.condition.containsKey(gemType) && noble.condition[gemType]!! > 0) {
                    amountOfNoblesWithRespectiveBonus++
                }
            }
            mapOfNoblesWithRespectiveBonus[gemType] = amountOfNoblesWithRespectiveBonus
        }
        cardsOnBoard.sortWith(compareByDescending<DevCard> { it.prestigePoints }
            .thenByDescending { mapOfNoblesWithRespectiveBonus[it.bonus] ?: 0 }
            .thenByDescending { mapOfBuyableDevCards[it.bonus] ?: 0 })
        val result: MutableMap<DevCard, Double> = mutableMapOf()
        result[cardsOnBoard[0]] = 1.0
        cardsOnBoard.forEachIndexed { index, _ ->
            if(index >= 1) {
                result[cardsOnBoard[index]] = 1.0 - (index.toDouble()/(cardsOnBoard.size - 1).toDouble())
            }
        }
        return result
    }

    /**
     * Help-function that calculates the amount of missing gems from the gems of the player
     * and the given map of gems
     */
    fun calculateMissingGems(player: Player, costs: Map<GemType,Int>): MutableMap<GemType, Int> {
        val result = mutableMapOf<GemType, Int>()
        player.gems.forEach {
            val costsForIndividualGemType: Int = costs[it.key] ?: 0
            val gemsOwnedByPlayer: Int = it.value + (player.bonus[it.key] ?: 0)
            val difference = gemsOwnedByPlayer - costsForIndividualGemType
            if(difference < 0)
                result[it.key] = -1 * difference
        }
        return result
    }

    /**
     * Help-function that calculates amount of rounds needed to buy the card
     * and therefore additional gems
     */
    fun calculateAmountOfRoundsNeededToBuy(player: Player, card: DevCard): Pair<Int, Int> {
        var result = 0
        var leftOverGems = 0
        val missingGems: MutableMap<GemType, Int> = calculateMissingGems(player, card.price)
        if(missingGems.isEmpty())
            return Pair(0, 0)
        missingGems.keys.forEach { it ->
            val value: Int = missingGems[it]!!
            if(value % 2 == 0) {
                result += value /2
                missingGems[it] = 0
            } else {
                result += (value / 2) + 1
                missingGems[it] = 0
                var remainingGems = 2
                missingGems.forEach {
                    if(remainingGems > 0) {
                        if(it.value > 0) {
                            missingGems[it.key] = missingGems[it.key]!! - 1
                            remainingGems -= 1
                        }
                    }
                }
                leftOverGems += remainingGems
            }
        }
        return Pair(result, leftOverGems)
    }
}