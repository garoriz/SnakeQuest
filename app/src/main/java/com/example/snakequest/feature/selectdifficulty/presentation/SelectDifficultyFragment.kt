package com.example.snakequest.feature.selectdifficulty.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import com.example.snakequest.R
import com.example.snakequest.databinding.FragmentSelectDifficultyBinding

const val GAME_SPEED = "GAME_SPEED"
const val GAME_SPEED_VAL_1 = 500L
const val GAME_SPEED_VAL_2 = 300L
const val GAME_SPEED_VAL_3 = 200L

class SelectDifficultyFragment : Fragment(R.layout.fragment_select_difficulty) {
    private lateinit var binding: FragmentSelectDifficultyBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSelectDifficultyBinding.bind(view)

        with(binding) {
            btn1.setOnClickListener {
                view.findNavController().navigate(
                    R.id.action_selectDifficultyFragment_to_gameFragment,
                    bundleOf(GAME_SPEED to GAME_SPEED_VAL_1)
                )
            }
            btn2.setOnClickListener {
                view.findNavController().navigate(
                    R.id.action_selectDifficultyFragment_to_gameFragment,
                    bundleOf(GAME_SPEED to GAME_SPEED_VAL_2)
                )
            }
            btn3.setOnClickListener {
                view.findNavController().navigate(
                    R.id.action_selectDifficultyFragment_to_gameFragment,
                    bundleOf(GAME_SPEED to GAME_SPEED_VAL_3)
                )
            }
        }
    }
}