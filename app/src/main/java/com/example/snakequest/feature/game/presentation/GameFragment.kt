package com.example.snakequest.feature.game.presentation

import android.content.res.Resources
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.example.snakequest.R
import com.example.snakequest.feature.game.presentation.SnakeCore.isPlay
import com.example.snakequest.feature.game.presentation.SnakeCore.startTheGame
import com.example.snakequest.databinding.FragmentGameBinding

const val HEAD_SIZE = 100
const val CELLS_ON_FIELD = 10
const val GAME_SPEED = "GAME_SPEED"

class GameFragment : Fragment(R.layout.fragment_game) {
    private lateinit var binding: FragmentGameBinding
    val displayMetrics = Resources.getSystem().displayMetrics
    private val allTale = mutableListOf<PartOfTale>()
    private val food by lazy {
        ImageView(requireContext()).apply {
            this.layoutParams = FrameLayout.LayoutParams(HEAD_SIZE, HEAD_SIZE)
            this.setImageResource(R.drawable.circle)
        }
    }
    private val head by lazy {
        ImageView(requireContext())
            .apply {
                this.layoutParams = FrameLayout.LayoutParams(HEAD_SIZE, HEAD_SIZE)
                this.setImageResource(R.drawable.snake_head)
            }
    }
    private var currentDirection = Direction.BOTTOM

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        disableBackPress()

        binding = FragmentGameBinding.bind(view)

        with(binding) {
            container.layoutParams = LinearLayout.LayoutParams(
                displayMetrics.widthPixels,
                HEAD_SIZE * CELLS_ON_FIELD,
            )


            val gameSpeed = arguments?.getLong(GAME_SPEED)
            SnakeCore.gameSpeed = gameSpeed ?: 500L
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
                val dialog = AlertDialog.Builder(requireContext())
                dialog.setTitle(getString(R.string.pause))
                    .setPositiveButton(getString(R.string.continue_game)) { _, _ ->
                        isPlay = !isPlay
                    }
                    .setNegativeButton(getString(R.string.exit)) { _, _ ->
                        view.findNavController().popBackStack()
                    }
                    .setCancelable(false)
                    .create()
                    .show()
                isPlay = !isPlay
            }
        }
    }

    private fun checkIfCurrentDirectionIsNotOpposite(
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
        requireActivity().runOnUiThread {
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
        }
    }

    private fun addPartOfTale(top: Int, left: Int) {
        val talePart = drawPartOfTale(top, left)
        allTale.add(PartOfTale(ViewCoordinates(top, left), talePart))
    }

    private fun drawPartOfTale(top: Int, left: Int): ImageView {
        val taleImage = ImageView(requireContext())
        taleImage.setImageResource(R.drawable.snake_scales)
        taleImage.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.reed_green))
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
        requireActivity().runOnUiThread {
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
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.your_score) + allTale.size)
            .setPositiveButton(getString(R.string.Restart)) { _, _ ->
                requireActivity().recreate()
            }
            .setNegativeButton(getString(R.string.exit)) { _, _ ->
                view?.findNavController()?.popBackStack()
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
    }

    private fun disableBackPress() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {}
            })
    }
}

enum class Direction {
    UP,
    RIGHT,
    BOTTOM,
    LEFT
}