package service

import entity.*

class DecisionTree(var rootService: RootService) {

    /**
     * Builds the decision Tree with mini-max-algorithm
     */
    fun computeDecisionTree(turns: Int, gameState: GameState) : Turn {
        val root: TreeNode<Turn> = TreeNode.createEmptyTree(turns * gameState.playerList.size, 3)
        miniMax(root, Double.MIN_VALUE, Double.MAX_VALUE, 0, TurnType.EMPTY, gameState.board.cloneForSimulation(), gameState.playerList.clone().toMutableList())
        return root.getChildren().sortedBy { treeNode -> if (treeNode.data == null) -1.0 else treeNode.data!!.evaluation }[0].data!!
    }

    /**
     * Executes miniMax-algorithm on [node]
     * @param player: Important: Simulated player has to be at the first index of the list, followed by enemies in order
     */
    private fun miniMax(node: TreeNode<Turn>, alpha: Double, beta: Double, playerIndex: Int, turnType: TurnType, board: Board, player: MutableList<Player>): Double {
        val maximizing = playerIndex == 0
        // Simulate the current Turn
        val enemies: List<Player> = player.minus(player[playerIndex])
        val simulation: Pair<Turn, Pair<Board, Player>>? = simulateMove(turnType, board, player[playerIndex], enemies)
        if(simulation == null) {
            return -1.0 // Invalid move
        }
        node.data = simulation.first
        // Leaf
        if(node.getChildren().isEmpty()) {
            node.data!!.evaluation = rootService.aiService.computeTurnEvaluationScore(simulation.second.first, simulation.second.second, enemies)
            return node.data!!.evaluation
        }
        //TODO: children
        return -1.0
    }

    private fun simulateMove(turnType: TurnType, board: Board, player: Player, enemyPlayer: List<Player>): Pair<Turn, Pair<Board, Player>>? {
        val newBoard = board.cloneForSimulation()
        val newPlayer = player.clone()
        val bestDevCards: Map<DevCard, Double> = rootService.aiService.calculateGeneralDevCardScore(newBoard, newPlayer, enemyPlayer)
        return when (turnType) {
            TurnType.TAKE_GEMS -> {
                val chosenGems: Pair<Map<GemType, Int>, Boolean> = rootService.aiService.chooseGems(bestDevCards, player, board)
                val mapOfChosenGems: MutableMap<GemType, Int> = chosenGems.first.toMutableMap()
                if(mapOfChosenGems == null) { // Invalid move
                    return null
                }
                val turn = Turn(mapOfChosenGems, listOf(), TurnType.TAKE_GEMS, chosenGems.second)
                // Update Board and Player
                newBoard.gems = newBoard.gems.combine(mapOfChosenGems, subtract = true)
                newPlayer.gems.clear()
                newPlayer.gems.putAll(newPlayer.gems.combine(mapOfChosenGems))
                return Pair(turn, Pair(newBoard, newPlayer))
            }
            TurnType.BUY_CARD -> {
                val cardsSortedAfterScore = bestDevCards.keys.sortedBy { bestDevCards[it] }
                val affordableCards: MutableList<DevCard> = mutableListOf()
                val totalGemsOfPlayer: Map<GemType, Int> = player.gems.combine(player.bonus)
                for(card in cardsSortedAfterScore) {
                    if(rootService.gameService.isCardAcquirable(card, totalGemsOfPlayer)) {
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

}