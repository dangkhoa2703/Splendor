package service

import entity.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.*

/**
 *  test class for playerActionService
 * */
class PlayerActionServiceTest {

    /** initialize objects */
    private val root = RootService()
    private val playerOne = Player(name = "Olaf", playerType = PlayerType.HUMAN)
    private val playerTwo = Player(name = "Mirco", playerType = PlayerType.EASY)
    private val playerList = listOf(Pair("name1", PlayerType.HUMAN), Pair("name2", PlayerType.EASY))
    private var gemMap = mutableMapOf(GemType.RED to 2, GemType.GREEN to 3, GemType.WHITE to 3, GemType.BLACK to 4,
        GemType.BLUE to 3, GemType.YELLOW to 1)

    /**
     * tests if takeGems(types) works correct
     */
    @Test
    fun takeGemsTest() {
        root.gameService.startNewGame(playerList, false, 1)
        assertNotNull(root.currentGame)
        val types = mutableListOf(GemType.RED, GemType.BLUE, GemType.BLACK, GemType.GREEN)

        //Exception:
        //exception if types is too long
        var user = root.currentGame!!.currentGameState.currentPlayer
        assertThrows<IllegalArgumentException> { root.playerActionService.takeGems(types, user) }
        root.currentGame!!.currentGameState.board.gems = mutableMapOf(GemType.RED to 3, GemType.GREEN to 2,
            GemType.WHITE to 0, GemType.BLACK to 0, GemType.BLUE to 2, GemType.YELLOW to 0)
        types.remove(GemType.BLACK)
        types.remove(GemType.GREEN)
        //exception if board has at least three different types and chosen gemTypes are two different ones or only one
        assertThrows<IllegalArgumentException> { root.playerActionService.takeGems(types, user) }
        types.add(GemType.RED)
        //exception if three chosen gems contain less than three different GemTypes
        assertThrows<IllegalArgumentException> { root.playerActionService.takeGems(types, user) }
        types.remove(GemType.BLUE)
        //exception if less than four gems of GemType are left
        assertThrows<IllegalArgumentException> { root.playerActionService.takeGems(types, user) }

        //Functionality:
        //take two same gems
        root.currentGame!!.currentGameState.board.gems[GemType.RED] = 4
        val playersGemCountRed = root.currentGame!!.currentGameState.currentPlayer.gems.getValue(GemType.RED)
        root.playerActionService.takeGems(types, user)
        assertEquals(2, root.currentGame!!.currentGameState.board.gems[GemType.RED])
        assertEquals(playersGemCountRed + 2, user.gems[GemType.RED])
        //take three different gems
        types.removeAt(0)
        types.add(GemType.GREEN)
        types.add(GemType.BLUE)
        user = root.currentGame!!.currentGameState.currentPlayer
        root.playerActionService.takeGems(types, user)
        assertEquals(1, root.currentGame!!.currentGameState.board.gems[GemType.RED])
        assertEquals(1, root.currentGame!!.currentGameState.board.gems[GemType.BLUE])
        assertEquals(1, root.currentGame!!.currentGameState.board.gems[GemType.GREEN])
        assertEquals(1, user.gems[GemType.RED])
        assertEquals(1, user.gems[GemType.BLUE])
        assertEquals(1, user.gems[GemType.GREEN])
    }

