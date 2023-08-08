package com.arshapshap.surftraineetask.presentation.screens.details

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import androidx.annotation.AnimRes
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import com.arshapshap.surftraineetask.R
import com.arshapshap.surftraineetask.common.base.BaseFragment
import com.arshapshap.surftraineetask.common.base.ViewModelErrorLevel
import com.arshapshap.surftraineetask.common.di.appComponent
import com.arshapshap.surftraineetask.common.di.lazyViewModel
import com.arshapshap.surftraineetask.common.extensions.showAlert
import com.arshapshap.surftraineetask.common.extensions.showToast
import com.arshapshap.surftraineetask.databinding.FragmentDetailsBinding
import java.lang.StringBuilder

class DetailsFragment : BaseFragment<FragmentDetailsBinding, DetailsScreenViewModel>(
    FragmentDetailsBinding::inflate
) {

    companion object {

        fun createBundle(cocktailId: Long): Bundle {
            return bundleOf(COCKTAIL_ID_KEY to cocktailId)
        }

        private const val COCKTAIL_ID_KEY = "COCKTAIL_ID_KEY"
    }

    override val viewModel: DetailsScreenViewModel by lazyViewModel {
        requireContext().appComponent().detailsScreenViewModel().create(
            arguments?.getLong(COCKTAIL_ID_KEY)
        )
    }

    override fun inject() {
        requireContext().appComponent().inject(this)
    }

    override fun initViews() { }

    override fun subscribe() {
        with (viewModel) {
            viewModel.error.observe(viewLifecycleOwner) {
                when (it.level) {
                    ViewModelErrorLevel.Error -> showAlert(
                        title = getString(R.string.error),
                        message = getString(it.messageRes)
                    ) {
                        viewModel.closeFragment()
                    }

                    ViewModelErrorLevel.Message -> showToast(
                        message = getString(it.messageRes)
                    )
                }
            }

            isLoading.observe(viewLifecycleOwner) {
                with (binding) {
                    if (!it) {
                        detailsCardView.animate(R.anim.fade_in, R.anim.slide_up)
                        cocktailImageView.animate(R.anim.fade_in)
                    }
                    detailsCardView.isGone = it
                    cocktailImageView.isGone = it
                }
            }

            cocktail.observe(viewLifecycleOwner) {
                with (binding) {
                    // TODO: загрузка изображения
                    cocktailImageView.setImageResource(R.drawable.img_summer_holidays)

                    nameTextView.text = it.name

                    descriptionTextView.text = it.description
                    descriptionTextView.isGone = it.description.isBlank()

                    ingredientsTextView.text = getIngredientsString(it.ingredients)

                    recipeSubtitleTextView.isGone = it.recipe.isBlank()
                    recipeTextView.isGone = it.recipe.isBlank()
                    recipeTextView.text = it.recipe

                    editButton.setOnClickListener {
                        // TODO: переход на страницу редактирования
                    }
                }
            }

            loadDetails()
        }
    }

    private fun View.animate(@AnimRes vararg animations: Int) {
        val animationSet = AnimationSet(true)

        animations.forEach {
            animationSet.addAnimation(AnimationUtils.loadAnimation(requireContext(), it))
        }

        startAnimation(animationSet)
    }

    private fun getIngredientsString(ingredients: List<String>): String {
        val stringBuilder = StringBuilder()
        ingredients.forEachIndexed { i, it ->
            stringBuilder.append(it)
            if (i != ingredients.size - 1)
                stringBuilder.append("\n—\n")
        }
        return stringBuilder.toString()
    }
}