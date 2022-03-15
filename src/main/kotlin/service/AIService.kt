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
     * @param player
     * @param gameState
     * @return The best possible turn for the player
     */
    fun calculateBestTurn(player : Player, gameState: GameState) : Turn
    {
        val decisionTree = DecisionTree(rootService)
        val currentBoard = gameState.board
        val playerList = gameState.playerList as MutableList<Player>
        if (playerList[0] != player) {
            playerList.remove(player)
            playerList.add(0,player)
        }
        //Calculate amount of turns for computeDecisionTree
        val aiDifficulty = when {
            (player.playerType == PlayerType.EASY) -> 1
            (player.playerType == PlayerType.MEDIUM) -> 2
            //if Human or HARD
            else -> 3
        }
        return decisionTree.computeDecisionTree(aiDifficulty,currentBoard,playerList)
    }

    /**
     * Calculates the best possible card to buy based on predefined heuristics (specified here:
     * https://sopra-gitlab.cs.tu-dortmund.de/sopra22A/gruppe03/Projekt2/-/wikis/3-Produkt/KI-Gruppe#strategie-
     *  f%C3%BCr-minimax-algorithmus) for each DevCard
     * @param board
     * @param player
     * @param enemyPlayer
     * @return Returns a map with all open cards and their individual score
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
     * Help-function that calculates the cost-scores for each DevCard
     * @param board
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
     * Help-function that calculates the purchasing-power-scores for each DevCard for the current player
     * @param board
     * @param player
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
     * @param board
     * @param enemyPlayer
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
     * Help-function that calculates the score of the cards based on the benefit of that card
     * @param board
     * @return Map of all cards with respective scores
     */
    fun calculateDevCardImportanceScore(board: Board) : Map<DevCard, Double> {
        val cardsOnBoard: MutableList<DevCard> = mutableListOf()
        cardsOnBoard.addAll(board.levelOneOpen)
        cardsOnBoard.addAll(board.levelTwoOpen)
        cardsOnBoard.addAll(board.levelThreeOpen)
        val mapOfPurchasableDevCards: MutableMap<GemType, Int> = mutableMapOf()
        val mapOfNoblesWithRespectiveBonus: MutableMap<GemType, Int> = mutableMapOf()
        for(gemType in GemType.values()) {
            var amountOfPurchasableCards = 0
            for(cardOnBoard in cardsOnBoard) {
                if(cardOnBoard.price.containsKey(gemType) && cardOnBoard.price[gemType]!! > 0) {
                    amountOfPurchasableCards++
                }
            }
            mapOfPurchasableDevCards[gemType] = amountOfPurchasableCards
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
            .thenByDescending { mapOfPurchasableDevCards[it.bonus] ?: 0 })
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
     * Help-function that calculates the amount of missing gems the player needs to buy the card
     * @param player
     * @param costs the costs of a card
     * @return Map with each missing GemType and the amount of missing gems of this GemType
     */
    fun calculateMissingGems(player: Player, costs: Map<GemType,Int>): MutableMap<GemType, Int> {
        val result = mutableMapOf<GemType, Int>()
        costs.keys.forEach {
            val costsForIndividualGemType: Int = costs[it] ?: 0
            var gemsOwnedByPlayer = 0
            if (player.gems.containsKey(it)) {
                gemsOwnedByPlayer = player.gems[it]!!
            }
            if (player.bonus.containsKey(it)) {
                gemsOwnedByPlayer += (player.bonus[it] ?: 0)
            }
            val difference = gemsOwnedByPlayer - costsForIndividualGemType
            if(difference < 0)
                result[it] = -1 * difference
        }
        return result
    }

    /**
     * Help-function that calculates the amount of rounds needed to buy the card and how many additional gems we can get
     * @param player
     * @param card
     * @return Pair(Amount of rounds, additional gems)
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

    /**
     * Help-function to calculate which gems a player should choose
     * @param bestDevCards all open cards with their own score
     * @param player
     * @param board
     * @return Pair with the chosen gems and boolean (true = take three gems, false = take two gems)
     */
    fun chooseGems(bestDevCards: Map<DevCard, Double>, player: Player, board: Board) : Pair<Map<GemType, Int>,Boolean> {
        val gems: MutableMap<GemType, Int> = mutableMapOf()
        //if gems on board are empty
        if (board.gems.isEmpty()) {
            return Pair(mutableMapOf(),false)
        }
        //safe all open cards that still need gems
        val devCardsWithMissingGems: ArrayList<DevCard> = arrayListOf()
        bestDevCards.keys.forEach {
            val missingGemsColours: MutableMap<GemType, Int> = calculateMissingGems(player,it.price)
            if (missingGemsColours.isNotEmpty()) {
                devCardsWithMissingGems.add(it)
            }
        }
        var takenGems = 0
        val allMissingColours: MutableSet<GemType> = mutableSetOf()
        val gemsOnBoard = board.gems
        devCardsWithMissingGems.forEach { devCard ->
            //if no gems are left
            if (gemsOnBoard.isEmpty()) {
                if (gems.size == 1) {
                    return Pair(gems,false)
                }
                return Pair(gems,true)
            }
            val missingGemsForCurrentDevCard: MutableMap<GemType, Int> = calculateMissingGems(player,devCard.price)
            val sortedListOfMissingGems: List<GemType> = missingGemsForCurrentDevCard.keys
                .sortedBy { missingGemsForCurrentDevCard[it] }
            allMissingColours.addAll(sortedListOfMissingGems)
            sortedListOfMissingGems.forEach {
                var amountOfGemColourOnBoard = (gemsOnBoard[it] ?: 0)
                var amountOfSameGemColour = 0
                if (amountOfGemColourOnBoard > missingGemsForCurrentDevCard[it]!!) {
                    //while the colour is still available, the player still needs that colour and did not choose
                    //two gems of this colour yet
                    while ((amountOfGemColourOnBoard > 0)
                        && (amountOfSameGemColour < missingGemsForCurrentDevCard[it]!!)
                        && (amountOfSameGemColour < 2)
                    ) {
                        amountOfSameGemColour += 1
                        amountOfGemColourOnBoard -= 1
                    }
                    if (gems.isEmpty() && amountOfSameGemColour == 2) {
                        gems[it] = 2
                        return Pair(gems,false)
                    }
                    if (!gems.containsKey(it) && (amountOfGemColourOnBoard > 0)) {
                        gems[it] = 1
                        takenGems++
                    }
                    gemsOnBoard[it]!!.minus(1)
                    if (takenGems == 3) {
                        return Pair(gems,true)
                    }
                }
            }
        }
        //if we still have some gems left
        if (takenGems < 3 && board.gems.isNotEmpty()) {
            // we cannot buy one card after taking gems, so we choose gems which helps us
            board.gems.keys.forEach {
                if (takenGems < 3 && allMissingColours.contains(it)) {
                    if (!gems.containsKey(it)) {
                        gems[it] = 1
                        takenGems++
                    }
                }
            }
            // if we still can take gems and there are some gems on the board, choose random gems
            board.gems.keys.forEach {
                if (takenGems < 3) {
                    if (!gems.containsKey(it)) {
                        gems[it] = 1
                        takenGems++
                    }
                }
            }
        }
        return Pair(gems,true)
    }


    /**
     * Function to calculate the heuristic for our artificial intelligence
     * @param player
     * @param enemies
     * @return The value of the heuristic
     */
    fun computeTurnEvaluationScore(player: Player, enemies: List<Player>): Double {
        // 1.Heuristic
        val playersPrestigePoints = player.score.toDouble()
        // 2.Heuristic
        val amountOfBoni = player.devCards.size.toDouble()
        // 3.Heuristic
        val winningProbability : Double
        //Calculate the probability to win the game
        var playerAmountOfGems = 0.0
        //Calculate number of gems of the current player
        player.gems.forEach {
            playerAmountOfGems += it.value.toDouble()
        }
        val playerMinusEnemyPrestige: ArrayList<Double> = arrayListOf()
        val playerMinusEnemyAmountOfGems: ArrayList<Double> = arrayListOf()
        val playerMinusEnemyAmountOfBoni: ArrayList<Double> = arrayListOf()
        enemies.forEach {
            playerMinusEnemyPrestige.add(playersPrestigePoints - it.score.toDouble())
            var enemyAmountOfGems = 0.0
            //Calculate number of gems of the current enemy
            it.gems.forEach { gemColour ->
                enemyAmountOfGems += gemColour.value.toDouble()
            }
            playerMinusEnemyAmountOfGems.add(playerAmountOfGems - enemyAmountOfGems)
            playerMinusEnemyAmountOfBoni.add(amountOfBoni - it.devCards.size.toDouble())
        }
        winningProbability = 0.5 * playerMinusEnemyPrestige.average()
        +0.2 * playerMinusEnemyAmountOfGems.average()
        +0.3 * playerMinusEnemyAmountOfBoni.average()
        //Calculate and return heuristic
        return 0.3 * playersPrestigePoints + 0.2 * amountOfBoni + 0.5 * winningProbability
    }
}