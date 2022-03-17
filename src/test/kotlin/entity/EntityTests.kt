package entity

import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.*

/**
 *  test class for objects of entity classes
 * */
class EntityTests
{

    /** initialize objects */
    private val playerOne = Player(name = "Olaf", playerType = PlayerType.HUMAN)
    private val playerTwo = Player(name = "Mirco", playerType = PlayerType.EASY)
    private val board = Board()
    private val gameStateOne = GameState(playerOne, listOf(playerOne, playerTwo), board)
    private val highscoreOne = Highscore(playerOne.name, 30.6)
    private var splendor = Splendor(simulationSpeed = 2, currentGameState = gameStateOne,
        highscores = mutableListOf(highscoreOne))
    private val gemMap = mutableMapOf(GemType.RED to 2, GemType.GREEN to 3, GemType.WHITE to 3, GemType.BLACK to 4,
        GemType.BLUE to 3, GemType.YELLOW to 1)

    /** tests if Player objects can be created correctly */
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
        type.forEach { playerOne.gems[it] = playerOne.gems.getValue(it) + 1 }
        playerOne.bonus[GemType.RED] = playerOne.bonus.getValue(GemType.RED) + 1

        /** test if player's attributes can be changed correctly */
        assertEquals(4, playerOne.score)
        assertEquals(1, playerOne.gems[GemType.YELLOW])
        assertEquals(1, playerOne.gems[GemType.BLUE])
        assertEquals(1, playerOne.bonus[GemType.RED])

