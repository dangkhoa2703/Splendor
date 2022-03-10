package service

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
        val playerList4 = listOf(
            Pair("p1",PlayerType.HUMAN),
            Pair("p2",PlayerType.HUMAN),
            Pair("p3",PlayerType.HUMAN),
            Pair("p4",PlayerType.HUMAN))
        val playerList5 = listOf(
            Pair("p1",PlayerType.HUMAN),
            Pair("p2",PlayerType.HUMAN),
            Pair("p3",PlayerType.HUMAN),
            Pair("p4",PlayerType.HUMAN),
            Pair("p5",PlayerType.HUMAN))

        val root = RootService()
        root.gameService.startNewGame(playerList2,true,1)
        val game = root.currentGame
        checkNotNull(game)
        val currentGame = game.currentGameState

        assertEquals("p2",currentGame.playerList[0].name)
        assertEquals(36,currentGame.board.levelOneCards.size)
        assertEquals(4,currentGame.board.levelOneOpen.size)
        assertEquals(26,currentGame.board.levelTwoCards.size)
        assertEquals(4,currentGame.board.levelTwoOpen.size)
        assertEquals(16,currentGame.board.levelThreeCards.size)
        assertEquals(4,currentGame.board.levelThreeOpen.size)
        assertEquals(3,currentGame.board.nobleTiles.size)
        assertEquals(1,game.simulationSpeed)

//        val root3 = RootService()
//        root.gameService.startNewGame(playerList3,false,1)
//        val game3 = root.currentGame
//        checkNotNull(game)
//        val currentGame3 = game.currentGameState

        assertThrows<IllegalArgumentException> {
            root.gameService.startNewGame(playerList1,false,1)
        }
        assertThrows<IllegalArgumentException> {
            root.gameService.startNewGame(playerList5,false,1)
        }

    }

    /** tests if isCardAcquirable works correctly */
    @Test
    fun testIsCardAcquirable()
    {
        /** tests whether payment for a given card is correctly recognized as valid or invalid */
        val devCardOne = DevCard(id = 2, price = mutableMapOf(GemType.GREEN to 2, GemType.RED to 3),
            bonus = GemType.BLACK, prestigePoints = 0)
        val validPaymentWithoutJoker = mapOf(GemType.YELLOW to 0, GemType.GREEN to 2, GemType.RED to 3)
        val validPaymentWithJoker = mapOf(GemType.YELLOW to 2, GemType.GREEN to 1, GemType.RED to 2)
        val invalidPayment = mapOf(GemType.YELLOW to 1, GemType.GREEN to 2, GemType.RED to 1)
        assertEquals(true, root.gameService.isCardAcquirable(devCardOne, validPaymentWithoutJoker))
        assertEquals(true, root.gameService.isCardAcquirable(devCardOne, validPaymentWithJoker))
        assertEquals(false, root.gameService.isCardAcquirable(devCardOne, invalidPayment))
    }
}