    /**
     * tests if cards from a level stack are reserved correct
     */
    @Test
    fun reserveCardFromStackTest() {
        root.gameService.startNewGame(playerList, false, 1)
        val devCard1 = DevCard(0, gemMap, 1, 0, GemType.BLUE)
        val devCard2 = DevCard(0, gemMap, 2, 0, GemType.RED)
        val devCard3 = DevCard(0, gemMap, 3, 0, GemType.RED)
        var player = root.currentGame!!.currentGameState.currentPlayer
        root.currentGame!!.currentGameState.board.levelOneCards.clear()
        root.currentGame!!.currentGameState.board.levelOneCards.add(devCard1)
        root.currentGame!!.currentGameState.board.levelTwoCards.clear()
        root.currentGame!!.currentGameState.board.levelTwoCards.add(devCard2)
        root.currentGame!!.currentGameState.board.levelThreeCards.clear()
        root.currentGame!!.currentGameState.board.levelThreeCards.add(devCard3)
        root.currentGame!!.currentGameState.board.gems[GemType.YELLOW] = 2
        //reserveCard:
        root.playerActionService.reserveCard(devCard1, 0, player)
        assertFalse(root.currentGame!!.currentGameState.board.levelOneCards.contains(devCard1))
        assertTrue(player.reservedCards.contains(devCard1))
        assertTrue { root.currentGame!!.currentGameState.board.gems[GemType.YELLOW] == 1 }
        assertTrue { player.gems[GemType.YELLOW] == 1 }
        player = root.currentGame!!.currentGameState.currentPlayer
        root.playerActionService.reserveCard(devCard2, 0, player)
        assertFalse(root.currentGame!!.currentGameState.board.levelTwoCards.contains(devCard2))
        assertTrue(player.reservedCards.contains(devCard2))
        player = root.currentGame!!.currentGameState.currentPlayer
        root.playerActionService.reserveCard(devCard3, 0, player)
        assertFalse(root.currentGame!!.currentGameState.board.levelThreeCards.contains(devCard3))
        assertTrue(player.reservedCards.contains(devCard3))
        assertTrue { root.currentGame!!.currentGameState.board.gems.getValue(GemType.YELLOW) == 0 }
        assertTrue { player.gems.getValue(GemType.YELLOW)  == 1 }
    }
    /**
    /**
     * tests if open cards are reserved correct
    */
    @Test
    fun reserveCardFromOpenCardsTest() {
    root.gameService.startNewGame(playerList, false, 1)
    var player = root.currentGame!!.currentGameState.currentPlayer
    val devCard1 = DevCard(0, gemMap, 1, 0, GemType.BLUE)
    val devCard2 = DevCard(0, gemMap, 2, 0, GemType.RED)
    val devCard3 = DevCard(0, gemMap, 3, 0, GemType.RED)
    val devCard4 = DevCard(0, gemMap, 3, 1, GemType.RED)
    val devCard5 = DevCard(0, gemMap, 3, 1, GemType.RED)
    val board = root.currentGame!!.currentGameState.board
    board.levelOneOpen.clear()
    board.levelOneOpen.add(devCard1)
    board.levelTwoOpen.clear()
    board.levelTwoOpen.add(devCard2)
    board.levelThreeOpen.clear()
    board.levelThreeOpen.add(devCard3)
    board.levelThreeOpen.add(devCard4)
    board.gems[GemType.YELLOW] = 2
    //reserveCard:
    root.playerActionService.reserveCard(devCard1, 0, player)
    assertNotEquals(devCard1, root.currentGame!!.currentGameState.board.levelOneOpen[0])
    assertTrue(player.reservedCards.contains(devCard1))
    assertTrue { root.currentGame!!.currentGameState.board.levelOneOpen.size == 4 }
    assertTrue { root.currentGame!!.currentGameState.board.gems[GemType.YELLOW] == 1 }
    assertTrue { player.gems.getValue(GemType.YELLOW) == 1 }
    player = root.currentGame!!.currentGameState.currentPlayer
    root.playerActionService.reserveCard(devCard2, 0, player)
    assertNotEquals(devCard2, root.currentGame!!.currentGameState.board.levelTwoOpen[0])
    assertTrue(player.reservedCards.contains(devCard2))
    assertTrue { root.currentGame!!.currentGameState.board.levelTwoOpen.size == 1 }
    player = root.currentGame!!.currentGameState.currentPlayer
    root.playerActionService.reserveCard(devCard3, 0, player)
    assertNotEquals(devCard3, root.currentGame!!.currentGameState.board.levelThreeOpen[0])
    assertTrue(player.reservedCards.contains(devCard3))
    assertTrue { root.currentGame!!.currentGameState.board.levelThreeOpen.size == 2 }
    assertTrue { root.currentGame!!.currentGameState.board.gems.getValue(GemType.YELLOW) == 0 }
    assertNotNull(root.currentGame!!.currentGameState.board.gems)
    assertTrue { player.gems.getValue(GemType.YELLOW) == 1 }

    assertThrows<IllegalArgumentException> { root.playerActionService.reserveCard(devCard4, 0, player) }

    root.gameService.startNewGame(playerList, false, 1)
    player = root.currentGame!!.currentGameState.currentPlayer
    root.currentGame!!.currentGameState.board.gems[GemType.YELLOW] = 0
    root.playerActionService.reserveCard(devCard5, index = 0, player)
    assertEquals(0, player.gems[GemType.YELLOW])
    }

