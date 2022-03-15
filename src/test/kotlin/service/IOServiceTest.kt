package service

import entity.Highscore
import entity.PlayerType
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File
import kotlin.test.assertEquals

/**
 *  test class for IOService
 * */
class IOServiceTest {

    /** RootService reference */
    private var root = RootService()
    private val playerList = listOf(
        Pair("p1", PlayerType.HUMAN),
        Pair("p2", PlayerType.MEDIUM),
        Pair("p3", PlayerType.EASY),
        Pair("p4",PlayerType.HARD))

    /** tests if saving and loading games works correctly */
    @Test
    fun testSaveAndLoadGame() {

        root.gameService.startNewGame(playerList, false, 1)
        val game = root.currentGame
        checkNotNull(game)
        val currentGame = game.currentGameState
        val player = currentGame.currentPlayer

        root.gameService.nextPlayer()
        root.gameService.nextPlayer()

        //test save file
        root.ioService.saveGame("src/test/resources/testSaveFile")
        root.ioService.loadGame("src/test/resources/testSaveFile")

//        //test Gems exception
//        assertThrows<IllegalArgumentException> {
//
//        }
    }

    /** tests if saving and loading highscores works correctly */
    @Test
    fun testSaveAndLoadHighscore(){
        //save current highscores
        val highscoreFile = File("src/main/resources/highscore")
        val oldHighscores = root.ioService.loadHighscore()
        //test on empty file
        highscoreFile.bufferedWriter().use{out-> out.write("")}
        val highscore1 = Highscore("Dumbledore",100.0)
        val highscore2 = Highscore("Spider-man",99.0)
        val highscore3 = Highscore("BATMAN",10.0)
        val highscore4 = Highscore("N00bMaster69",69.69)
        val highscore5 = Highscore("Tifa Lockhart", 20.0)
        val highscore6 = Highscore(":3",30.0)
        val highscore7 = Highscore("Pikachu",30.0)
        val highscore8 = Highscore("BabyMetal",30.0)
        val highscore9 = Highscore("Olaf",30.0)
        val highscore10 = Highscore("Rambo",30.0)
        val highscore11 = Highscore(":3",30.0)

        root.ioService.saveHighscore(highscore1)
        root.ioService.saveHighscore(highscore2)
        root.ioService.saveHighscore(highscore3)
        root.ioService.saveHighscore(highscore4)
        root.ioService.saveHighscore(highscore5)
        root.ioService.saveHighscore(highscore6)
        root.ioService.saveHighscore(highscore7)
        root.ioService.saveHighscore(highscore8)
        root.ioService.saveHighscore(highscore9)
        root.ioService.saveHighscore(highscore10)
        root.ioService.saveHighscore(highscore11)

        val highscoreList = root.ioService.loadHighscore()
        assertEquals("Dumbledore",highscoreList[0].playerName)
        assertEquals(30.0,highscoreList[5].score)
        assertEquals(10,highscoreList.size)

        //recreate old file
        highscoreFile.bufferedWriter().use{out-> out.write("")}
        for (highscore in oldHighscores) {
            root.ioService.saveHighscore(highscore)
        }
    }
}