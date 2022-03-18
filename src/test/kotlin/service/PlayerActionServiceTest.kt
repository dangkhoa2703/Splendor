package service

import entity.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import view.Refreshable
import kotlin.test.*

/**
 *  test class for playerActionService
 * */
class PlayerActionServiceTest {

    /** initialize objects */
    private val testRefreshable = TestRefreshable()
    private val root = RootService()
    private val playerOne = Player(name = "Olaf", playerType = PlayerType.HUMAN)
    private val playerTwo = Player(name = "Mirco", playerType = PlayerType.EASY)
    private val playerList = listOf(Pair("name1", PlayerType.HUMAN), Pair("name2", PlayerType.EASY))
    private var gemMap = mutableMapOf(GemType.RED to 2, GemType.GREEN to 3, GemType.WHITE to 3, GemType.BLACK to 4,
        GemType.BLUE to 3, GemType.YELLOW to 1)

    /**
     * starts a game with a static order of cards that can be used
     * in other tests to deterministically validate the outcome
     * of turns.
     * @return the root service holding the started game as [RootService.currentGame]
     */
    private fun setUpGame(vararg refreshables: Refreshable): RootService {
        val mc = RootService()
        refreshables.forEach { mc.addRefreshable(it) }

        mc.gameService.startNewGame(playerList,false,1)
        return mc
    }

    /** tests if showPlayers works correctly */
    @Test
    fun showPlayerTest(){
        val testRefreshable = TestRefreshable()
        val root = setUpGame(testRefreshable)
        root.playerActionService.showPlayers(
            root.currentGame!!.currentGameState.currentPlayer
        )
        assertTrue(testRefreshable.refreshAfterPopUpCalled)
    }

    /**
     * tests if takeGems(types) works correct
     */
    @Test
    fun takeGemsTest() {
        root.addRefreshable(testRefreshable)
        testRefreshable.reset()
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
        assertTrue(testRefreshable.refreshAfterTakeGemsCalled)
        assertEquals(2, root.currentGame!!.currentGameState.board.gems[GemType.RED])
        assertEquals(playersGemCountRed + 2, user.gems[GemType.RED])
        //take three different gems
        types.removeAt(0)
        types.add(GemType.GREEN)
        types.add(GemType.BLUE)
        root.gameService.nextPlayer()
        user = root.currentGame!!.currentGameState.currentPlayer
        root.playerActionService.takeGems(types, user)
        assertEquals(1, root.currentGame!!.currentGameState.board.gems[GemType.RED])
        assertEquals(1, root.currentGame!!.currentGameState.board.gems[GemType.BLUE])
        assertEquals(1, root.currentGame!!.currentGameState.board.gems[GemType.GREEN])
        root.gameService.startNewGame(playerList, false, 1)
        user = root.currentGame!!.currentGameState.currentPlayer
        root.playerActionService.takeGems(types, user)
        assertThrows<IllegalArgumentException> { root.playerActionService.takeGems(types, user) }
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
        player.gems[GemType.YELLOW] = 0
        root.currentGame!!.currentGameState.board.levelOneCards.clear()
        root.currentGame!!.currentGameState.board.levelOneCards.add(devCard1)
        root.currentGame!!.currentGameState.board.levelTwoCards.clear()
        root.currentGame!!.currentGameState.board.levelTwoCards.add(devCard2)
        root.currentGame!!.currentGameState.board.levelThreeCards.clear()
        root.currentGame!!.currentGameState.board.levelThreeCards.add(devCard3)
        root.currentGame!!.currentGameState.board.gems[GemType.YELLOW] = 2
        //reserveCard:
        root.playerActionService.reserveCard(devCard1, player)
        assertFalse(root.currentGame!!.currentGameState.board.levelOneCards.contains(devCard1))
        assertTrue(player.reservedCards.contains(devCard1))
        assertTrue { root.currentGame!!.currentGameState.board.gems[GemType.YELLOW] == 1 }
        assertTrue { player.gems[GemType.YELLOW] == 1 }
        root.gameService.nextPlayer()
        player = root.currentGame!!.currentGameState.currentPlayer
        root.playerActionService.reserveCard(devCard2, player)
        assertFalse(root.currentGame!!.currentGameState.board.levelTwoCards.contains(devCard2))
        assertTrue(player.reservedCards.contains(devCard2))
        root.gameService.nextPlayer()
        player = root.currentGame!!.currentGameState.currentPlayer
        root.playerActionService.reserveCard(devCard3, player)
        assertFalse(root.currentGame!!.currentGameState.board.levelThreeCards.contains(devCard3))
        assertTrue(player.reservedCards.contains(devCard3))
        assertTrue { root.currentGame!!.currentGameState.board.gems.getValue(GemType.YELLOW) == 0 }

        root.gameService.startNewGame(playerList, false, 1)
        player = root.currentGame!!.currentGameState.currentPlayer
        root.playerActionService.reserveCard(root.currentGame!!.currentGameState.board.levelOneOpen[0], player)
    }

