package service

import entity.*

class DecisionTree(var rootService: RootService) {

    /**
     * Builds the decision Tree with mini-max-algorithm
     */
    fun computeDecisionTree(turns: Int, board: Board, players: List<Player>) : Turn {
        val root: TreeNode<Turn> = TreeNode.createEmptyTree(turns * players.size, 3)
        miniMax(root, Double.MIN_VALUE, Double.MAX_VALUE, 0, TurnType.EMPTY,
            board.cloneForSimulation(), players.clone().toMutableList())
        return root.getChildren().sortedBy { treeNode -> if (treeNode.data == null) -1.0
            else treeNode.data!!.evaluation }[0].data!!
    }

    /**
     * Executes miniMax-algorithm on [node]
     * @param player: Important: Simulated player has to be at first index of the list, followed by enemies in order
     */
    private fun miniMax(node: TreeNode<Turn>, alpha_: Double, beta_: Double, playerIndex: Int, turnType: TurnType,
                        board: Board, player: MutableList<Player>): Double {
        var alpha = alpha_
        var beta = beta_
        val maximizing = playerIndex == 0
        // Simulate the current Turn
        val enemies: List<Player> = player.minus(player[playerIndex])
        var simulation: Pair<Turn, Pair<Board, Player>>? = null
        if(turnType != TurnType.EMPTY) {
            simulation = simulateMove(turnType, board, player[playerIndex], enemies)
            if(simulation == null) {
                return -1.0 // Invalid move
            }
            node.data = simulation.first
        }
        // Leaf
        if(node.getChildren().isEmpty()) {
            node.data!!.evaluation = rootService.aiService.computeTurnEvaluationScore(simulation!!.second.second, enemies)
            return node.data!!.evaluation
        }
        // MiniMax
        val newBoard: Board = if (turnType == TurnType.EMPTY) board else simulation!!.second.first
        val newPlayerList: MutableList<Player> = player.toMutableList()
        if(turnType != TurnType.EMPTY)
        {
            newPlayerList.remove(player[playerIndex])
            newPlayerList.add(simulation!!.second.second)
        }
        val newPlayerIndex: Int = (playerIndex + 1) % player.size
        if(maximizing) {
            var maxEval = Double.MIN_VALUE
            for(i in 0 until node.getChildren().size)
            {
                val elem = node.getChildren()[i]
                val newTurnType: TurnType = TurnType.values()[TurnType.TAKE_GEMS.ordinal + i]
                val eval = miniMax(elem, alpha, beta, newPlayerIndex, newTurnType, newBoard, newPlayerList)
                maxEval = maxOf(maxEval, eval)
                alpha = maxOf(alpha, eval)
                if(beta <= alpha)
                    break
            }
            return maxEval
        } else {
            var minEval = Double.MAX_VALUE
            for(i in 0 until node.getChildren().size)
            {
                val elem = node.getChildren()[i]
                val newTurnType: TurnType = TurnType.values()[TurnType.TAKE_GEMS.ordinal + i]
                val eval = miniMax(elem, alpha, beta, newPlayerIndex, newTurnType, newBoard, newPlayerList)
                minEval = minOf(minEval, eval)
                beta = minOf(beta, eval)
                if(beta <= alpha)
                    break
            }
            return minEval
        }
    }

    /**
     * Simulates the given move (through [turnType])
     * @return appropriate [Turn], [Board] and [Player] objects
     */
    fun simulateMove(turnType: TurnType, board: Board, player: Player, enemyPlayer: List<Player>):
            Pair<Turn, Pair<Board, Player>>? {
        val newBoard = board.cloneForSimulation()
        val newPlayer = player.clone()
        // No cards on the board
        if((newBoard.levelOneOpen.size + newBoard.levelTwoOpen.size + newBoard.levelThreeOpen.size) <= 0
            || turnType == TurnType.EMPTY)
            return null
        val bestDevCards: Map<DevCard, Double> = rootService.aiService
            .calculateGeneralDevCardScore(newBoard, newPlayer, enemyPlayer)
        return when (turnType) {
            TurnType.TAKE_GEMS -> {
                val chosenGems: Pair<Map<GemType, Int>, Boolean> = rootService.aiService
                    .chooseGems(bestDevCards, player, board)
                val mapOfChosenGems: MutableMap<GemType, Int> = chosenGems.first.toMutableMap()
                if(mapOfChosenGems.isEmpty()) { // Invalid move
                    return null
                }
                val turn = Turn(mapOfChosenGems, listOf(), TurnType.TAKE_GEMS, chosenGems.second)
                // Update Board and Player
                newBoard.gems = newBoard.gems.combine(mapOfChosenGems, subtract = true)
                mapOfChosenGems.forEach {
                    newPlayer.gems[it.key] = (newPlayer.gems[it.key] ?: 0) + it.value
                }
                return Pair(turn, Pair(newBoard, newPlayer))
            }
            TurnType.BUY_CARD -> {
                val cardsSortedAfterScore = bestDevCards.keys.sortedBy { bestDevCards[it] }
                val affordableCards: MutableList<DevCard> = mutableListOf()
                val totalGemsOfPlayer: Map<GemType, Int> = player.gems.combine(player.bonus)
                for(card in cardsSortedAfterScore) {
                    if(isCardAcquirable(card, totalGemsOfPlayer)) {
                        affordableCards.add(card)
                        break
                    }
                }
                if(affordableCards.isEmpty()) {
                    return null
                }
                // Update Board and player
                val turn = Turn(mapOf(), listOf(affordableCards[0]), TurnType.BUY_CARD)
                newBoard.levelOneOpen.remove(affordableCards[0])
                newBoard.levelTwoOpen.remove(affordableCards[0])
                newBoard.levelThreeOpen.remove(affordableCards[0])
                newPlayer.devCards.add(affordableCards[0])
                newPlayer.gems.clear()
                newPlayer.gems.putAll(newPlayer.gems.combine(affordableCards[0].price, subtract = true))
                newPlayer.bonus[affordableCards[0].bonus] = (newPlayer.bonus[affordableCards[0].bonus]?: 0) + 1
                newPlayer.score += affordableCards[0].prestigePoints
                return Pair(turn, Pair(newBoard, newPlayer))
            }
            else -> null
        }
    }

    /**
     * check if the card is for the current player affordable
     *
     * @param card the card which the player chose
     * @param payment map of gems from player
     * @return true if player can this card afford, else return false
     * */
    private fun isCardAcquirable(card: DevCard, payment: Map<GemType,Int>): Boolean {

        val tempGemMap = card.price.toMutableMap()

        card.price.forEach { (gemType) ->
            tempGemMap[gemType] = tempGemMap.getValue(gemType) - payment.getValue(gemType)
        }

        val gemsNeeded = tempGemMap.filterValues { it >= 0 }.values.sum()
        return (gemsNeeded == 0) || (gemsNeeded <= payment.getValue(GemType.YELLOW))
    }


}