        playerOne.devCards = mutableListOf(DevCard(1, mapOf(GemType.RED to 4), 2, 1, GemType.WHITE))
        assertEquals(2, playerOne.devCards[0].level)
    }

    /** tests if Board objects can be created correctly */
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

        /** set and check gems */
        board.gems = gemMap
        assertEquals(gemMap[GemType.YELLOW], board.gems[GemType.YELLOW])
    }

    /** tests if GameState objects can be created correctly */
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

        /** test if gameStates connections can be changed correctly */
        val gameStateTwo = GameState(playerOne, listOf(playerOne, playerTwo), board)
        gameStateOne.next = gameStateTwo
        gameStateOne.previous = gameStateTwo
        assertEquals(true, gameStateOne.hasNext())
        assertEquals(true, gameStateOne.hasPrevious())
        assertEquals(gameStateTwo, gameStateOne.next)
        assertEquals(gameStateTwo, gameStateOne.previous)
    }

    /** tests if Splendor objects can be created correctly */
    @Test
    fun splendorTest()
    {
        /** test if splendor got initialized correctly */
        splendor = Splendor(simulationSpeed = 2, currentGameState = gameStateOne,
            highscores = mutableListOf(highscoreOne), validGame = true)
        assertEquals(2, splendor.simulationSpeed)
        assertEquals(gameStateOne, splendor.currentGameState)
        assertEquals(1, splendor.highscores.size)
        assertEquals(true, splendor.validGame)
        assertEquals(playerOne.name, highscoreOne.playerName)

        /** test if splendor's attributes can be changed correctly */
        splendor.simulationSpeed = 3
        splendor.validGame = false
        val gameStateTwo = GameState(playerOne, listOf(playerOne, playerTwo), board)
        splendor.currentGameState = gameStateTwo
        assertEquals(3, splendor.simulationSpeed)
        assertEquals(gameStateTwo, splendor.currentGameState)
        assertEquals(false, splendor.validGame)

        splendor = Splendor(simulationSpeed = 2, currentGameState = gameStateOne, validGame = true)
        assertEquals(mutableListOf(), splendor.highscores)
    }

    /** tests if Gem objects can be created correctly */
    @Test
    fun gemTest()
    {
        /** test if gem got initialized correctly */
        val gemRed = Gem(GemType.RED)
        assertEquals(GemType.RED, gemRed.gemType)
        assertEquals(4, gemRed.gemType.toInt())
        assertEquals(3, Gem(GemType.GREEN).gemType.toInt())
        assertEquals(2, Gem(GemType.BLUE).gemType.toInt())
        assertEquals(1, Gem(GemType.WHITE).gemType.toInt())
        assertEquals(5, Gem(GemType.BLACK).gemType.toInt())
        assertEquals(6, Gem(GemType.YELLOW).gemType.toInt())
        assertEquals(GemType.WHITE, gemRed.gemType.gemTypeFromInt(1))
        assertEquals(GemType.BLUE, gemRed.gemType.gemTypeFromInt(2))
        assertEquals(GemType.GREEN, gemRed.gemType.gemTypeFromInt(3))
        assertEquals(GemType.RED, gemRed.gemType.gemTypeFromInt(4))
        assertEquals(GemType.BLACK, gemRed.gemType.gemTypeFromInt(5))
        assertEquals(GemType.YELLOW, gemRed.gemType.gemTypeFromInt(6))
        assertEquals(null, gemRed.gemType.gemTypeFromInt(7))
    }

    /** tests if Card objects can be created correctly */
    @Test
    fun cardTest()
    {
        /** test if card got initialized correctly */
        val map = mapOf( GemType.BLUE to 2)
        val devCard0 = DevCard(0,map,1,2,GemType.RED)
        assertEquals(0,devCard0.id)
        assertEquals(1,devCard0.level)
        assertEquals(2,devCard0.prestigePoints)
        assertEquals(GemType.RED,devCard0.bonus)
        assertEquals(map, devCard0.price)

        /** test if card's attributes can be changed correctly */
        val nobleTile0 = NobleTile(0,map,1)
        assertEquals(0, nobleTile0.id)
        assertEquals(1,nobleTile0.prestigePoints)
        assertEquals(map,nobleTile0.condition)
    }

    /** tests if Turn objects can be created correctly */
    @Test
    fun testTurn()
    {
        /** test if turn got initialized correctly */
        val turnCard = listOf( DevCard(2, mapOf( GemType.RED to 3), 1,2,GemType.RED ))
        val turnOne = Turn(mapOf(GemType.RED to 3), turnCard, TurnType.BUY_CARD)
        assertEquals(mapOf(GemType.RED to 3), turnOne.gems)
        assertEquals(turnCard, turnOne.card)
        assertEquals(TurnType.BUY_CARD, turnOne.turnType)

        val turnTwo = Turn(mapOf(GemType.RED to 2), listOf(), TurnType.TAKE_GEMS)
        assertEquals(mapOf(GemType.RED to 2), turnTwo.gems)
        assertEquals(listOf(), turnTwo.card)
        assertEquals(TurnType.TAKE_GEMS, turnTwo.turnType)

        val turnThree = Turn(mapOf(GemType.RED to 1), listOf(), TurnType.TAKE_GEMS)
        assertEquals(mapOf(GemType.RED to 1), turnThree.gems)
        assertEquals(listOf(), turnThree.card)
        assertEquals(TurnType.TAKE_GEMS, turnThree.turnType)

        val turnFour = Turn(mapOf(), turnCard, TurnType.RESERVE_CARD)
        assertEquals(mapOf(), turnFour.gems)
        assertEquals(turnCard, turnFour.card)
        assertEquals(TurnType.RESERVE_CARD, turnFour.turnType)
    }

    /**
     * ImageLoader Test
     */
    @Test
    fun loadImage(){
        assertDoesNotThrow {  SplendorImageLoader().saveGameImage()}
        assertDoesNotThrow {  SplendorImageLoader().nextPlayersImage()}
        assertDoesNotThrow {  SplendorImageLoader().humanIcon()}
        assertDoesNotThrow {  SplendorImageLoader().highscores()}
        assertDoesNotThrow {  SplendorImageLoader().startBackground()}
        assertDoesNotThrow {  SplendorImageLoader().button()}
        assertDoesNotThrow {  SplendorImageLoader().redoButton()}
        assertDoesNotThrow {  SplendorImageLoader().hintButton()}
        assertDoesNotThrow {  SplendorImageLoader().undoButton()}
        assertDoesNotThrow {  SplendorImageLoader().table()}
        assertDoesNotThrow {  SplendorImageLoader().cardBack()}
        assertDoesNotThrow {  SplendorImageLoader().velocity(1)}
        assertDoesNotThrow {  SplendorImageLoader().velocity(2)}
        assertDoesNotThrow {  SplendorImageLoader().shuffleImage(0)}
        assertDoesNotThrow {  SplendorImageLoader().shuffleImage(1)}
        assertDoesNotThrow {  SplendorImageLoader().configBackground()}
        assertDoesNotThrow {  SplendorImageLoader().highscoreBackground()}
        assertDoesNotThrow {  SplendorImageLoader().tokenImage(1)}
        assertDoesNotThrow {  SplendorImageLoader().tokenImage(2)}
        assertDoesNotThrow {  SplendorImageLoader().tokenImage(3)}
        assertDoesNotThrow {  SplendorImageLoader().tokenImage(4)}
        assertDoesNotThrow {  SplendorImageLoader().tokenImage(5)}
        assertDoesNotThrow {  SplendorImageLoader().tokenImage(6)}
        assertDoesNotThrow { SplendorImageLoader().carbon() }
        assertDoesNotThrow { SplendorImageLoader().loadGame() }
        assertDoesNotThrow { SplendorImageLoader().tokenImage(1) }
        assertDoesNotThrow { SplendorImageLoader().preload() }
        assertDoesNotThrow { SplendorImageLoader().imageFor(11) }
        assertDoesNotThrow { SplendorImageLoader().image("/highscores.png") }
    }

}