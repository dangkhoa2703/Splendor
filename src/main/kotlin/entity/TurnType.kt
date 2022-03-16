package entity

/**
 *  enum class for the different actions a player could do in a turn
 *  @property TAKE_GEMS: take two gems of the same type or take maximum three gems of different types
 *  @property RESERVE_CARD: reserve one devCard and receive one yellow gem
 *  @property BUY_CARD: buy one devCard and receive its bonus and prestige points
 */
enum class TurnType {
    TAKE_GEMS,
    BUY_CARD,
    RESERVE_CARD
}