package service

import entity.*

/**
 *  Class for inputs and outputs
 * */
class IOService(private val rootService: RootService): AbstractRefreshingService()
{
    /** loads a game saved locally in a file */
    fun loadGame(path : String) : Splendor?
    {
        return  null
    }

    /** saves a file of the current game locally to the specified file path */
    fun saveGame(path : String) : Splendor?
    {
        return  null
    }

    /** deletes the file in the specified file path */
    fun deleteGame(path : String)
    {
    }

    /** saves a highscore to a file including highscores of different games */
    fun saveHighscore(score : Highscore)
    {
    }

    /** loads a file including highscores */
    fun loadHighscore(path : String) : List<Highscore>?
    {
        return  null
    }
}