    /**
     * tests if open cards are reserved correct
     */
    @Test
    fun reserveCardFromOpenCardsTest(){
        root.addRefreshable(testRefreshable)
        testRefreshable.reset()
        root.gameService.startNewGame(playerList, false, 1)
        var player = root.currentGame!!.currentGameState.currentPlayer
        player.gems[GemType.YELLOW] = 0
        val devCard1 = DevCard(111, gemMap, 1, 0, GemType.BLUE)
        val devCard2 = DevCard(222, gemMap, 2, 0, GemType.RED)
        val devCard3 = DevCard(333, gemMap, 3, 0, GemType.RED)
        val devCard4 = DevCard(444, gemMap, 3, 1, GemType.RED)
        val devCard5 = DevCard(555, gemMap, 3, 1, GemType.RED)
        val board = root.currentGame!!.currentGameState.board
        board.levelOneOpen[0] = devCard1
        board.levelTwoOpen[0] = devCard2
        board.levelThreeOpen[0] = devCard3
        board.levelThreeOpen[1] = devCard4
        board.gems[GemType.YELLOW] = 2
        //reserveCard:
        root.playerActionService.reserveCard(devCard1, player)
        assertTrue(testRefreshable.refreshAfterReserveCardCalled)
        assertNotEquals(devCard1, root.currentGame!!.currentGameState.board.levelOneOpen[0])
        assertTrue(player.reservedCards.contains(devCard1))
        assertTrue { root.currentGame!!.currentGameState.board.levelOneOpen.size == 4 }
        assertTrue { root.currentGame!!.currentGameState.board.gems[GemType.YELLOW] == 1 }
        assertTrue { player.gems.getValue(GemType.YELLOW) == 1 }
        root.gameService.nextPlayer()
        player = root.currentGame!!.currentGameState.currentPlayer
        root.playerActionService.reserveCard(devCard2, player)
        assertNotEquals(devCard2, root.currentGame!!.currentGameState.board.levelTwoOpen[0])
        assertTrue(player.reservedCards.contains(devCard2))
        assertTrue { root.currentGame!!.currentGameState.board.levelTwoOpen.size == 4 }
        root.gameService.nextPlayer()
        player = root.currentGame!!.currentGameState.currentPlayer
        root.playerActionService.reserveCard(devCard3, player)
        assertNotEquals(devCard3, root.currentGame!!.currentGameState.board.levelThreeOpen[0])
        assertTrue(player.reservedCards.contains(devCard3))
        assertTrue { root.currentGame!!.currentGameState.board.levelThreeOpen.size == 4 }
        assertTrue { root.currentGame!!.currentGameState.board.gems.getValue(GemType.YELLOW) == 0 }
        assertNotNull(root.currentGame!!.currentGameState.board.gems)
        root.gameService.startNewGame(playerList, false, 1)
        player = root.currentGame!!.currentGameState.currentPlayer
        player.gems[GemType.YELLOW] = 0
        root.currentGame!!.currentGameState.board.gems[GemType.YELLOW] = 0
        root.playerActionService.reserveCard(devCard5, player)
        assertEquals(0, player.gems[GemType.YELLOW])
        root.gameService.startNewGame(playerList, false, 1)
        assertThrows<NoSuchElementException> { root.playerActionService.buyCard(root.currentGame!!.currentGameState.
            board.levelOneOpen[0], false, mutableMapOf(), root.currentGame!!.currentGameState.
            currentPlayer) }
    }

