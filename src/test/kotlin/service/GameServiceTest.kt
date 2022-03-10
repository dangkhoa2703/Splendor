package service

import entity.GemType
import entity.PlayerType
import entity.*
import org.junit.jupiter.api.assertThrows
import kotlin.test.*

/**
 *  test class for functions of GameService
 * */
class GameServiceTest {
    /** RootService reference */
    private var root = RootService()

    /** tests if games start correctly */
    @Test
    fun testCreateNewGame(){

        val playerList1 = listOf(Pair("p1",PlayerType.HUMAN))
        val playerList2 = listOf(Pair("p1",PlayerType.HUMAN),Pair("p2",PlayerType.HUMAN))
        val playerList3 = listOf(
            Pair("p1",PlayerType.HUMAN),
            Pair("p2",PlayerType.HUMAN),
            Pair("p3",PlayerType.HUMAN))
        val playerList5 = listOf(
            Pair("p1",PlayerType.HUMAN),
            Pair("p2",PlayerType.HUMAN),
            Pair("p3",PlayerType.HUMAN),
            Pair("p4",PlayerType.HUMAN),
            Pair("p5",PlayerType.HUMAN))

        root.gameService.startNewGame(playerList2,true,1)
        val game = root.currentGame
        checkNotNull(game)
        val currentGame = game.currentGameState

        assertEquals(2,currentGame.playerList.size)
        assertEquals(36,currentGame.board.levelOneCards.size)
        assertEquals(4,currentGame.board.levelOneOpen.size)
        assertEquals(26,currentGame.board.levelTwoCards.size)
        assertEquals(4,currentGame.board.levelTwoOpen.size)
        assertEquals(16,currentGame.board.levelThreeCards.size)
        assertEquals(4,currentGame.board.levelThreeOpen.size)
        assertEquals(3,currentGame.board.nobleTiles.size)
        assertEquals(1,game.simulationSpeed)

        root.gameService.startNewGame(playerList3,false,1)
        val newGame = root.currentGame
        checkNotNull(newGame)
        assertEquals(3,newGame.currentGameState.playerList.size)
        assertEquals("p1", newGame.currentGameState.playerList[0].name)

        assertThrows<IllegalArgumentException> {
            root.gameService.startNewGame(playerList1,false,1)
        }
        assertThrows<IllegalArgumentException> {
            root.gameService.startNewGame(playerList5,false,1)
        }

    }

    /** tests if isCardAcquirable works correctly */
    @Test
    fun testIsCardAcquirable() {
        /** tests whether payment for a given card is correctly recognized as valid or invalid */
        val devCardOne = DevCard(id = 2, price = mutableMapOf(GemType.GREEN to 2, GemType.RED to 3),1,
            bonus = GemType.BLACK, prestigePoints = 0)
        val validPaymentWithoutJoker = mapOf(GemType.YELLOW to 0, GemType.GREEN to 2, GemType.RED to 3)
        val validPaymentWithJoker = mapOf(GemType.YELLOW to 2, GemType.GREEN to 1, GemType.RED to 2)
        val invalidPayment = mapOf(GemType.YELLOW to 1, GemType.GREEN to 2, GemType.RED to 1)
        assertEquals(true, root.gameService.isCardAcquirable(devCardOne, validPaymentWithoutJoker))
        assertEquals(true, root.gameService.isCardAcquirable(devCardOne, validPaymentWithJoker))
        assertEquals(false, root.gameService.isCardAcquirable(devCardOne, invalidPayment))
    }

    /** tests if one createCard works correctly */
    @Test
    fun testCreateCard() {
        assertThrows<IllegalArgumentException> {
            root.gameService.createCard(listOf( "0", "0", "0", "0", "2", "1", "0", "1", "diamante"))
        }
    }

    /** tests if nextPlayer works correctly */
    @Test
    fun testNextPlayer() {
        val playerList2 = listOf(Pair("p1",PlayerType.HUMAN),Pair("p2",PlayerType.HUMAN))

        root.gameService.startNewGame(playerList2,false,1)
        val game = root.currentGame
        checkNotNull(game)
        val currentGame = game.currentGameState
        val board = currentGame.board

        currentGame.playerList[1].reservedCards = mutableListOf(
            root.gameService.createCard(listOf( "0", "0", "0", "0", "2", "1", "0", "1", "diamant")),
            root.gameService.createCard(listOf( "1", "1", "0", "0", "0", "2", "0", "1", "saphir")),
            root.gameService.createCard(listOf( "2", "2", "1", "0", "0", "0", "0", "1", "smaragd"))
        )
        currentGame.playerList[1].score = 15
        for (gem in currentGame.board.gems) {
            gem.setValue(0)
        }

        root.gameService.nextPlayer()
        assertEquals(1,root.gameService.currentPlayerIndex)
        assertEquals(1,root.gameService.consecutiveNoAction)
        assertEquals("p2",currentGame.playerList[0].name)

        currentGame.playerList[1].score = 14
        root.gameService.nextPlayer()
        assertEquals("p2",currentGame.playerList[0].name)

        root.gameService.consecutiveNoAction = 0
        root.gameService.nextPlayer()
        val newGame = root.currentGame
        checkNotNull(newGame)
        val newBoard = newGame.currentGameState.board
        val newPlayerList = newGame.currentGameState.playerList

        assertEquals(board.levelOneCards[0].id,newBoard.levelOneCards[0].id)
        assertEquals("p2",newPlayerList[0].name)
    }

    /** tests if checkGems works correctly */
    @Test
    fun testCheckGems() {
        val playerList = listOf(Pair("p1", PlayerType.HUMAN), Pair("p2", PlayerType.HUMAN))
        root.gameService.startNewGame(playerList, false, 1)
        val player1 = root.currentGame!!.currentGameState.currentPlayer

        /** currentPlayer has less or equal than ten gems */
        assertEquals(false, root.gameService.checkGems())

        /** currentPlayer has more than ten gems */
        player1.gems[GemType.BLUE] = player1.gems.getValue(GemType.BLUE) + 11
        assertEquals(true, root.gameService.checkGems())
    }
}