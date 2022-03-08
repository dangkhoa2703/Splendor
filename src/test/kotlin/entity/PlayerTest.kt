package entity

import kotlin.test.*

/**
 *  test class for player objects
 * */
class PlayerTest
{
    /** initialize a player */
    private val playerOne = Player(name = "Olaf", playerType = PlayerType.HUMAN)

    @Test
    fun testPlayer()
    {
        /** test if player got initialized correctly */
        assertEquals("Olaf", playerOne.name)
        assertEquals(0, playerOne.score)
        assertEquals(0, playerOne.gems[GemType.RED])
        assertEquals(0, playerOne.gems[GemType.BLUE])

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
}