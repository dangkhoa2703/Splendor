package service

import entity.GameState
import entity.TreeNode
import entity.Turn
import entity.TurnType

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

}