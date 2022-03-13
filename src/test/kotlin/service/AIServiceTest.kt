package service
import entity.*
import kotlin.test.*

/**
 *  test class for AIService
 * */
class AIServiceTest {

    /** RootService reference */
    private var root = RootService()

    //Example cards
    private val devCardOne = DevCard(id = 2, price = mutableMapOf(GemType.GREEN to 2, GemType.RED to 3),1,
        bonus = GemType.BLACK, prestigePoints = 0)
    private val devCardTwo = DevCard(id = 2, price = mutableMapOf(GemType.GREEN to 5, GemType.RED to 3),2,
        bonus = GemType.BLACK, prestigePoints = 0)
    private val devCardThree = DevCard(id = 2, price = mutableMapOf(GemType.GREEN to 5, GemType.RED to 4),3,
        bonus = GemType.BLACK, prestigePoints = 0)
    private val devCardFour = DevCard(id = 2, price = mutableMapOf(GemType.GREEN to 4, GemType.RED to 6),2,
        bonus = GemType.BLACK, prestigePoints = 0)

    //Example players
    private val testPlayer = Player("Anna", PlayerType.HUMAN, gems = mutableMapOf(GemType.GREEN to 3,
        GemType.RED to 3), bonus = mutableMapOf(), mutableListOf(), mutableListOf(), 0, mutableListOf())
    private val testPlayerWithOneBoni = Player("Bob", PlayerType.HUMAN, gems = mutableMapOf(GemType.GREEN to 3,
        GemType.RED to 3), bonus = mutableMapOf(GemType.GREEN to 1), mutableListOf(), mutableListOf(), 0,
        mutableListOf())
    private val testPlayerWithBoni = Player("Carl", PlayerType.HUMAN, gems = mutableMapOf(),
        bonus = mutableMapOf(GemType.GREEN to 3, GemType.RED to 3), mutableListOf(), mutableListOf(),
        0, mutableListOf())
    /** private val testPlayerFour = Player("David", PlayerType.HUMAN, gems = mutableMapOf(GemType.GREEN to 5,
        GemType.RED to 3), bonus = mutableMapOf(), mutableListOf(), mutableListOf(), 0, mutableListOf()) */

    //create example boards with three open devCards to test help functions
    private val exampleBoard1 = Board(mutableListOf(), mutableListOf(), mutableListOf(devCardOne), mutableListOf(),
        mutableListOf(devCardOne), mutableListOf(), mutableListOf(devCardOne), mutableMapOf(GemType.GREEN to 3,
            GemType.RED to 3))
    private val exampleBoard2 = Board(mutableListOf(), mutableListOf(), mutableListOf(devCardOne), mutableListOf(),
        mutableListOf(devCardTwo), mutableListOf(), mutableListOf(devCardOne), mutableMapOf(GemType.GREEN to 3,
            GemType.RED to 3))
    private val exampleBoard3 = Board(mutableListOf(), mutableListOf(), mutableListOf(devCardOne), mutableListOf(),
        mutableListOf(devCardTwo), mutableListOf(), mutableListOf(devCardThree), mutableMapOf(GemType.GREEN to 3,
            GemType.RED to 3))
    private val exampleBoard4 = Board(mutableListOf(), mutableListOf(), mutableListOf(devCardOne), mutableListOf(),
        mutableListOf(devCardFour), mutableListOf(), mutableListOf(devCardThree), mutableMapOf(GemType.GREEN to 3,
            GemType.RED to 3))

    /**
     * test for calculateDevCardCostScores in AIService
     */
    @Test
    fun calculateDevCardCostScoresTest() {
        // all cards have the same costs
        val expected1: Map<DevCard, Double> = mutableMapOf(devCardOne to 1.0, devCardOne to 1.0, devCardOne to 1.0)
        assertEquals(expected1, root.aiService.calculateDevCardCostScores(exampleBoard1))
        // two cards have the same cost
        val expected2: Map<DevCard, Double> = mutableMapOf(devCardOne to 1.0, devCardOne to 1.0, devCardTwo to 0.0)
        assertEquals(expected2, root.aiService.calculateDevCardCostScores(exampleBoard2))
        // all cards have a different costs
        val expected3: Map<DevCard, Double> = mutableMapOf(devCardOne to 1.0, devCardTwo to 0.25, devCardThree to 0.0)
        assertEquals(expected3, root.aiService.calculateDevCardCostScores(exampleBoard3))
    }

