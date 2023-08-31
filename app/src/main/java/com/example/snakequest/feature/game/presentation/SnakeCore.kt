package com.example.snakequest.feature.game.presentation

object SnakeCore {

    var nextMove:() -> Unit = {}
    var isPlay = true
    private val thread: Thread
    var gameSpeed = 500L

    init {
        thread =  Thread(Runnable {
            while (true) {
                Thread.sleep(gameSpeed)
                if (isPlay) {
                    nextMove()
                }
            }
        })
        thread.start()
    }

    fun startTheGame() {
        isPlay = true
    }
}