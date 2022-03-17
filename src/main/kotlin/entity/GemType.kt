package entity

/** enum class for the different types a gem could have.*/
enum class GemType {
    GREEN,
    RED,
    BLUE,
    WHITE,
    BLACK,
    YELLOW;

    /**[toInt] : method to convert our Gem types from the enum class GemType to corresponding Int values/ "positions". */
    fun toInt(): Int = when(this) {
    GREEN -> 3
    RED -> 4
    BLUE -> 2
    WHITE  -> 1
    BLACK -> 5
    YELLOW -> 6
    }

    /**[gemType] : method to convert our Int Values to corresponding Gem Types. */
    fun gemType (index: Int): GemType? = when(index) {
        1 -> WHITE
        2 -> BLUE
        3 -> GREEN
        4 -> RED
        5 -> BLACK
        6 -> YELLOW
        else -> null
    }
}