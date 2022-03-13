package service
import entity.*
import kotlin.test.*

class AIServiceTest {

    /** RootService reference */
    private var root = RootService()

    private val devCardOne = DevCard(id = 2, price = mutableMapOf(GemType.GREEN to 2, GemType.RED to 3),1,
        bonus = GemType.BLACK, prestigePoints = 0)
    private val devCardTwo = DevCard(id = 2, price = mutableMapOf(GemType.GREEN to 5, GemType.RED to 3),2,
        bonus = GemType.BLACK, prestigePoints = 0)
    private val devCardThree = DevCard(id = 2, price = mutableMapOf(GemType.GREEN to 5, GemType.RED to 4),3,
        bonus = GemType.BLACK, prestigePoints = 0)
    private val testPlayer = Player("Anna", PlayerType.HUMAN, gems = mutableMapOf(GemType.GREEN to 3, GemType.RED to 3),
        bonus = mutableMapOf(), mutableListOf(), mutableListOf(), 0, mutableListOf())
    private val testPlayerWithOneBoni = Player("Anna", PlayerType.HUMAN, gems = mutableMapOf(GemType.GREEN to 3, GemType.RED to 3),
        bonus = mutableMapOf(GemType.GREEN to 1), mutableListOf(), mutableListOf(), 0, mutableListOf())
    private val testPlayerWithBoni = Player("Anna", PlayerType.HUMAN, gems = mutableMapOf(),
        bonus = mutableMapOf(GemType.GREEN to 3, GemType.RED to 3), mutableListOf(), mutableListOf(), 0, mutableListOf())

    //create example board with three open devCards to test help functions
    private val exampleBoard = Board(mutableListOf(), mutableListOf(), mutableListOf(devCardOne), mutableListOf(),
        mutableListOf(devCardTwo), mutableListOf(), mutableListOf(devCardThree), mutableMapOf(GemType.GREEN to 3, GemType.RED to 3))

    @Test
    fun calculateDevCardCostScoresTest() {
        val expected: Map<DevCard, Double> = mutableMapOf(devCardOne to 1.0, devCardTwo to 0.25, devCardThree to 0.0)
        assertEquals(expected, root.aiService.calculateDevCardCostScores(exampleBoard))
    }

    @Test
    fun calculateGemPriceTest() {
        assertEquals(5,devCardOne.calculateGemPrice())
    }

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