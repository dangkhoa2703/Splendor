package service

import entity.*
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter




/**
 *  Class for inputs and outputs
 * */
class IOService(private val rootService: RootService): AbstractRefreshingService()
{
    /** loads a game saved locally in a file */
    fun loadGame(path: String): Splendor? {
        val gameSettingFile = File("$path/gameSetting")
        val playerOneFile = File("$path/player1")
        val playerTwoFile = File("$path/player2")
        val playerThreeFile = File("$path/player3")
        val playerFourFile = File("$path/player4")
        val boardFile = File("$path/board")
        if (!gameSettingFile.exists()) { return null }
        val playerCount = gameSettingFile.readLines()[1].toInt()

        //create board
        val boardFileLines = boardFile.readLines()
        val nobleTileIDs = boardFileLines[0].removePrefix("[").removeSuffix("]").split(",")
        val nobleTilesOnBoard = readNobleTiles(nobleTileIDs)
        var devCardsIDs = boardFileLines[1].removePrefix("[").removeSuffix("]").split(",")
        val levelOneCards = readDevCards(devCardsIDs)
        devCardsIDs = boardFileLines[2].removePrefix("[").removeSuffix("]").split(",")
        val levelOneOpen = readDevCards(devCardsIDs)
        devCardsIDs = boardFileLines[3].removePrefix("[").removeSuffix("]").split(",")
        val levelTwoCards = readDevCards(devCardsIDs)
        devCardsIDs = boardFileLines[4].removePrefix("[").removeSuffix("]").split(",")
        val levelTwoOpen = readDevCards(devCardsIDs)
        devCardsIDs = boardFileLines[5].removePrefix("[").removeSuffix("]").split(",")
        val levelThreeCards = readDevCards(devCardsIDs)
        devCardsIDs = boardFileLines[6].removePrefix("[").removeSuffix("]").split(",")
        val levelThreeOpen = readDevCards(devCardsIDs)
        val gemsFromFile = boardFileLines[7].removePrefix("{").removeSuffix("}")
        val gems = readMap(gemsFromFile)
        val board = Board(nobleTilesOnBoard, levelOneCards, levelOneOpen, levelTwoCards, levelTwoOpen,
            levelThreeCards, levelThreeOpen)
        board.gems = gems

        //create Players
        val playerList = mutableListOf<Player>()
        val playerOne = createPlayerFromFile(playerOneFile)
        val playerTwo = createPlayerFromFile(playerTwoFile)
        playerList.add(playerOne)
        playerList.add(playerTwo)
        if (playerCount > 2) {
            val playerThree = createPlayerFromFile(playerThreeFile)
            playerList.add(playerThree)
            if (playerCount == 4) {
                val playerFour = createPlayerFromFile(playerFourFile)
                playerList.add(playerFour) } }

        //create Splendor
        val gameSettings = gameSettingFile.readLines()
        val simulationSpeed = gameSettings[0].trim().toInt()
        val currentPlayer = playerList[gameSettings[2].trim().toInt()]
        val validGame = when (gameSettings[3].trim()) {
            "true" -> true
            "false" -> false
            else -> { throw IllegalStateException("no valid game parameter") }
        }
        val gameState = GameState(currentPlayer, playerList, board)
        ///hier noch Highscore Objekte laden

        return Splendor(simulationSpeed, gameState, mutableListOf(), validGame)
    }

    private fun createPlayerFromFile(file:File):Player{
        val fileLines = file.readLines()
        val name = fileLines[0].trim()
        val playerType = when (fileLines[1].trim()) {
            "HUMAN" -> PlayerType.HUMAN
            "MEDIUM" -> PlayerType.MEDIUM
            "EASY" -> PlayerType.EASY
            "HARD" -> PlayerType.HARD
            else -> { throw java.lang.IllegalArgumentException("invalid player type") }
        }
        val gems = readMap(fileLines[2].removePrefix("{").removeSuffix("}"))
        val bonus = readMap(fileLines[3].removePrefix("{").removeSuffix("}"))
        val reserved = readDevCards(fileLines[4].removePrefix("[").removeSuffix("]").split(","))
        val nobles = readNobleTiles(fileLines[5].removePrefix("[").removeSuffix("]").split(","))
        val score = fileLines[6].trim().toInt()
        val devCards = readDevCards(fileLines[7].removePrefix("[").removeSuffix("]").split(","))
        return Player(name,playerType,gems,bonus,reserved,nobles,score,devCards)
    }


    private fun readDevCards(list :List<String>): MutableList<DevCard>{
        val devCards = mutableListOf<DevCard>()
        val cardConfigFile = File("src/main/resources/splendor-entwicklungskarten.csv")
        val cardConfigStringList = cardConfigFile.readLines()


        if(list.size == 1 && list[0].trim() == ""){
            return mutableListOf()
        }

        list.forEach { id ->
            // retrieve the card's configuration according to card's id
            val cardConfigString = cardConfigStringList[id.trim().toInt() + 1]
            val cardConfig = cardConfigString.split(", ")
            val tempMap = mapOf(
                GemType.WHITE to cardConfig[1].trim().toInt(),
                GemType.BLUE to cardConfig[2].trim().toInt(),
                GemType.GREEN to cardConfig[3].trim().toInt(),
                GemType.RED to cardConfig[4].trim().toInt(),
                GemType.BLACK to cardConfig[5].trim().toInt()
            )
            val color = when (cardConfig[8].trim()) {
                "diamant" -> GemType.WHITE
                "saphir" -> GemType.BLUE
                "smaragd" -> GemType.GREEN
                "rubin" -> GemType.RED
                "onyx" -> GemType.BLACK
                else -> { throw java.lang.IllegalArgumentException("invalid gem type") }
            }
            devCards.add(
                DevCard(
                    cardConfig[0].trim().toInt(),
                    tempMap,
                    cardConfig[6].trim().toInt(),
                    cardConfig[7].trim().toInt(),
                    color))
        }
        return devCards
    }