    /**
     * tests if card is bought correct
    */
    @Test
    fun buyCardTest() {
    root.gameService.startNewGame(playerList, false, 1)
    assertNotNull(root.currentGame)
    gemMap = mutableMapOf(GemType.RED to 2, GemType.GREEN to 3)
    val devCard1 = DevCard(0, gemMap, 1, 1, GemType.BLUE)
    val devCard2 = DevCard(0, gemMap, 2, 1, GemType.RED)
    val devCard3 = DevCard(0, gemMap, 3, 1, GemType.RED)
    val board = root.currentGame!!.currentGameState.board
    var player = root.currentGame!!.currentGameState.currentPlayer
    player.gems[GemType.RED] = 3
    player.gems[GemType.GREEN] = 3
    board.gems[GemType.RED] = 0
    board.gems[GemType.GREEN] = 0
    val score = player.score
    player.bonus[GemType.BLUE] = 0

    //exception if card is not acquirable
    val map = mutableMapOf(GemType.RED to 0, GemType.GREEN to 0, GemType.WHITE to 0, GemType.BLACK to 0,
    GemType.BLUE to 0, GemType.YELLOW to 0)
    assertThrows<IllegalArgumentException> { root.playerActionService.buyCard(devCard1, true, map,
    0, player) }

    board.levelOneOpen.clear()
    board.levelOneOpen.add(devCard1)
    board.levelTwoOpen.clear()
    board.levelTwoOpen.add(devCard2)
    board.levelThreeOpen.clear()
    board.levelThreeOpen.add(devCard3)

    //buy card from board level 1
    root.playerActionService.buyCard(devCard1, true, player.gems, 0, player)
    assertTrue { player.devCards.contains(devCard1) }
    assertFalse { board.levelOneOpen.contains(devCard1) }
    assertTrue { board.levelOneOpen.size == 1 }
    assertEquals(score + 1, player.score)
    assertEquals(1, player.bonus[GemType.BLUE])
    assertEquals(2, board.gems[GemType.RED])
    assertEquals(3, board.gems[GemType.GREEN])
    assertEquals(1, player.gems[GemType.RED])
    assertEquals(0, player.gems[GemType.GREEN])

    //buy ard from other levels or reserved cards
    player = root.currentGame!!.currentGameState.currentPlayer
    root.playerActionService.buyCard(devCard2, true, gemMap, 0, player)
    assertFalse { board.levelOneOpen.contains(devCard2) }
    player = root.currentGame!!.currentGameState.currentPlayer
    root.playerActionService.buyCard(devCard3, true, gemMap, 0, player)
    assertFalse { board.levelOneOpen.contains(devCard3) }
    player.devCards.remove(devCard1)
    player.reservedCards.add(devCard1)
    player = root.currentGame!!.currentGameState.currentPlayer
    root.playerActionService.buyCard(devCard1, false, gemMap, 0, player)
    assertFalse { player.reservedCards.contains(devCard1) }
    } */

    /**
     * tests if nobleTiles are returned correct
     */
    @Test
    fun selectNobleTileTest() {
        root.gameService.startNewGame(playerList, false, 1)
        assertNotNull(root.currentGame)
        val player = root.currentGame!!.currentGameState.currentPlayer
        val nobleTileOne = NobleTile(0, gemMap, 1)
        root.currentGame!!.currentGameState.board.nobleTiles.add(nobleTileOne)
        val score = root.currentGame!!.currentGameState.currentPlayer.score
        assertFalse { root.currentGame!!.currentGameState.currentPlayer.nobleTiles.contains(nobleTileOne) }
        root.playerActionService.selectNobleTile(nobleTileOne, player)
        assertTrue { player.nobleTiles.contains(nobleTileOne) }
        assertFalse { root.currentGame!!.currentGameState.board.nobleTiles.contains(nobleTileOne) }
        assertEquals(score + 1, player.score)
    }

    /**
     * test to check if gems are returned correct
     */
    @Test
    fun returnGemTest() {
        root.gameService.startNewGame(playerList, false, 1)
        assertNotNull(root.currentGame)
        val gemTypeList = listOf(GemType.RED, GemType.BLUE)
        //set number of Players Gems
        val player = root.currentGame!!.currentGameState.currentPlayer
        player.gems.clear()
        player.gems[GemType.RED] = 2
        player.gems[GemType.BLUE] = 3
        //get numbers of gems on the board
        val numberRedGemsOnBoard = root.currentGame!!.currentGameState.board.gems.getValue(GemType.RED)
        val numberBlueGemsOnBoard = root.currentGame!!.currentGameState.board.gems.getValue(GemType.BLUE)
        //check if changes are correct
        root.playerActionService.returnGems(gemTypeList, root.currentGame!!.currentGameState.currentPlayer)
        assertEquals(numberRedGemsOnBoard + 1, root.currentGame!!.currentGameState.board.gems[GemType.RED])
        assertEquals(numberBlueGemsOnBoard + 1, root.currentGame!!.currentGameState.board.gems[GemType.BLUE])
        assertEquals(1, player.gems[GemType.RED])
        assertEquals(2, player.gems[GemType.BLUE])
    }

    /**
     * tests if undo and redo work
     */
    @Test
    fun undoRedoTest() {
        val gameStateOne = GameState(playerTwo, listOf(playerOne, playerTwo), Board())
        val gameStateTwo = GameState(playerTwo, listOf(playerOne, playerTwo), Board())
        root.gameService.startNewGame(playerList, false, 1)
        //exceptions if there are no next or previous gameStates
        assertThrows<IllegalStateException> { root.playerActionService.redo() }
        assertThrows<IllegalStateException> { root.playerActionService.undo() }
        assertTrue { root.currentGame!!.validGame }
        root.currentGame!!.currentGameState.next = gameStateTwo
        root.currentGame!!.currentGameState.previous = gameStateOne
        gameStateOne.next = root.currentGame!!.currentGameState
        //test for undo
        root.playerActionService.undo()
        assertEquals(gameStateOne, root.currentGame!!.currentGameState)
        assertFalse { root.currentGame!!.validGame }
        //test for redo
        root.currentGame!!.validGame = true
        root.playerActionService.redo()
        root.playerActionService.redo()
        assertEquals(gameStateTwo, root.currentGame!!.currentGameState)
        assertFalse { root.currentGame!!.validGame }
    }
}