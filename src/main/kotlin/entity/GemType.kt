package entity

enum class GemType {

    GREEN,
    RED,
    BLUE,
    WHITE,
    BLACK,
    YELLOW;

    override fun toString() = when(this){
        GREEN -> "green"
        RED -> "red"
        BLUE -> "blue"
        WHITE -> "white"
        BLACK -> "black"
        YELLOW -> "yellow"
    }
}