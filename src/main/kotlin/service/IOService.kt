package service

import entity.*
import java.io.File

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
    fun saveGame(path : String) {
        val game = rootService.currentGame
        checkNotNull(game)
        val currentPlayerIndex = rootService.gameService.currentPlayerIndex
        val players = game.currentGameState.playerList
        val board = game.currentGameState.board

        //save each player infos to a file
        for ((index, player) in players.withIndex()) {
            val playerFileName = path + "/player${index}"
            val playerFile = File(playerFileName)
            playerFile.bufferedWriter().use { out ->
                out.write(player.name + "\n")
                out.write(player.playerType.toString() + "\n")
                out.write(player.gems.toString() + "\n")
                out.write(player.bonus.toString() + "\n")
                out.write(cardToString(player.reservedCards, isDevCard = true) + "\n")
                out.write(cardToString(nobleTileList = player.nobleTiles, isDevCard = false) + "\n")
                out.write(player.score.toString() + "\n")
                out.write(player.devCards.toString() + "\n")
            }
        }

        //save current board infos
        val boardFileName = path + "/board"
        val boardFile = File(boardFileName)
        boardFile.bufferedWriter().use{ out ->
            out.write(cardToString(nobleTileList = board.nobleTiles, isDevCard = false) + "\n")
            out.write(cardToString(board.levelOneCards, isDevCard = true) + "\n")
            out.write(cardToString(board.levelOneOpen, isDevCard = true) + "\n")
            out.write(cardToString(board.levelTwoCards, isDevCard = true) + "\n")
            out.write(cardToString(board.levelTwoOpen, isDevCard = true) + "\n")
            out.write(cardToString(board.levelThreeOpen, isDevCard = true) + "\n")
            out.write(board.gems.toString() + "\n")
        }

        val gameSettingName = path + "/gameSetting"
        val gameFile = File(gameSettingName)
        gameFile.bufferedWriter().use{ out ->
            out.write(game.simulationSpeed.toString() + "\n" )
            out.write(game.currentGameState.playerList.size.toString() + "\n")
        }

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

    /*-HELP FUNCTION-*/

    private fun readMap(mapString: String): MutableMap<GemType,Int>{

        val mapList = mapString.split(",")
        for(pair in mapList){
            pair.trim()
        }
        val map = mapList.associate {
            val (left, right) = it.split("=")
            val gemType =  when(left){
                "WHITE" -> GemType.WHITE
                "BLUE" -> GemType.BLUE
                "GREEN" -> GemType.GREEN
                "RED" -> GemType.RED
                "BLACK" -> GemType.BLACK
                else -> {
                    throw java.lang.IllegalArgumentException(
                        "invalid gem type"
                    )
                }
            }
            gemType to right.toInt()
        }
        return map.toMutableMap()
    }

//    private fun readDevCards(): MutableList<DevCard>{
//
//
//    }

    /**
     * convert a mutablist of card in to string list of card's id
     *
     * @param devCardList to convert development card list
     * @param nobleTileList to convert noble card list
     * @param isDevCard to tell whether the to converted card list is a stack of development cards or nobleTile cards
     * @return string of card's id
     */
    private fun cardToString(
        devCardList: MutableList<DevCard> = mutableListOf(),
        nobleTileList: MutableList<NobleTile> = mutableListOf(),
        isDevCard: Boolean
    ): String{
        val cardIdList = mutableListOf<String>()

        if(isDevCard) {
            devCardList.forEach { card ->
                cardIdList.add(card.id.toString())
            }
        }else{
            nobleTileList.forEach{ card ->
                cardIdList.add(card.id.toString())}
        }
        return cardIdList.toString()
    }
}