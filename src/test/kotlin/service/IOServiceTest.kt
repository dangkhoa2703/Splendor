package service

import entity.Highscore
import entity.PlayerType
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

class IOServiceTest {

    /** RootService reference */
    private var root = RootService()

    /** tests if saving and loading games works correctly */
    @Test
    fun testSaveAndLoadGame() {
        val playerList2 = listOf(Pair("p1", PlayerType.HUMAN), Pair("p2", PlayerType.HUMAN))
        root.gameService.startNewGame(playerList2, false, 1)
        val game = root.currentGame
        checkNotNull(game)
        val currentGame = game.currentGameState

        currentGame.board.levelOneCards.clear()
        currentGame.board.levelOneCards.add(
            root.gameService.createCard(listOf("1", "2", "3", "0", "0", "0", "0", "0", "diamant")))
        currentGame.currentPlayer.score = 11
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

    /** tests if saving and loading highscores works correctly */
    @Test
    fun testSaveAndLoadHighscore(){
        val highscore1 = Highscore("Dumbledore",100)
        val highscore2 = Highscore("Spider-man",99)
        val highscore3 = Highscore("BATMAN",10)
        val highscore4 = Highscore("N00bMaster69",69)
        val highscore5 = Highscore("Tifa Lockhart", 20)
        val highscore6 = Highscore(":3",30)

        root.ioService.saveHighscore(highscore1)
        root.ioService.saveHighscore(highscore2)
        root.ioService.saveHighscore(highscore3)
        root.ioService.saveHighscore(highscore4)
        root.ioService.saveHighscore(highscore5)
        root.ioService.saveHighscore(highscore6)

        val highscoreList = root.ioService.loadHighscore()
        assertEquals("Dumbledore",highscoreList[0].playerName)
        assertEquals(10,highscoreList[5].score)
    }
}