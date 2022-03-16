package service

import entity.*
import kotlin.test.*

/**
 *  Test class for the AIService
 * */
class AIServiceTest {

    /** RootService reference */
    private var root = RootService()

    //Example cards
    private val devCardOne = DevCard(id = 1, price = mutableMapOf(GemType.GREEN to 2, GemType.RED to 3),1,
        bonus = GemType.BLACK, prestigePoints = 0)
    private val devCardTwo = DevCard(id = 2, price = mutableMapOf(GemType.GREEN to 5, GemType.RED to 3),2,
        bonus = GemType.BLACK, prestigePoints = 2)
    private val devCardThree = DevCard(id = 3, price = mutableMapOf(GemType.GREEN to 5, GemType.RED to 4),3,
        bonus = GemType.BLACK, prestigePoints = 4)
    private val devCardFour = DevCard(id = 4, price = mutableMapOf(GemType.GREEN to 4, GemType.RED to 6),2,
        bonus = GemType.BLACK, prestigePoints = 0)
    private val devCardFive = DevCard(id = 5, price = mutableMapOf(GemType.GREEN to 6, GemType.RED to 3),3,
        bonus = GemType.BLACK, prestigePoints = 0)
    private val devCardSix = DevCard(id = 6, price = mutableMapOf(GemType.BLUE to 2, GemType.GREEN to 6),2,
        bonus = GemType.BLUE, prestigePoints = 2)

    //Example noble tiles
    private val nobleTileOne = NobleTile(1, mutableMapOf(GemType.BLUE to 1), prestigePoints = 3)
    private val nobleTileTwo = NobleTile(2, mutableMapOf(GemType.RED to 2, GemType.BLUE to 1), prestigePoints = 3)

    //Example players
    private val testPlayer = Player("Anna", PlayerType.HUMAN, gems = mutableMapOf(GemType.GREEN to 3,
        GemType.RED to 3), bonus = mutableMapOf(), mutableListOf(), mutableListOf(), 0, mutableListOf())
    private val testPlayerWithOneBoni = Player("Bob", PlayerType.HUMAN, gems = mutableMapOf(GemType.GREEN to 3,
        GemType.RED to 3), bonus = mutableMapOf(GemType.GREEN to 1), mutableListOf(), mutableListOf(), 0,
        mutableListOf())
    private val testPlayerWithBoni = Player("Carl", PlayerType.HUMAN, gems = mutableMapOf(),
        bonus = mutableMapOf(GemType.GREEN to 3, GemType.RED to 3), mutableListOf(), mutableListOf(),
        0, mutableListOf())
    private val testPlayerFour = Player("David", PlayerType.HUMAN, gems = mutableMapOf(GemType.GREEN to 5,
        GemType.RED to 3), bonus = mutableMapOf(), mutableListOf(), mutableListOf(), 0, mutableListOf())

    //Example boards with three open devCards of each level to test help functions
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
    private val exampleBoard5 = Board(mutableListOf(), mutableListOf(), mutableListOf(devCardOne), mutableListOf(),
        mutableListOf(devCardFour), mutableListOf(), mutableListOf(devCardThree, devCardFive),
        mutableMapOf(GemType.GREEN to 3, GemType.RED to 3))
    private val exampleBoardCalculateDevCardImportanceScore = Board(mutableListOf(nobleTileOne, nobleTileTwo),
        mutableListOf(), mutableListOf(devCardOne), mutableListOf(), mutableListOf(devCardTwo, devCardSix),
        mutableListOf(), mutableListOf(devCardThree), mutableMapOf(GemType.GREEN to 3, GemType.RED to 3))

    /**
     * Test for calculateDevCardCostScores in AIService
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
     * Test for calculateDevCardPurchasingPowerScores
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
        //testPlayerFour needs zero rounds to buy devCardOne, zero rounds to buyDevCardTwo and one round to buy
        //devCardThree
        val expected3: Map<DevCard, Double> = mutableMapOf(devCardOne to 1.0, devCardTwo to 1.0, devCardThree to 0.0)
        assertEquals(expected3, root.aiService.calculateDevCardPurchasingPowerScores(exampleBoard3, testPlayerFour))
        //testPlayerFour needs zero rounds to buy devCardOne, one round to buy devCardThree (with 2 leftovers), one
        //round to buy devCardFive (with 2 leftovers) and two rounds to buy devCardFour (with 2 leftovers)
        val expected4: Map<DevCard, Double> = mutableMapOf(devCardOne to 1.0, devCardThree to 0.5, devCardFive to 0.5,
        devCardFour to 0.0)
        assertEquals(expected4, root.aiService.calculateDevCardPurchasingPowerScores(exampleBoard5, testPlayerFour))
    }

    /**
     * Test for calculateDevCardPurchasingPowerScoresForEnemies
    */
    @Test
    fun calculateDevCardPurchasingPowerScoresForEnemiesTest() {
        val enemyPlayers: List<Player> = listOf(testPlayer, testPlayerFour)
        //The best cards to buy for our enemies on our exampleBoard3 are 1.devCardOne, 2.devCard2 and 3.devCardThree
        //Score testPlayer: (devCardOne to 1.0, devCardTwo to 0.5, devCardThree to 0.0)
        //Score testPlayerFour: (devCardOne to 1.0, devCardTwo to 1.0, devCardThree to 0.0)
        //The average score for our cards is (devCardOne to 1.0, devCardTwo to 0.75, devCardThree to 0.0)
        //So our score for devCardOne need to be the lowest, the score for devCardThree has to be the highest
        val expected1: Map<DevCard, Double> = mutableMapOf(devCardOne to 0.0, devCardTwo to 0.25, devCardThree to 1.0)
        assertEquals(expected1, root.aiService.calculateDevCardPurchasingPowerScoresForEnemies(exampleBoard3,
        enemyPlayers))
    }

