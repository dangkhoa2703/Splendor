package entity

import kotlin.test.*

/**
 *  test class for objects of (not enum) classes
 * */
class EntityTests
{
    /** initialize objects */
    private val playerOne = Player(name = "Olaf", playerType = PlayerType.HUMAN)
    private val playerTwo = Player(name = "Mirco", playerType = PlayerType.EASY)
    private val board = Board()
    private val gameStateOne = GameState(playerOne, listOf(playerOne, playerTwo), board)
    private val highscoreOne = Highscore(playerOne.name, playerOne.score)
    private val splendor = Splendor(simulationSpeed = 2, currentGameState = gameStateOne,
        highscores = mutableListOf(highscoreOne), validGame = true)

    @Test
    fun testPlayer()
    {
        /** test if player got initialized correctly */
        assertEquals("Olaf", playerOne.name)
        assertEquals(0, playerOne.score)
        assertEquals(0, playerOne.gems[GemType.RED])
        assertEquals(0, playerOne.gems[GemType.BLUE])
        assertEquals(PlayerType.HUMAN, playerOne.playerType)
        assertEquals(PlayerType.EASY, playerTwo.playerType)
        assertEquals(mutableListOf(), playerOne.nobleTiles)
        assertEquals(mutableListOf(), playerOne.reservedCards)
        assertEquals(mutableListOf(), playerOne.devCards)

        /** change values of player's attributes */
        playerOne.score = 4
        val type = mutableListOf(GemType.YELLOW, GemType.BLUE)
        playerOne.gems = playerOne.gems + playerOne.gems.filterKeys { it in type }.mapValues { it.value + 1 }
        playerOne.bonus = playerOne.bonus + playerOne.bonus.filterKeys { it == GemType.RED }.mapValues { it.value + 1 }

        /** test if player's attributes can be changed correctly */
        assertEquals(4, playerOne.score)
        assertEquals(1, playerOne.gems[GemType.YELLOW])
        assertEquals(1, playerOne.gems[GemType.BLUE])
        assertEquals(1, playerOne.bonus[GemType.RED])
    }

    @Test
    fun boardTest()
    {
        /** test if board got initialized correctly */
        assertEquals(listOf(), board.nobleTiles)
        assertEquals(mutableListOf(), board.levelOneCards)
        assertEquals(mutableListOf(), board.levelOneOpen)
        assertEquals(mutableListOf(), board.levelTwoCards)
        assertEquals(mutableListOf(), board.levelTwoOpen)
        assertEquals(mutableListOf(), board.levelThreeCards)
        assertEquals(mutableListOf(), board.levelThreeOpen)
        assertEquals(6, board.gems.size)
    }

    @Test
    fun testGameState()
    {
        /** test if gameState got initialized correctly */
        assertEquals(playerOne, gameStateOne.currentPlayer)
        assertEquals(board, gameStateOne.board)
        assertEquals(2, gameStateOne.playerList.size)
        assertEquals(gameStateOne, gameStateOne.previous)
        assertEquals(gameStateOne, gameStateOne.next)
        assertEquals(false, gameStateOne.hasNext())
        assertEquals(false, gameStateOne.hasPrevious())
    }

    @Test
    fun splendorTest()
    {
        /** test if splendor got initialized correctly */
        assertEquals(2, splendor.simulationSpeed)
        assertEquals(gameStateOne, splendor.currentGameState)
        assertEquals(1, splendor.highscores.size)
        assertEquals(true, splendor.validGame)
        assertEquals(playerOne.name, highscoreOne.playerName)
        assertEquals(playerOne.score, highscoreOne.score)
    }

    @Test
    fun gemTest()
    {
        val gemRed = Gem(GemType.RED)
        assertEquals(GemType.RED, gemRed.gemType)
        assertNotNull(gemRed.toString())
    }
}