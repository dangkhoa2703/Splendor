package service

import entity.Highscore
import entity.PlayerType
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File
import java.io.FileWriter
import kotlin.test.assertEquals

class IOServiceTest {

    /** RootService reference */
    private var root = RootService()
    val playerList = listOf(
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

        currentGame.board.levelOneCards.clear()
        currentGame.board.levelOneCards.add(
            root.gameService.createCard(listOf("1", "2", "3", "0", "0", "0", "0", "0", "diamant")))
        currentGame.currentPlayer.score = 11
        val nobleTileId = game.currentGameState.board.nobleTiles[0].id
        game.currentGameState.board.nobleTiles.removeAt(0)

        //test save file
        root.ioService.saveGame("src/test/resources/testSaveFile")
        val boardConfig = File("src/test/resources/testSaveFile/board").readLines()
        val player1Config = File("src/test/resources/testSaveFile/player1").readLines()

        assertEquals("[1]", boardConfig[1])
        assertEquals(11, player1Config[6].trim().toInt())

        //test load game
        root.ioService.loadGame("src/test/resources/testSaveFile")
        val loadGame = root.currentGame
        checkNotNull(loadGame)

        assertEquals(0,root.gameService.currentPlayerIndex)
        assertEquals(11,loadGame.currentGameState.currentPlayer.score)
        assertEquals(1,loadGame.currentGameState.board.levelOneCards[0].id)

        assertThrows<IllegalArgumentException> {
            root.ioService.loadGame("Olaf")
        }

        //splendor exception test
        assertThrows<IllegalArgumentException> {
            val gameSettingName = "src/test/resources/testSaveFile/gameSetting"
            val gameFile = File(gameSettingName)
            gameFile.bufferedWriter().use{ out ->
                out.write(game.simulationSpeed.toString() + "\n" )
                out.write(game.currentGameState.playerList.size.toString() + "\n")
                out.write(root.gameService.currentPlayerIndex.toString() +"\n")
                out.write("Mario" + "\n")
                out.write(root.gameService.consecutiveNoAction.toString() + "\n")
            }
            root.ioService.loadGame("src/test/resources/testSaveFileFail")
        }

        //player's exception test
        assertThrows<IllegalArgumentException> {
            val playerFileName = "src/test/resources/testSaveFile/player1"
            val playerFile = File(playerFileName)
            playerFile.bufferedWriter().use { out ->
                out.write(player.name + "\n")
                out.write("Luigi" + "\n")
                out.write(player.gems.toString() + "\n")
                out.write(player.bonus.toString() + "\n")
                out.write("[]" + "\n")
                out.write("[]" + "\n")
                out.write(player.score.toString() + "\n")
                out.write(player.devCards.toString() + "\n")
            }
            root.ioService.loadGame("src/test/resources/testSaveFile")
        }

//        //test Gems exception
//        assertThrows<IllegalArgumentException> {
//
//        }
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

        val highscoreFile = File("src/main/resources/highscore")
        highscoreFile.bufferedWriter().use{out-> out.write("")}
    }
}