    /**
     * tests if card is bought correct
     */
    @Test
    fun buyCardTest() {
        root.addRefreshable(testRefreshable)
        testRefreshable.reset()
        root.gameService.startNewGame(playerList, false, 1)
        assertNotNull(root.currentGame)
        gemMap = mutableMapOf(GemType.RED to 2, GemType.GREEN to 3)
        val devCard1 = DevCard(0, gemMap, 1, 1, GemType.BLUE)
        val devCard2 = DevCard(0, gemMap, 2, 1, GemType.RED)
        val devCard3 = DevCard(0, gemMap, 3, 1, GemType.RED)
        val board = root.currentGame!!.currentGameState.board
        var player = root.currentGame!!.currentGameState.currentPlayer
        player.gems[GemType.RED] = 2
        player.gems[GemType.GREEN] = 3
        board.gems[GemType.RED] = 0
        board.gems[GemType.GREEN] = 0
        val score = player.score
        player.bonus[GemType.BLUE] = 0

        board.levelOneOpen[0] = devCard1
        board.levelTwoOpen[0] = devCard2
        board.levelThreeOpen[0] = devCard3

        //buy card from board level 1
        root.playerActionService.buyCard(devCard1, true, player.gems, player)
        assertTrue { player.devCards.contains(devCard1) }
        assertFalse { board.levelOneOpen.contains(devCard1) }
        assertTrue { board.levelOneOpen.size == 4 }
        assertEquals(score + 1, player.score)
        assertEquals(1, player.bonus[GemType.BLUE])
        assertEquals(2, board.gems[GemType.RED])
        assertEquals(3, board.gems[GemType.GREEN])
        assertEquals(0, player.gems[GemType.RED])
        assertEquals(0, player.gems[GemType.GREEN])
        assertTrue(testRefreshable.refreshAfterBuyCardCalled)

        //buy card from other levels or reserved cards
        root.gameService.nextPlayer()
        player = root.currentGame!!.currentGameState.currentPlayer
        root.playerActionService.buyCard(devCard2, true, gemMap, player)
        assertFalse { board.levelOneOpen.contains(devCard2) }
        root.gameService.nextPlayer()
        player = root.currentGame!!.currentGameState.currentPlayer
        root.playerActionService.buyCard(devCard3, true, gemMap, player)
        assertFalse { board.levelOneOpen.contains(devCard3) }
        player.devCards.remove(devCard1)
        player.reservedCards.add(devCard1)
        root.gameService.nextPlayer()
        player = root.currentGame!!.currentGameState.currentPlayer
        root.playerActionService.buyCard(devCard1, false, gemMap, player)
        assertFalse { player.reservedCards.contains(devCard1) }
        root.gameService.startNewGame(playerList, false, 1)
        player = root.currentGame!!.currentGameState.currentPlayer
        player.gems[GemType.RED] = 10
        player.gems[GemType.GREEN] = 10
        root.playerActionService.buyCard(devCard1, false, gemMap, player)
        assertThrows<IllegalArgumentException> { root.playerActionService.buyCard(board.levelOneOpen[0],
            false, gemMap, player) }
    }

    /**
     * tests if nobleTiles are returned correct
     */
    @Test
    fun selectNobleTileTest() {
        root.addRefreshable(testRefreshable)
        testRefreshable.reset()
        root.gameService.startNewGame(playerList, false, 1)
        assertNotNull(root.currentGame)
        val emptyGemMap = mutableMapOf(GemType.RED to 0, GemType.GREEN to 0, GemType.WHITE to 0, GemType.BLACK to 0,
            GemType.BLUE to 0, GemType.YELLOW to 0)
        val player = root.currentGame!!.currentGameState.currentPlayer
        val nobleTileOne = NobleTile(0, emptyGemMap, 1)
        root.currentGame!!.currentGameState.board.nobleTiles.add(nobleTileOne)
        val score = root.currentGame!!.currentGameState.currentPlayer.score
        assertFalse { root.currentGame!!.currentGameState.currentPlayer.nobleTiles.contains(nobleTileOne) }
        root.playerActionService.selectNobleTile(nobleTileOne, player)
        assertTrue { player.nobleTiles.contains(nobleTileOne) }
        assertFalse { root.currentGame!!.currentGameState.board.nobleTiles.contains(nobleTileOne) }
        assertEquals(score + 1, player.score)

        val nobleTileTwo = NobleTile(20, mutableMapOf(GemType.RED to 3, GemType.GREEN to 3, GemType.WHITE to 3,
            GemType.BLACK to 0,  GemType.BLUE to 0, GemType.YELLOW to 0), 3)
        assertThrows<IllegalArgumentException> { root.playerActionService.selectNobleTile(nobleTileTwo, player) }
    }

    /**
     * test to check if gems are returned correct
     */
    @Test
    fun returnGemTest() {
        root.addRefreshable(testRefreshable)
        testRefreshable.reset()
        root.gameService.startNewGame(playerList, false, 1)
        assertNotNull(root.currentGame)
        val gemTypeList = listOf(GemType.RED, GemType.BLUE)
        //set number of Players Gems
        val player = root.currentGame!!.currentGameState.currentPlayer
        player.gems.clear()
        player.gems[GemType.RED] = 9
        player.gems[GemType.BLUE] = 3
        //get numbers of gems on the board
        val numberRedGemsOnBoard = root.currentGame!!.currentGameState.board.gems.getValue(GemType.RED)
        val numberBlueGemsOnBoard = root.currentGame!!.currentGameState.board.gems.getValue(GemType.BLUE)
        //check if changes are correct
        root.playerActionService.returnGems(gemTypeList, root.currentGame!!.currentGameState.currentPlayer)
        assertEquals(numberRedGemsOnBoard + 1, root.currentGame!!.currentGameState.board.gems[GemType.RED])
        assertEquals(numberBlueGemsOnBoard + 1, root.currentGame!!.currentGameState.board.gems[GemType.BLUE])
        assertEquals(8, player.gems[GemType.RED])
        assertEquals(2, player.gems[GemType.BLUE])
        assertTrue(testRefreshable.refreshAfterEndTurnCalled)
        assertThrows<IllegalArgumentException> { root.playerActionService.returnGems(gemTypeList, player) }
    }

