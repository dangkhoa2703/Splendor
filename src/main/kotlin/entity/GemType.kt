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

    /** transforms the names to strings */
    override fun toString() = when(this){
        GREEN -> "green"
        RED -> "red"
        BLUE -> "blue"
        WHITE -> "white"
        BLACK -> "black"
        YELLOW -> "yellow"
    }

}