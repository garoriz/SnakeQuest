package com.example.snakequest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate


const val HEAD_SIZE = 100
const val CELLS_ON_FIELD = 10

class MainActivity : AppCompatActivity() {
    /*val displayMetrics = Resources.getSystem().displayMetrics
    private val allTale = mutableListOf<PartOfTale>()
    private val food by lazy {
        ImageView(this).apply {
            this.layoutParams = FrameLayout.LayoutParams(HEAD_SIZE, HEAD_SIZE)
            this.setImageResource(R.drawable.circle)
        }
    }
    private val head by lazy {
        ImageView(this)
            .apply {
                this.layoutParams = FrameLayout.LayoutParams(HEAD_SIZE, HEAD_SIZE)
                this.setImageResource(R.drawable.snake_head)
            }
    }
    private var currentDirection = Direction.BOTTOM
    private lateinit var binding: ActivityMainBinding*/

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /*binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        with(binding) {
            container.layoutParams = LinearLayout.LayoutParams(
                displayMetrics.widthPixels,
                HEAD_SIZE * CELLS_ON_FIELD,
            )

            startTheGame()
            generateNewFood()
            SnakeCore.nextMove = { move(Direction.BOTTOM) }

            ivArrowUp.setOnClickListener {
                SnakeCore.nextMove = {
                    checkIfCurrentDirectionIsNotOpposite(Direction.UP, Direction.BOTTOM)
                }
            }
            ivArrowBottom.setOnClickListener {
                SnakeCore.nextMove = {
                    checkIfCurrentDirectionIsNotOpposite(Direction.BOTTOM, Direction.UP)
                }
            }
            ivArrowLeft.setOnClickListener {
                SnakeCore.nextMove = {
                    checkIfCurrentDirectionIsNotOpposite(Direction.LEFT, Direction.RIGHT)
                }
            }
            ivArrowRight.setOnClickListener {
                SnakeCore.nextMove = {
                    checkIfCurrentDirectionIsNotOpposite(Direction.RIGHT, Direction.LEFT)
                }
            }
            ivPause.setOnClickListener {
                if (isPlay) {
                    ivPause.setImageResource(R.drawable.baseline_play)
                } else {
                    ivPause.setImageResource(R.drawable.baseline_pause)
                }
                isPlay = !isPlay
            }
        }*/
    }

    /*private fun checkIfCurrentDirectionIsNotOpposite(
        properDirection: Direction,
        oppositeDirection: Direction
    ) {
        if (currentDirection == oppositeDirection) {
            move(currentDirection)
        } else {
            move(properDirection)
        }
    }

    private fun generateNewFood() {
        val viewCoordinate = generateFoodCoordinates()
        (food.layoutParams as FrameLayout.LayoutParams).topMargin = viewCoordinate.top
        (food.layoutParams as FrameLayout.LayoutParams).leftMargin = viewCoordinate.left
        runOnUiThread {
            with(binding) {
                container.removeView(food)
                container.addView(food)
            }
        }
    }

    private fun generateFoodCoordinates(): ViewCoordinates {
        val viewCoordinates = ViewCoordinates(
            (0 until CELLS_ON_FIELD).random() * HEAD_SIZE,
            (0 until CELLS_ON_FIELD).random() * HEAD_SIZE
        )
        for (partTale in allTale) {
            if (partTale.viewCoordinates == viewCoordinates) {
                return generateFoodCoordinates()
            }
        }
        if (head.top == viewCoordinates.top && head.left == viewCoordinates.left) {
            return generateFoodCoordinates()
        }
        return viewCoordinates
    }

    private fun checkIfSnakeEatsFood() {
        if (head.left == food.left && head.top == food.top) {
            generateNewFood()
            addPartOfTale(head.top, head.left)
            increaseDifficult()
        }
    }

    private fun increaseDifficult() {
        if (gameSpeed <= MINIMUM_GAME_SPEED) {
            return
        }
        if (allTale.size % 5 == 0) {
            gameSpeed -= 100
        }
    }

    private fun addPartOfTale(top: Int, left: Int) {
        val talePart = drawPartOfTale(top, left)
        allTale.add(PartOfTale(ViewCoordinates(top, left), talePart))
    }

    private fun drawPartOfTale(top: Int, left: Int): ImageView {
        val taleImage = ImageView(this)
        taleImage.setImageResource(R.drawable.snake_scales)
        taleImage.setBackgroundColor(ContextCompat.getColor(this, R.color.reed_green))
        taleImage.layoutParams = FrameLayout.LayoutParams(HEAD_SIZE, HEAD_SIZE)
        (taleImage.layoutParams as FrameLayout.LayoutParams).topMargin = top
        (taleImage.layoutParams as FrameLayout.LayoutParams).leftMargin = left

        with(binding) {
            container.addView(taleImage)
        }
        return taleImage
    }

    fun move(direction: Direction) {
        when (direction) {
            Direction.UP -> moveHeadAndRotate(Direction.UP, 0f, -HEAD_SIZE)

            Direction.BOTTOM -> moveHeadAndRotate(Direction.BOTTOM, 180f, HEAD_SIZE)

            Direction.LEFT -> moveHeadAndRotate(Direction.LEFT, 270f, -HEAD_SIZE)

            Direction.RIGHT -> moveHeadAndRotate(Direction.RIGHT, 90f, HEAD_SIZE)
        }
        runOnUiThread {
            if (checkIfSnakeSmash()) {
                isPlay = false
                showScore()
                return@runOnUiThread
            }
            makeTaleMove()
            checkIfSnakeEatsFood()
            with(binding) {
                container.removeView(head)
                container.addView(head)
            }
        }
    }

    private fun moveHeadAndRotate(direction: Direction, angle: Float, coordinates: Int) {
        head.rotation = angle
        when (direction) {
            Direction.UP, Direction.BOTTOM -> {
                (head.layoutParams as FrameLayout.LayoutParams).topMargin += coordinates
            }

            Direction.LEFT, Direction.RIGHT -> {
                (head.layoutParams as FrameLayout.LayoutParams).leftMargin += coordinates
            }
        }
        currentDirection = direction
    }

    private fun showScore() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.your_score) + allTale.size)
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                this.recreate()
            }
            .setCancelable(false)
            .create()
            .show()
    }

    private fun checkIfSnakeSmash(): Boolean {
        for (talePart in allTale) {
            if (talePart.viewCoordinates.left == head.left
                && talePart.viewCoordinates.top == head.top
            ) {
                return true
            }
        }
        if (head.top < 0
            || head.left < 0
            || head.top >= HEAD_SIZE * CELLS_ON_FIELD
            || head.left >= displayMetrics.widthPixels
        ) {
            return true
        }
        return false
    }

    private fun makeTaleMove() {
        var tempTalePart: PartOfTale? = null
        with(binding) {
            for (index in 0 until allTale.size) {
                val talePart = allTale[index]
                container.removeView(talePart.imageView)
                if (index == 0) {
                    tempTalePart = talePart
                    allTale[index] = PartOfTale(
                        ViewCoordinates(head.top, head.left),
                        drawPartOfTale(head.top, head.left)
                    )
                } else {
                    val anotherTempPartOfTale = allTale[index]
                    tempTalePart?.let {
                        allTale[index] = PartOfTale(
                            ViewCoordinates(it.viewCoordinates.top, it.viewCoordinates.left),
                            drawPartOfTale(it.viewCoordinates.top, it.viewCoordinates.left)
                        )
                    }
                    tempTalePart = anotherTempPartOfTale
                }
            }
        }
    }*/
}

/*enum class Direction {
    UP,
    RIGHT,
    BOTTOM,
    LEFT
}*/