    /**
     * tests if undo and redo work
     */
    @Test
    fun undoRedoTest() {
        root.addRefreshable(testRefreshable)
        testRefreshable.reset()
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
        assertTrue(testRefreshable.refreshAfterEndTurnCalled)
        testRefreshable.reset()
        assertEquals(gameStateOne, root.currentGame!!.currentGameState)
        assertFalse { root.currentGame!!.validGame }
        //test for redo
        root.currentGame!!.validGame = true
        root.playerActionService.redo()
        assertTrue(testRefreshable.refreshAfterEndTurnCalled)
        root.playerActionService.redo()
        assertEquals(gameStateTwo, root.currentGame!!.currentGameState)
        assertFalse { root.currentGame!!.validGame }
    }

    /**
     * tests if showHint works for buyCards and reserveCards
     */
    @Test
    fun showHintTestCards(){
        root.gameService.startNewGame(playerList, false, 1)
        val board = root.currentGame!!.currentGameState.board
        val player = root.currentGame!!.currentGameState.currentPlayer
        val testCard1 = DevCard(0,mapOf(),1,0,GemType.RED)
        val testCard2 = DevCard(0,mapOf(),2,0,GemType.RED)
        val testCard3 = DevCard(0,mapOf(),3,0,GemType.RED)

        //best turn is to reserve a card
        var turn = Turn(mapOf(),listOf(testCard1),TurnType.RESERVE_CARD)
        board.levelOneOpen.clear()
        board.levelOneOpen.add(testCard1)
        var hint = "You should reserve the level-1-card at position 1."
        assertEquals(hint, root.playerActionService.showHint(turn))
        turn = Turn(mapOf(),listOf(testCard2),TurnType.RESERVE_CARD)
        board.levelTwoOpen.clear()
        board.levelTwoOpen.add(testCard2)
        hint = "You should reserve the level-2-card at position 1."
        assertEquals(hint, root.playerActionService.showHint(turn))
        turn = Turn(mapOf(),listOf(testCard3),TurnType.RESERVE_CARD)
        board.levelThreeOpen.clear()
        board.levelThreeOpen.add(testCard3)
        hint = "You should reserve the level-3-card at position 1."
        assertEquals(hint, root.playerActionService.showHint(turn))

        //best turn is to buy a card
        turn = Turn(mapOf(),listOf(testCard1),TurnType.BUY_CARD)
        board.levelOneOpen.clear()
        board.levelOneOpen.add(testCard1)
        hint = "You should buy the level-1-card at position 1."
        assertEquals(hint,root.playerActionService.showHint(turn))
        player.reservedCards.add(testCard1)
        hint = "You should buy your reserved card at position 1."
        assertEquals(hint,root.playerActionService.showHint(turn))
        turn = Turn(mapOf(),listOf(testCard2),TurnType.BUY_CARD)
        board.levelOneOpen.clear()
        board.levelOneOpen.add(testCard2)
        hint = "You should buy the level-2-card at position 1."
        assertEquals(hint,root.playerActionService.showHint(turn))
        turn = Turn(mapOf(),listOf(testCard3),TurnType.BUY_CARD)
        board.levelOneOpen.clear()
        board.levelOneOpen.add(testCard3)
        hint = "You should buy the level-3-card at position 1."
        assertEquals(hint,root.playerActionService.showHint(turn))
    }

    /**
     * tests if showHint works for takeGems and returnGems
     */
    @Test
    fun showHintTestGems() {
        //best turn is to take gems
        root.gameService.startNewGame(playerList, false, 1)
        var turn = Turn(mapOf(Pair(GemType.RED, 2)), listOf(), TurnType.TAKE_GEMS)
        var hint = "You should take two RED gems "
        assertEquals(hint, root.playerActionService.showHint(turn))
        val gemsMap = mutableMapOf(Pair(GemType.RED, 1), Pair(GemType.GREEN, 1), Pair(GemType.BLUE, 1))
        turn = Turn(gemsMap, listOf(), TurnType.TAKE_GEMS)
        hint = "You should take three gems of the colours RED, GREEN and BLUE"
        assertEquals(hint, root.playerActionService.showHint(turn))

        //best turn is to take and to discard gems
        turn = Turn(mapOf(Pair(GemType.RED, 2)), listOf(), TurnType.TAKE_GEMS_AND_DISCARD)
        turn.gemsToDiscard = mapOf(GemType.RED to 1, GemType.BLACK to 1)
        hint = "You should take two RED gems and discard gems of the colours RED BLACK "
        assertEquals(hint, root.playerActionService.showHint(turn))
    }
}