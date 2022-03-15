package service

import entity.*
import java.io.File

/**
 *  Class for inputs and outputs
 * */
class IOService(private val rootService: RootService): AbstractRefreshingService()
{
    /**
     * loads a game from the given file path
     */
    fun loadGame(path:String):Splendor{
        //get information for splendor
        val gameSettingFile = File("$path/gameSetting")
        val gameSettings = gameSettingFile.readLines()
        val totalGameStates = gameSettings[0].trim().toInt()
        val indexCurrentGameState = gameSettings[1].trim().toInt()
        val simulationSpeed = gameSettings[2].trim().toInt()
        val playerCount = gameSettings[3].trim().toInt()
        val currentPlayerIndex = gameSettings[4].trim().toInt()
        val validGame = when (gameSettings[5].trim()) {
            "true" -> true
            "false" -> false
            else -> { throw IllegalStateException("no valid game parameter") }
        }

        //create gameStates and link them
        val gameStates = mutableListOf<GameState>()
        for (i in 1..totalGameStates){
            val file = File("$path/gameState${i}.txt")
            gameStates.add(loadGameState(file,playerCount))
        }
        gameStates[0].next = gameStates[1]
        gameStates[gameStates.size-1].previous = gameStates[gameStates.size-2]
        for ( i in 1..gameStates.size-2){
            gameStates[i].previous = gameStates[i-1]
            gameStates[i].next = gameStates[i+1]
        }

        //create splendor
        val currentGameState = gameStates[indexCurrentGameState-1] //muss geprüft werden!!!!
        val splendor = Splendor(simulationSpeed, currentGameState, mutableListOf(), validGame)
        rootService.gameService.consecutiveNoAction = gameSettings[6].trim().toInt()
        return splendor
    }


    /** loads a game state */
    fun loadGameState(file: File,numberPlayers:Int):GameState{
        val lines = file.readLines()

        //create Players
        val players = mutableListOf<Player>()
        var nextLineAt = 20
        val playerOneLines = lines.subList(0,8)
        players.add(createPlayerFromLines(playerOneLines))
        val playerTwoLines = lines.subList(10,18)
        players.add(createPlayerFromLines(playerTwoLines))
        if (numberPlayers>2){
            val playerThreeLines = lines.subList(20,28)
            players.add(createPlayerFromLines(playerThreeLines))
            nextLineAt = 30
            if(numberPlayers==4){
                val playerFourLines = lines.subList(30,38)
                players.add(createPlayerFromLines(playerFourLines))
                nextLineAt = 40
            }
        }

        //create board
        val boardFileLines = lines.subList(nextLineAt,nextLineAt+9)
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

        //create gameState
        val currentPlayer = players[lines[nextLineAt+10].toInt()]
        return GameState(currentPlayer, players, board)
    }

    /**
     * creates a player from strings
     */
    private fun createPlayerFromLines(fileLines:List<String>):Player{
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

    /**
     * creates devCards from strings
     */
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

    /**
     * creates noble tiles from strings
     */
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
        var toSaveGameState = game.currentGameState
        var totalGameState = 1
        var indexCurrentGame = 0

        // go to the first game state
        while(toSaveGameState.hasPrevious()){
            toSaveGameState = toSaveGameState.previous
            indexCurrentGame++
        }

        //create new file and save game state
        while(toSaveGameState.hasNext()){
            val fileName = "$path/gameState${totalGameState}.txt"
            val saveFile = File(fileName)
            saveFile.writeText("")
            saveGameState(toSaveGameState,saveFile)
            totalGameState++
            toSaveGameState = toSaveGameState.next
        }

        val gameSettingName = "$path/gameSetting"
        val gameFile = File(gameSettingName)
        gameFile.bufferedWriter().use{ out ->
            out.write((totalGameState-1).toString()+"\n")
            out.write(indexCurrentGame.toString() + "\n")
            out.write(game.simulationSpeed.toString() + "\n" )
            out.write(game.currentGameState.playerList.size.toString() + "\n")
            out.write(rootService.gameService.currentPlayerIndex.toString() +"\n")
            out.write(rootService.currentGame!!.validGame.toString() + "\n")
            out.write(rootService.gameService.consecutiveNoAction.toString() + "\n")
        }
    }

    /**
     * saves a gameState to a file
     */
    private fun saveGameState(gameState: GameState, file: File){

        val players = gameState.playerList
        val board = gameState.board

            file.bufferedWriter().use { out ->
                for (player in players) {
                    out.write(player.name + "\n")
                    out.write(player.playerType.toString() + "\n")
                    out.write(player.gems.toString() + "\n")
                    out.write(player.bonus.toString() + "\n")
                    out.write(cardToString(player.reservedCards, isDevCard = true) + "\n")
                    out.write(cardToString(nobleTileList = player.nobleTiles, isDevCard = false) + "\n")
                    out.write(player.score.toString() + "\n")
                    out.write(player.devCards.toString() + "\n" + "\n" + "\n")
                }
                //save current board infos
                out.write(cardToString(nobleTileList = board.nobleTiles, isDevCard = false) + "\n")
                out.write(cardToString(board.levelOneCards, isDevCard = true) + "\n")
                out.write(cardToString(board.levelOneOpen, isDevCard = true) + "\n")
                out.write(cardToString(board.levelTwoCards, isDevCard = true) + "\n")
                out.write(cardToString(board.levelTwoOpen, isDevCard = true) + "\n")
                out.write(cardToString(board.levelThreeCards, isDevCard = true) + "\n")
                out.write(cardToString(board.levelThreeOpen, isDevCard = true) + "\n")
                out.write(board.gems.toString() + "\n"+ "\n"+ "\n")
                out.write(players.indexOf(gameState.currentPlayer).toString())
            }

    }


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
            highscoreList.add(Highscore(content[0],content[1].toDouble()))
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