    /**
     * Test for calculateDevCardImportanceScore in AIService
     */
    @Test
    fun calculateDevCardImportanceScoreTest() {
        /**
         * 1. Given on board:
         *      - devCardOne (Green 2, Red 3), prestige 0
         *      - devCardTwo (Green 5, Red 3), prestige 2
         *      - devCardThree (Green 5, Red 4), prestige 4
         *      - devCardSix (Blue 2, Green 6), prestige 2
         *      - nobleTileOne (Blue 1), prestige 3
         *      - nobleTileTwo (Red 2, Blue 1), prestige 3
         * 2. Calculate the amount of purchasable cards for the bonus of a card:
         *      - card six = 1 (because  of blue bonus)
         *      - all other cards = 0
         * 3. Calculate the amount of noble tiles that need the bonus of the card
         *      - green = 0
         *      - red = 1
         *      - blue = 2
         * 4. Sort the cards on the board 1. after the prestige points, then after the amount of noble tiles and
         *      then after the amount of purchasable cards
         *          - Three, Two, Six, One (Two and Six have the same number of prestige points)
         *          - Three, Six, Two, One (Six can get more noble tiles with the bonus than Two)
         *          - Three, Six, Two, One
         * 5. Give each card a score:
         *          - Three = 1.0, Six = (2.0/3.0), Two = (1.0/3.0), One = 0.0
         */
        val expected: Map<DevCard, Double> = mutableMapOf(devCardThree to 1.0, devCardSix to 0.6666666666666667,
            devCardTwo to 0.33333333333333337, devCardOne to 0.0)
        assertEquals(expected,
            root.aiService.calculateDevCardImportanceScore(exampleBoardCalculateDevCardImportanceScore))
    }

    /**
     * Test for calculateGemPrice in AIService
     */
    @Test
    fun calculateGemPriceTest() {
        assertEquals(5,devCardOne.calculateGemPrice())
    }

