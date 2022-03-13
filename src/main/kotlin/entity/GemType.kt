package entity

/**
 *  enum class for the different types a gem could have
 * */
enum class GemType {
    GREEN,
    RED,
    BLUE,
    WHITE,
    BLACK,
    YELLOW;

//    /** transforms the names to strings */
//    override fun toString() = when(this){
//        GREEN -> "green"
//        RED -> "red"
//        BLUE -> "blue"
//        WHITE -> "white"
//        BLACK -> "black"
//        YELLOW -> "yellow"
//    }
    fun toInt(): Int = when(this) {
    GREEN -> 3
    RED -> 4
    BLUE -> 2
    WHITE  -> 1
    BLACK -> 5
    YELLOW -> 6
    }

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