    private fun readNobleTiles( list :List<String>):MutableList<NobleTile> {
        val nobleTiles = mutableListOf<NobleTile>()
        val cardConfigFile = File("src/main/resources/splendor-adligenkarten.csv")
        val cardConfigStringList = cardConfigFile.readLines()

        if(list.size == 1 && list[0].trim() == ""){
            return mutableListOf()
        }

        list.forEach { id ->
            // retrieve the card's configuration according to card's id
            val cardConfigString = cardConfigStringList[id.trim().toInt() - 89]
            val cardConfig = cardConfigString.split(", ")
            val tempMap = mapOf(
                GemType.WHITE to cardConfig[1].trim().toInt(),
                GemType.BLUE to cardConfig[2].trim().toInt(),
                GemType.GREEN to cardConfig[3].trim().toInt(),
                GemType.RED to cardConfig[4].trim().toInt(),
                GemType.BLACK to cardConfig[5].trim().toInt()
            )
            nobleTiles.add(
                NobleTile(
                    cardConfig[0].trim().toInt(),
                    tempMap,
                    cardConfig[6].trim().toInt()
                )
            )
        }
        return nobleTiles
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
            val playerFileName = path + "/player${index+1}"
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
        val boardFileName = "$path/board"
        val boardFile = File(boardFileName)
        boardFile.bufferedWriter().use{ out ->
            out.write(cardToString(nobleTileList = board.nobleTiles, isDevCard = false) + "\n")
            out.write(cardToString(board.levelOneCards, isDevCard = true) + "\n")
            out.write(cardToString(board.levelOneOpen, isDevCard = true) + "\n")
            out.write(cardToString(board.levelTwoCards, isDevCard = true) + "\n")
            out.write(cardToString(board.levelTwoOpen, isDevCard = true) + "\n")
            out.write(cardToString(board.levelThreeCards, isDevCard = true) + "\n")
            out.write(cardToString(board.levelThreeOpen, isDevCard = true) + "\n")
            out.write(board.gems.toString() + "\n")
        }

        val gameSettingName = "$path/gameSetting"
        val gameFile = File(gameSettingName)
        gameFile.bufferedWriter().use{ out ->
            out.write(game.simulationSpeed.toString() + "\n" )
            out.write(game.currentGameState.playerList.size.toString() + "\n")
            out.write(rootService.gameService.currentPlayerIndex.toString() +"\n")
            out.write(rootService.currentGame!!.validGame.toString() + "\n")
        }
    }

//    /** deletes the content of the file in the specified file path */
//    fun deleteGame(path : String)
//    {
//        FileWriter(path + "gameSetting",false).close()
//        FileWriter(path + "player1",false).close()
//        FileWriter(path + "player2",false).close()
//        FileWriter(path + "player3",false).close()
//        FileWriter(path + "player4",false).close()
//        FileWriter(path + "board",false).close()
//    }

    /** saves a highscore to a file including highscores of different games; maximum 10*/
    fun saveHighscore(score : Highscore) {
        val currentHighscores = loadHighscore()
        currentHighscores.add(score)
        currentHighscores.sortByDescending { highscore -> highscore.score }
        //es werden nur die 10 besten scores gespeichert
        if(currentHighscores.size>10){
            currentHighscores.removeAt(10)
        }

        //save updated highscores if changed
        if(currentHighscores != loadHighscore()) {
            val highscoreFile = File("src/main/resources/highscore")
            highscoreFile.bufferedWriter().use{ out ->
                for (highscore in currentHighscores) {
                    out.write(highscore.playerName+","+highscore.score+ "\n")
                }
            }
        }
    }

    /** loads a file including highscores */
    fun loadHighscore() : MutableList<Highscore> {
        val highscoreFileList = File("src/main/resources/highscore").readLines()
        val highscoreList = mutableListOf<Highscore>()
        var content:List<String>
        for(line in highscoreFileList){
            content = line.split(",")
            highscoreList.add(Highscore(content[0],content[1].toInt()))
        }
        return highscoreList
    }

    /*-HELP FUNCTION-*/

    private fun readMap(mapString: String): MutableMap<GemType,Int> {

        val mapList = mapString.split(",")
        /** for(pair in mapList){
        pair.trim()
        } */
        mapList.forEach { it.trim() }

        val map = mapList.associate {
            val (left, right) = it.split("=")
            val gemType =  when(left.trim()){
                "WHITE" -> GemType.WHITE
                "BLUE" -> GemType.BLUE
                "GREEN" -> GemType.GREEN
                "RED" -> GemType.RED
                "BLACK" -> GemType.BLACK
                "YELLOW" -> GemType.YELLOW
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

    /**
     * convert a mutableList of card in to string list of card's id
     *
     * @param devCardList to convert development card list
     * @param nobleTileList to convert noble card list
     * @param isDevCard to tell whether the converted card list is a stack of development cards or nobleTile cards
     * @return string of card's id
     */
    private fun cardToString(
        devCardList: MutableList<DevCard> = mutableListOf(),
        nobleTileList: MutableList<NobleTile> = mutableListOf(),
        isDevCard: Boolean): String {
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