    /**
     * Test for calculateMissingGems in AIService
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
     * Test for calculateAmountOfRoundsNeededToBuy in AIService
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

    /**
     * Test for chooseGems in AIService
     */
    @Test
    fun chooseGemsTest() {
        val devCardOne = DevCard(id = 1, price = mutableMapOf(GemType.RED to 3, GemType.BLUE to 1),1,
            bonus = GemType.BLACK, prestigePoints = 0)
        val devCardTwo = DevCard(id = 2, price = mutableMapOf(GemType.GREEN to 3, GemType.RED to 1),2,
            bonus = GemType.BLACK, prestigePoints = 2)
        val devCardThree = DevCard(id = 3, price = mutableMapOf(GemType.BLACK to 3, GemType.RED to 2),3,
            bonus = GemType.BLACK, prestigePoints = 4)
        val devCardFour = DevCard(id = 4, price = mutableMapOf(GemType.RED to 1),1,
            bonus = GemType.BLACK, prestigePoints = 0)
        val devCardFive = DevCard(id = 5, price = mutableMapOf(GemType.GREEN to 1),2,
            bonus = GemType.BLACK, prestigePoints = 2)
        val devCardSix = DevCard(id = 6, price = mutableMapOf(GemType.BLUE to 1),3,
            bonus = GemType.BLACK, prestigePoints = 4)
        val testPlayer = Player("Anna", PlayerType.HUMAN, gems = mutableMapOf(GemType.RED to 1,
            GemType.GREEN to 1, GemType.BLUE to 1), bonus = mutableMapOf(), mutableListOf(),
            mutableListOf(), 0, mutableListOf())
        val testPlayer2 = Player("Bob", PlayerType.HUMAN, gems = mutableMapOf(GemType.RED to 2),
            bonus = mutableMapOf(), mutableListOf(), mutableListOf(), 0, mutableListOf())
        val bestDevCards = mutableMapOf(devCardOne to 1.0, devCardTwo to 0.5, devCardThree to 0.0)
        val bestDevCardsTwo = mutableMapOf(devCardSix to 1.0, devCardFive to 0.5, devCardFour to 0.0)
        val exampleBoard1 = Board(mutableListOf(), mutableListOf(), mutableListOf(devCardOne), mutableListOf(),
            mutableListOf(devCardTwo), mutableListOf(), mutableListOf(devCardThree), mutableMapOf(GemType.RED to 3))
        val exampleBoard2 = Board(mutableListOf(), mutableListOf(), mutableListOf(devCardOne), mutableListOf(),
            mutableListOf(devCardTwo), mutableListOf(), mutableListOf(devCardThree), mutableMapOf(GemType.RED to 1,
            GemType.GREEN to 3, GemType.BLACK to 3))
        val exampleBoard3 = Board(mutableListOf(), mutableListOf(), mutableListOf(devCardOne), mutableListOf(),
            mutableListOf(devCardTwo), mutableListOf(), mutableListOf(devCardThree), mutableMapOf(GemType.RED to 0,
                GemType.GREEN to 3, GemType.BLACK to 3))
        val exampleBoard4 = Board(mutableListOf(), mutableListOf(), mutableListOf(devCardOne), mutableListOf(),
            mutableListOf(devCardTwo), mutableListOf(), mutableListOf(devCardThree), mutableMapOf())
        val exampleBoard5 = Board(mutableListOf(), mutableListOf(), mutableListOf(devCardFour), mutableListOf(),
            mutableListOf(devCardFive), mutableListOf(), mutableListOf(devCardSix), mutableMapOf(GemType.RED to 3,
                GemType.GREEN to 3, GemType.BLACK to 3))
        val exampleBoard6 = Board(mutableListOf(), mutableListOf(), mutableListOf(devCardFour), mutableListOf(),
            mutableListOf(devCardFive), mutableListOf(), mutableListOf(devCardSix), mutableMapOf(GemType.RED to 1,
                GemType.GREEN to 1))
        val exampleBoard7 = Board(mutableListOf(), mutableListOf(), mutableListOf(devCardOne), mutableListOf(),
            mutableListOf(devCardTwo), mutableListOf(), mutableListOf(devCardThree), mutableMapOf(GemType.RED to 1,
                GemType.WHITE to 1, GemType.GREEN to 1, GemType.BLACK to 2))
        val exampleBoard8 = Board(mutableListOf(), mutableListOf(), mutableListOf(devCardOne), mutableListOf(),
            mutableListOf(devCardTwo), mutableListOf(), mutableListOf(devCardThree), mutableMapOf(GemType.RED to 1))
        val exampleBoard9 = Board(mutableListOf(), mutableListOf(), mutableListOf(devCardOne), mutableListOf(),
            mutableListOf(devCardTwo), mutableListOf(), mutableListOf(devCardThree), mutableMapOf(GemType.RED to 1,
            GemType.BLUE to 1))

        // 1. Board has 3 red gems left, so we want to choose two red gems
        assertEquals(Pair(mutableMapOf(GemType.RED to 2),false),
            root.aiService.chooseGems(bestDevCards,testPlayer,exampleBoard1))
        // 2. Board has only 1 red gem left, so we want to choose two green gems
        assertEquals(Pair(mutableMapOf(GemType.GREEN to 2),false),
            root.aiService.chooseGems(bestDevCards,testPlayer,exampleBoard2))
        // 3. Board has no red gems left, so we want to choose two green gems
        assertEquals(Pair(mutableMapOf(GemType.GREEN to 2),false),
            root.aiService.chooseGems(bestDevCards,testPlayer,exampleBoard3))
        // 4. Board has no gems
        assertEquals(Pair(mutableMapOf(),false),
            root.aiService.chooseGems(bestDevCards,testPlayer,exampleBoard4))
        // 5. All cards have no missing gems (take one gem for each of the first board gem-colours)
        assertEquals(Pair(mutableMapOf(GemType.RED to 1, GemType.GREEN to 1, GemType.BLACK to 1),true),
            root.aiService.chooseGems(bestDevCardsTwo,testPlayer,exampleBoard5))
        // 6. The board has only two gems
        assertEquals(Pair(mutableMapOf(GemType.RED to 1, GemType.GREEN to 1),true),
            root.aiService.chooseGems(bestDevCardsTwo,testPlayer,exampleBoard6))
        // 7. Player can buy no card after taking some gems; the player chooses "take three gems with the first
        // three missing colours; player has two red gems; Board has 1 red gem, 1 white gem, 1 green and 2 black gems
        assertEquals(Pair(mutableMapOf(GemType.RED to 1, GemType.GREEN to 1, GemType.BLACK to 1),true),
            root.aiService.chooseGems(bestDevCards,testPlayer2,exampleBoard7))
        // 8. Player needs 2 red gems, but there is only 1 red gem on the board left (all other colours are also empty)
        assertEquals(Pair(mutableMapOf(GemType.RED to 1),false),
            root.aiService.chooseGems(bestDevCards,testPlayer2,exampleBoard8))
        // 9. There are only two gems on the board left
        assertEquals(Pair(mutableMapOf(GemType.RED to 1, GemType.BLUE to 1),true),
            root.aiService.chooseGems(bestDevCards,testPlayer2,exampleBoard9))
    }
}