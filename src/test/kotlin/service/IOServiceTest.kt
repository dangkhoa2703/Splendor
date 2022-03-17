package service

import entity.*
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

        assertThrows<IllegalStateException> {
            root.ioService.saveGame("Raya")
        }

        root.gameService.startNewGame(playerList, false, 1)
        val game = root.currentGame
        checkNotNull(game)

        root.ioService.saveGame("src/test/resources/testSaveFile")
        game.currentGameState.playerList[3].score = 32
        game.currentGameState.playerList[1].nobleTiles.add(
            NobleTile(
                90,
                mapOf(GemType.RED to 3),
                2
            )
        )
        root.gameService.nextPlayer()
        game.currentGameState.currentPlayer.gems[GemType.RED] = 10
        game.currentGameState.playerList[2].devCards.add(
            DevCard(
                69,
                mapOf(GemType.BLACK to 4),
                2,
                1,
                GemType.WHITE
            )
        )
        root.gameService.nextPlayer()
        root.playerActionService.undo()
        root.playerActionService.redo()


        root.ioService.saveGame("src/test/resources/testSaveFile")

        //test save file
        val gameState2 = File("src/test/resources/testSaveFile/gameState2.txt").readLines()
        val gameSetting = File("src/test/resources/testSaveFile/gameSetting.txt").readLines()

        assertEquals("32",gameState2[36])
        assertEquals(10,gameState2[12].slice(5..6).toInt())
        assertEquals("false", gameSetting[4])

        root.ioService.loadGame("src/test/resources/testSaveFile")
        val loadGame = root.currentGame
        checkNotNull(loadGame)
        assertEquals(1,root.currentGame!!.currentGameState.currentPlayerIndex)
        assertEquals(32,loadGame.currentGameState.playerList[3].score)
        assertEquals(4,loadGame.currentGameState.board.levelOneOpen.size)

        //test Gems exception
        assertThrows<IllegalArgumentException> {
            root.ioService.loadGame("src/test/resources/testFail")
        }
    }

    /** tests if saving and loading highscores works correctly */
    @Test
    fun testSaveAndLoadHighscore(){
        //save current highscores
        val highscoreFile = File("src/main/resources/highscore")
        val oldHighscores = root.ioService.loadHighscore()
        highscoreFile.bufferedWriter().use{out-> out.write("")}
        val emptyScoreList = root.ioService.loadHighscore()
        assertEquals(0, emptyScoreList.size)
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

    @Test
    fun testCreatePlayerFromLines(){
        val fileLines = listOf<String>(
            "p4",
            "Nemo"
        )
        assertThrows<IllegalArgumentException> {
            root.ioService.createPlayerFromLines(fileLines)
        }
    }

    @Test
    fun testReadDevCard(){
        val testDevCardString = "0, 0, 1, 1, 1, 1, 0, 1, diamante"
        val file = File("src/main/resources/splendor-entwicklungskarten.csv")
        val originalCardFileString = file.readLines()

        file.bufferedWriter().use { out ->
            out.write(testDevCardString)
        }

        assertThrows<IllegalArgumentException> {
            root.ioService.readDevCards(listOf("-1"))
        }

        file.bufferedWriter().use { out ->
            originalCardFileString.forEach {
                out.write(it + "\n")
            }
        }
    }
}