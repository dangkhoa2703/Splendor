package entity

/**
 *  enum class for the different types a player could have
 *  @property HUMAN: the player's actions are controlled by a human
 *  @property EASY: low skill level of the artificial intelligence
 *  @property MEDIUM: moderate skill level of the artificial intelligence
 *  @property HARD: high skill level of the artificial intelligence
 * */
enum class PlayerType {
    HUMAN,
    EASY,
    MEDIUM,
    HARD
}