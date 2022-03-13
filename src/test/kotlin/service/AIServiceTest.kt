package service
import entity.*
import kotlin.test.*

class AIServiceTest {

    /** RootService reference */
    private var root = RootService()

    val devCardOne = DevCard(id = 2, price = mutableMapOf(GemType.GREEN to 2, GemType.RED to 3),1,
        bonus = GemType.BLACK, prestigePoints = 0)
    val testPlayer = Player("Anna", PlayerType.HUMAN, gems = mutableMapOf(GemType.GREEN to 3, GemType.RED to 3),
    bonus = mutableMapOf(GemType.BLUE to 1), mutableListOf(), mutableListOf(), 0, mutableListOf())

    @Test
    fun calculateMissingGemsTest() {
        /*
        //No gems are missing
        val cost1: Pair<Int,Int> = Pair(2,3)
        assertNull(root.aiService.calculateMissingGems(testPlayer,cost1))
        //Two green gems are missing
        val cost2: Pair<Int,Int> = Pair(5,3)
        assertEquals(mutableMapOf(GemType.GREEN,2),root.aiService.calculateMissingGems(testPlayer,cost1))
        */
    }

}