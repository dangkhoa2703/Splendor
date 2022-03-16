package entity

/**
 *  enum class for the different actions a player could do in a turn
 *  @property TAKE_GEMS: take two gems of the same type or take maximum three gems of different types
 *  @property RESERVECARD: reserve one devCard and receive one yellow gem
 *  @property BUYCARD: buy one devCard and receive its bonus and prestige points
 *  @property EMPTY: empty turn, for structural purposes
 */
enum class TurnType {
    TAKE_GEMS,
    BUY_CARD,
    RESERVE_CARD,
    EMPTY
}