package service

import entity.*
import java.lang.IllegalArgumentException

class DecisionTree(var aiService: AIService) {

    /**
     * Builds the decision Tree with mini-max-algorithm
     */
    fun computeDecisionTree(turns: Int, gameState: GameState) : Turn {
        val root: TreeNode<Turn> = TreeNode.createEmptyTree(turns * gameState.playerList.size, 3)
        // MiNIMax rekursiv dies das

        // Rückverfolgung
        return Turn(mutableMapOf(), mutableListOf(), TurnType.EMPTY)
    }

    /**
     * Executes miniMax-algorithm on [node]
     * @param player: Important: Simulated player has to be at the first index of the list, followed by enemies in order
     */
    private fun miniMax(node: TreeNode<Turn>, alpha: Double, beta: Double, playerIndex: Int, board: Board, player: List<Player>): Double {
        if(node.getChildren().isEmpty()) {
            node.data =
            node.data!!.evaluation = aiService.computeTurnEvaluationScore(board, player[0], player.drop(1))
            return
        }

        // Check for invalid move
        if(board == null || player == null)
            return

    }

}