package service

import entity.PlayerType
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

class IOServiceTest {

    /** RootService reference */
    private var root = RootService()
    val playerList2 = listOf(Pair("p1", PlayerType.HUMAN), Pair("p2", PlayerType.HUMAN))

    @Test
    fun testSaveAndLoadGame() {
        val playerList2 = listOf(Pair("p1", PlayerType.HUMAN), Pair("p2", PlayerType.HUMAN))
        root.gameService.startNewGame(playerList2, false, 1)
        val game = root.currentGame
        checkNotNull(game)
        val currentGame = game.currentGameState
        val player = currentGame.currentPlayer

        currentGame.board.levelOneCards.clear()
        currentGame.board.levelOneCards.add(
            root.gameService.createCard(
                listOf("1", "2", "3", "0", "0", "0", "0", "0", "diamant")
            )
        )
        currentGame.currentPlayer.score = 11
        val nobleTileId = game.currentGameState.board.nobleTiles[0].id
        game.currentGameState.board.nobleTiles.removeAt(0)

        //test save file
        root.ioService.saveGame("src/main/resources/saveFile1")
        val boardConfig = File("src/main/resources/saveFile1/board").readLines()
        val player1Config = File("src/main/resources/saveFile1/player1").readLines()

        assertEquals("[1]", boardConfig[1])
        assertEquals(11, player1Config[6].trim().toInt())

        //test load game
        root.ioService.loadGame("src/main/resources/saveFile1")
        val loadGame = root.currentGame
        checkNotNull(loadGame)

        assertEquals(0,root.gameService.currentPlayerIndex)
        assertEquals(11,loadGame.currentGameState.currentPlayer.score)
        assertEquals(1,loadGame.currentGameState.board.levelOneCards[0].id)

    }

    @Test
    fun testSaveAndLoadHighScore(){

    }
}