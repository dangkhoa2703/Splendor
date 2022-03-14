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
    private val playerList = listOf(Pair("name1",PlayerType.HUMAN),Pair("name2",PlayerType.EASY))
    private var gemMap = mutableMapOf(
        GemType.RED to 2, GemType.GREEN to 3, GemType.WHITE to 3, GemType.BLACK to 4,
        GemType.BLUE to 3, GemType.YELLOW to 1)

    /**
     * tests if takeGems(types) works correct
     */
    @Test
    fun takeGemsTest() {
        val game = root.currentGame
        root.gameService.startNewGame(playerList, false, 1)
        if(game != null) {
            assertThrows<IllegalStateException> {
                root.playerActionService.takeGems(mutableListOf(GemType.RED), game.currentGameState.currentPlayer) }
            root.gameService.startNewGame(playerList, false, 1)
            assertNotNull(root.currentGame)
            val types = mutableListOf(GemType.RED, GemType.BLUE, GemType.BLACK, GemType.GREEN)

            //Exception:
            //exception if types is too long
            assertThrows<IllegalArgumentException> {
                root.playerActionService.takeGems(types, game.currentGameState.currentPlayer) }
            val boardGems = root.currentGame!!.currentGameState.board.gems
            boardGems.clear()
            boardGems[GemType.RED] = 3
            boardGems[GemType.BLUE] = 2
            boardGems[GemType.GREEN] = 2
            boardGems[GemType.YELLOW] = 0
            types.remove(GemType.BLACK)
            types.remove(GemType.GREEN)
            //exception if board has at least three different types and chosen gemTypes are two different ones or one
            assertThrows<IllegalArgumentException> {
                root.playerActionService.takeGems(types,
                    game.currentGameState.currentPlayer) }
            types.add(GemType.RED)
            //exception if three chosen gems contain less than three different GemTypes
            assertThrows<IllegalArgumentException> {
                root.playerActionService.takeGems(types,
                    game.currentGameState.currentPlayer) }
            types.remove(GemType.BLUE)
            //exception if less than four gems of GemType are left
            assertThrows<IllegalArgumentException> {
                root.playerActionService.takeGems(types,
                    game.currentGameState.currentPlayer) }

            //Functionality:
            //take two same gems
            boardGems[GemType.RED] = 4
            val playersGemCountRed = root.currentGame!!.currentGameState.currentPlayer.gems.getValue(GemType.RED)
            root.playerActionService.takeGems(types,game.currentGameState.currentPlayer)
            assertEquals(2, boardGems[GemType.RED])
            assertEquals(playersGemCountRed + 2,
                root.currentGame!!.currentGameState.currentPlayer.gems[GemType.RED])
            //take three different gems
            types.removeAt(0)
            types.add(GemType.GREEN)
            types.add(GemType.BLUE)
            root.playerActionService.takeGems(types,game.currentGameState.currentPlayer)
            assertEquals(1, boardGems[GemType.RED])
            assertEquals(1, boardGems[GemType.BLUE])
            assertEquals(1, boardGems[GemType.GREEN])
            assertEquals(playersGemCountRed + 3,
                root.currentGame!!.currentGameState.currentPlayer.gems[GemType.RED])
            assertEquals(playersGemCountRed + 1,
                root.currentGame!!.currentGameState.currentPlayer.gems[GemType.BLUE])
            assertEquals(playersGemCountRed + 1,
                root.currentGame!!.currentGameState.currentPlayer.gems[GemType.GREEN])
        }
    }

    /**
     * tests if cards from a level stack are reserved correct
     */
    @Test
    fun reserveCardFromStackTest() {
        val game = root.currentGame
        assertNull(root.currentGame)
        root.gameService.startNewGame(playerList, false, 1)
        assertNotNull(root.currentGame)
        if (game != null) {
            val devCard1 = DevCard(0, gemMap, 1, 0, GemType.BLUE)
            val devCard2 = DevCard(0, gemMap, 2, 0, GemType.RED)
            val devCard3 = DevCard(0, gemMap, 3, 0, GemType.RED)
            val devCard03 = DevCard(0, gemMap, 3, 0, GemType.RED)
            val board = root.currentGame!!.currentGameState.board
            val player = root.currentGame!!.currentGameState.currentPlayer
            board.levelOneCards.clear()
            board.levelOneCards.add(devCard1)
            board.levelTwoCards.clear()
            board.levelTwoCards.add(devCard2)
            board.levelThreeCards.clear()
            board.levelThreeCards.add(devCard3)
            board.levelThreeCards.add(devCard03)
            board.gems[GemType.YELLOW] = 2
            player.gems[GemType.YELLOW] = 0
            //reserveCard:
            root.playerActionService.reserveCard(devCard1, 0, game.currentGameState.currentPlayer)
            assertFalse(board.levelOneCards.contains(devCard1))
            assertTrue(player.reservedCards.contains(devCard1))
            assertTrue { board.gems[GemType.YELLOW] == 1 }
            assertTrue { player.gems[GemType.YELLOW] == 1 }
            root.playerActionService.reserveCard(devCard2, 0, game.currentGameState.currentPlayer)
            assertFalse(board.levelTwoCards.contains(devCard2))
            assertTrue(player.reservedCards.contains(devCard2))
            root.playerActionService.reserveCard(devCard3, 0, game.currentGameState.currentPlayer)
            assertFalse(board.levelThreeCards.contains(devCard3))
            assertTrue(player.reservedCards.contains(devCard3))
            assertTrue { board.gems[GemType.YELLOW] == 0 }
            assertTrue { player.gems[GemType.YELLOW] == 2 }

            assertThrows<IllegalArgumentException> {
                root.playerActionService.reserveCard(devCard03,0, game.currentGameState.currentPlayer) }
        }
    }

    /**
     * tests if open cards are reserved correct
     */
    @Test
    fun reserveCardFromOpenCardsTest() {
        val game = root.currentGame
        assertNull(root.currentGame)
        root.gameService.startNewGame(playerList, false, 1)
        assertNotNull(root.currentGame)
        if (game != null) {
            val devCard1 = DevCard(0, gemMap, 1, 0, GemType.BLUE)
            val devCard2 = DevCard(0, gemMap, 2, 0, GemType.RED)
            val devCard3 = DevCard(0, gemMap, 3, 0, GemType.RED)
            val devCard4 = DevCard(0, gemMap, 3, 1, GemType.RED)
            val devCard5 = DevCard(0, gemMap, 3, 1, GemType.RED)
            val board = root.currentGame!!.currentGameState.board
            val player = root.currentGame!!.currentGameState.currentPlayer
            board.levelOneOpen.clear()
            board.levelOneOpen.add(devCard1)
            board.levelTwoOpen.clear()
            board.levelTwoOpen.add(devCard2)
            board.levelThreeOpen.clear()
            board.levelThreeOpen.add(devCard3)
            board.levelThreeOpen.add(devCard4)
            board.gems[GemType.YELLOW] = 2
            player.gems[GemType.YELLOW] = 0
            //reserveCard:
            root.playerActionService.reserveCard(devCard1, 0, game.currentGameState.currentPlayer)
            assertFalse(board.levelOneOpen.contains(devCard1))
            assertTrue(player.reservedCards.contains(devCard1))
            assertTrue { board.levelOneOpen.size == 1 }
            assertTrue { board.gems[GemType.YELLOW] == 1 }
            assertTrue { player.gems[GemType.YELLOW] == 1 }
            root.playerActionService.reserveCard(devCard2, 0, game.currentGameState.currentPlayer)
            assertFalse(board.levelTwoOpen.contains(devCard2))
            assertTrue(player.reservedCards.contains(devCard2))
            assertTrue { board.levelTwoOpen.size == 1 }
            root.playerActionService.reserveCard(devCard3, 0, game.currentGameState.currentPlayer)
            assertFalse(board.levelThreeOpen.contains(devCard3))
            assertTrue(player.reservedCards.contains(devCard3))
            assertTrue { board.levelThreeOpen.size == 2 }
            assertTrue { board.gems[GemType.YELLOW] == 0 }
            assertNotNull(board.gems)
            assertTrue { player.gems[GemType.YELLOW] == 2 }

            assertThrows<IllegalArgumentException> {
                root.playerActionService.reserveCard(devCard4, 0, game.currentGameState.currentPlayer) }

            root.gameService.startNewGame(playerList, false, 1)
            val currentPlayer = root.currentGame!!.currentGameState.currentPlayer
            root.currentGame!!.currentGameState.board.gems[GemType.YELLOW] = 0
            root.playerActionService.reserveCard(devCard5, index = 0, game.currentGameState.currentPlayer)
            assertEquals(0, currentPlayer.gems[GemType.YELLOW])
        }
    }

    /**
     * tests if card is bought correct
     */
    @Test
    fun buyCardTest() {
        val game = root.currentGame
        root.gameService.startNewGame(playerList, false, 1)
        assertNotNull(root.currentGame)
        if (game != null) {
            gemMap = mutableMapOf(GemType.RED to 2, GemType.GREEN to 3)
            val devCard1 = DevCard(0, gemMap, 1, 1, GemType.BLUE)
            val devCard2 = DevCard(0, gemMap, 2, 1, GemType.RED)
            val devCard3 = DevCard(0, gemMap, 3, 1, GemType.RED)
            val board = root.currentGame!!.currentGameState.board
            val player = root.currentGame!!.currentGameState.currentPlayer
            player.gems[GemType.RED] = 3
            player.gems[GemType.GREEN] = 3
            board.gems[GemType.RED] = 0
            board.gems[GemType.GREEN] = 0
            val score = player.score
            player.bonus[GemType.BLUE] = 0

            //exception if card is not acquirable
            val map = mutableMapOf(GemType.RED to 0, GemType.GREEN to 0, GemType.WHITE to 0, GemType.BLACK to 0,
                GemType.BLUE to 0, GemType.YELLOW to 0)
            assertThrows<IllegalArgumentException> {
                root.playerActionService.buyCard(devCard1,true, map, 0, game.currentGameState.currentPlayer) }

            board.levelOneOpen.clear()
            board.levelOneOpen.add(devCard1)
            board.levelTwoOpen.clear()
            board.levelTwoOpen.add(devCard2)
            board.levelThreeOpen.clear()
            board.levelThreeOpen.add(devCard3)

            //buy card from board level 1
            root.playerActionService.buyCard(devCard1, true, player.gems, 0,
                game.currentGameState.currentPlayer)
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
            root.playerActionService.buyCard(devCard2, true, gemMap, 0,
                game.currentGameState.currentPlayer)
            assertFalse { board.levelOneOpen.contains(devCard2) }
            root.playerActionService.buyCard(devCard3, true, gemMap, 0,
                game.currentGameState.currentPlayer)
            assertFalse { board.levelOneOpen.contains(devCard3) }
            player.devCards.remove(devCard1)
            player.reservedCards.add(devCard1)
            root.playerActionService.buyCard(devCard1, false, gemMap, 0,
                game.currentGameState.currentPlayer)
            assertFalse { player.reservedCards.contains(devCard1) }

            root.currentGame = null
            assertThrows<IllegalStateException> {
                root.playerActionService.buyCard(devCard1, true, map, 0, game.currentGameState.currentPlayer) }
        }
    }

    /**
     * tests if nobleTiles are returned correct
     */
    @Test
    fun selectNobleTileTest() {
        val game = root.currentGame
        root.gameService.startNewGame(playerList, false, 1)
        assertNotNull(root.currentGame)
        if (game != null) {
            val nobleTileOne = NobleTile(0, gemMap, 1)
            root.currentGame!!.currentGameState.board.nobleTiles.add(nobleTileOne)
            val score = root.currentGame!!.currentGameState.currentPlayer.score
            assertFalse { root.currentGame!!.currentGameState.currentPlayer.nobleTiles.contains(nobleTileOne) }
            root.playerActionService.selectNobleTile(nobleTileOne, game.currentGameState.currentPlayer)
            assertTrue { root.currentGame!!.currentGameState.currentPlayer.nobleTiles.contains(nobleTileOne) }
            assertFalse { root.currentGame!!.currentGameState.board.nobleTiles.contains(nobleTileOne) }
            assertEquals(score + 1, root.currentGame!!.currentGameState.currentPlayer.score)

            root.currentGame = null
            assertThrows<IllegalStateException> {
                root.playerActionService.selectNobleTile(
                    nobleTileOne,
                    game.currentGameState.currentPlayer
                )
            }
        }
    }

    /**
     * test to check if gems are returned correct
     */
    @Test
    fun returnGemTest() {
        val game = root.currentGame
        root.gameService.startNewGame(playerList, false, 1)
        assertNotNull(root.currentGame)
        if (game != null) {
            assertThrows<IllegalStateException> {
                root.playerActionService.returnGems(mutableListOf(GemType.RED), game.currentGameState.currentPlayer) }
            root.gameService.startNewGame(playerList, false, 1)
            assertNotNull(root.currentGame)
            val gemTypeList = listOf(GemType.RED, GemType.BLUE)
            //set number of Players Gems
            val playersGems = root.currentGame!!.currentGameState.currentPlayer.gems
            playersGems.clear()
            playersGems[GemType.RED] = 2
            playersGems[GemType.BLUE] = 3
            //get numbers of gems on the board
            val numberRedGemsOnBoard = root.currentGame!!.currentGameState.board.gems.getValue(GemType.RED)
            val numberBlueGemsOnBoard = root.currentGame!!.currentGameState.board.gems.getValue(GemType.BLUE)
            //check if changes are correct
            root.playerActionService.returnGems(gemTypeList, game.currentGameState.currentPlayer)
            assertEquals(numberRedGemsOnBoard + 1, root.currentGame!!.currentGameState.board.gems[GemType.RED])
            assertEquals(numberBlueGemsOnBoard + 1, root.currentGame!!.currentGameState.board.gems[GemType.BLUE])
            assertEquals(1, root.currentGame!!.currentGameState.currentPlayer.gems[GemType.RED])
            assertEquals(2, root.currentGame!!.currentGameState.currentPlayer.gems[GemType.BLUE])
        }
    }

    /**
     * tests if undo and redo work
     */
    @Test
    fun undoRedoTest() {
        val game = root.currentGame
        root.gameService.startNewGame(playerList, false, 1)
        assertNotNull(root.currentGame)
        if (game != null) {
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
}