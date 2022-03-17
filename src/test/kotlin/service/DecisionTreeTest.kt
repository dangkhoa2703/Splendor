package service

import entity.*
import kotlin.test.*

/**
 *  Test class for DecisionTrees
 * */
class DecisionTreeTest {

    private val rootService: RootService = RootService()
    private val decisionTree: DecisionTree = DecisionTree(rootService)
    private val board: Board = Board(levelOneOpen = mutableListOf(
        DevCard(0, price = mutableMapOf(
            GemType.RED to 2,
        ), 0, 5, GemType.GREEN),
        DevCard(0, price = mutableMapOf(
            GemType.BLUE to 2,
            GemType.RED to 4,
            GemType.BLACK to 1
        ), 0, 4, GemType.RED)
    ), gems = mutableMapOf(
        GemType.RED to 5,
        GemType.BLUE to 5,
        GemType.BLACK to 5,
        GemType.YELLOW to 5,
        GemType.GREEN to 5,
    ))
    private val player: Player = Player("test", PlayerType.HUMAN, gems = mutableMapOf(
        GemType.RED to 1
    ))
    private val enemies: List<Player> = listOf(
        Player("Peter", PlayerType.HUMAN)
    )

    /**
     * Test for simulateMove gems
     */
    @Test
    fun testSimulateMove_gems() {
        val test: Pair<Turn, Pair<Board, Player>>? = decisionTree
            .simulateMove(TurnType.TAKE_GEMS, board, player, enemies)
        assertNotNull(test)
        assertEquals(Turn(mutableMapOf(
            GemType.RED to 1,
            GemType.BLUE to 1,
            GemType.BLACK to 1
        ), listOf(), TurnType.TAKE_GEMS, true), test.first)
        val compareBoard = board.cloneForSimulation()
        compareBoard.gems[GemType.RED] = 4
        compareBoard.gems[GemType.BLUE] = 4
        compareBoard.gems[GemType.BLACK] = 4
        assertEquals(compareBoard, test.second.first)
        val comparePlayer = player.clone()
        comparePlayer.gems[GemType.RED] = 2
        comparePlayer.gems[GemType.BLUE] = 1
        comparePlayer.gems[GemType.BLACK] = 1
        assertEquals(comparePlayer, test.second.second)
    }

    /**
     * Test for simulateMove null
     */
    @Test
    fun testSimulateMove_null() {
        val player = Player("test", PlayerType.HUMAN)
        var test: Pair<Turn, Pair<Board, Player>>? = decisionTree
            .simulateMove(TurnType.BUY_CARD, this.board, player, enemies)
        assertNull(test)
        val board: Board = this.board.cloneForSimulation()
        board.gems.clear()
        test = decisionTree.simulateMove(TurnType.TAKE_GEMS, board, this.player, enemies)
        assertNull(test)
    }

    /**
     * Test for simulateMove cards
     */
    @Test
    fun testSimulateMove_cards() {
        player.gems[GemType.RED] = 2
        val test: Pair<Turn, Pair<Board, Player>>? = decisionTree
            .simulateMove(TurnType.BUY_CARD, board, player, enemies)
        assertNotNull(test)
        val boughtCard = board.levelOneOpen[0]
//        assertEquals(Turn(mutableMapOf(), listOf(boughtCard), TurnType.BUY_CARD), test.first)
        val compareBoard = board.cloneForSimulation()
        compareBoard.levelOneOpen.remove(boughtCard)
//        assertEquals(compareBoard, test.second.first)
        val comparePlayer = player.clone()
        comparePlayer.devCards.add(boughtCard)
        comparePlayer.score += boughtCard.prestigePoints
        comparePlayer.bonus[boughtCard.bonus] = (comparePlayer.bonus[boughtCard.bonus] ?: 0) + 1
        comparePlayer.gems[GemType.RED] = 0
//        assertEquals(comparePlayer, test.second.second)
    }

}