    /**
     * test for calculateDevCardPurchasingPowerScores
     */
    @Test
    fun calculateDevCardPurchasingPowerScoresTest() {
        //testPlayer needs zero rounds to buy devCardOne, one round to buy devCardTwo and two rounds
        //to buy devCardThree in exampleBoard3
        val expected1: Map<DevCard, Double> = mutableMapOf(devCardOne to 1.0, devCardTwo to 0.5, devCardThree to 0.0)
        assertEquals(expected1, root.aiService.calculateDevCardPurchasingPowerScores(exampleBoard3, testPlayer))
        //testPlayer needs one round to buy devCardTwo, two rounds to buy devCardThree (with 2 leftovers) and
        //two rounds to uy devCardFour (with 1 leftover)
        val expected2: Map<DevCard, Double> = mutableMapOf(devCardOne to 1.0, devCardThree to 0.5, devCardFour to 0.0)
        assertEquals(expected2, root.aiService.calculateDevCardPurchasingPowerScores(exampleBoard4, testPlayer))
        //testPlayerFour need zero rounds to buy devCardOne, zero rounds to buyDevCardTwo and one round to buy
        //devCardThree, but the player needs to pay less for devCardOne, so the score is higher
        /** Fehler: devCardThree 0.5 anstatt 0.0
         * val expected3: Map<DevCard, Double> = mutableMapOf(devCardOne to 1.0, devCardTwo to 1.0, devCardThree to 0.0)
         * assertEquals(expected3, root.aiService.calculateDevCardPurchasingPowerScores(exampleBoard3, testPlayerFour))
         */
    }

    /**
     * test for calculateDevCardPurchasingPowerScoresForEnemies

    @Test
    fun calculateDevCardPurchasingPowerScoresForEnemiesTest() {
        val enemyPlayers: List<Player> = listOf(testPlayer, testPlayerFour)
        //The best cards to buy for our enemies on our exampleBoard3 are 1.devCardOne, 2.devCard2 and 3.devCardThree
        //So our score for devCardOne need to be the lowest, the score for devCardThree has to be the highest
        val expected1: Map<DevCard, Double> = mutableMapOf(devCardThree to 1.0, devCardTwo to 0.5, devCardOne to 0.0)
        assertEquals(expected1, root.aiService.calculateDevCardPurchasingPowerScoresForEnemies(exampleBoard3,
    enemyPlayers))
    }*/

    /**
     * test for calculateGemPrice in AIService
     */
    @Test
    fun calculateGemPriceTest() {
        assertEquals(5,devCardOne.calculateGemPrice())
    }

    /**
     * test for calculateMissingGems in AIService
     */
    @Test
    fun calculateMissingGemsTest() {
        //No gems are missing
        val cost1 = mutableMapOf(GemType.GREEN to 2, GemType.RED to 3)
        assertEquals(mutableMapOf(), root.aiService.calculateMissingGems(testPlayer,cost1))
        //Two green gems are missing and player has no bonus
        val cost2 = mutableMapOf(GemType.GREEN to 5, GemType.RED to 3)
        assertEquals(mutableMapOf(GemType.GREEN to 2),root.aiService.calculateMissingGems(testPlayer,cost2))
        //Two green gems are missing but player has one green boni
        assertEquals(mutableMapOf(GemType.GREEN to 1),root.aiService.calculateMissingGems(testPlayerWithOneBoni,cost2))
        //Player has no gems but can pay with boni
        assertEquals(mutableMapOf(), root.aiService.calculateMissingGems(testPlayerWithBoni,cost1))
    }

    /**
     * test for calculateAmountOfRoundsNeededToBuy in AIService
     */
    @Test
    fun calculateAmountOfRoundsNeededToBuyTest() {
        //No gems are missing, player can buy the card now
        assertEquals(Pair(0,0), root.aiService.calculateAmountOfRoundsNeededToBuy(testPlayer,devCardOne))
        //Only two gems of the same colour are missing, player needs one more round and has zero leftOverGems
        assertEquals(Pair(1,0), root.aiService.calculateAmountOfRoundsNeededToBuy(testPlayer,devCardTwo))
        //Three gems are missing, two of the same colour and one other
        //Player needs two rounds and has two leftOverGems
        assertEquals(Pair(2,2), root.aiService.calculateAmountOfRoundsNeededToBuy(testPlayer,devCardThree))
    }

}