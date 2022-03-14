package service

import entity.*

class DecisionTree {

    /**
     * Builds the decision Tree with mini-max-algorithm
     */
    fun computeDecisionTree(turns: Int, gameState: GameState) : Turn {
        val root: TreeNode<Turn> = TreeNode.createEmptyTree(turns * gameState.playerList.size, 3)
        // MiNIMax rekursiv dies das

        // Rückverfolgung
        return Turn(mutableMapOf(), mutableListOf(), TurnType.EMPTY)
    }

    private fun miniMax(node: TreeNode<Turn>, alpha: Double, beta: Double, playerIndex: Int, board: Board, player: List<Player>) {

    }

}