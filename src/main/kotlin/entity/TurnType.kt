package entity

/**
 *  enum class for the different actions a player could do in a turn
 *  @property TAKETWOGEMS: take two gems of the same type
 *  @property TAKETHREEGEMS: take maximum three gems of different types
 *  @property RESERVECARD: reserve one devCard and receive one yellow gem
 *  @property BUYCARD: buy one devCard and receive its bonus and prestige points
 * */
enum class TurnType {
    TAKETWOGEMS,
    TAKETHREEGEMS,
    RESERVECARD,
    BUYCARD
}