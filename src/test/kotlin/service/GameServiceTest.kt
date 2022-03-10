package service

import entity.PlayerType
import org.junit.jupiter.api.assertThrows
import kotlin.test.*

class GameServiceTest {

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


}