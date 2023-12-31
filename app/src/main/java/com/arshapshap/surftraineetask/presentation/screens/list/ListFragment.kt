package com.arshapshap.surftraineetask.presentation.screens.list

import android.content.Intent
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import com.arshapshap.surftraineetask.R
import com.arshapshap.surftraineetask.databinding.FragmentListBinding
import com.arshapshap.surftraineetask.presentation.screens.list.recyclerview.CocktailsAdapter
import com.arshapshap.surftraineetask.utils.base.BaseFragment
import com.arshapshap.surftraineetask.utils.di.appComponent
import com.arshapshap.surftraineetask.utils.di.lazyViewModel
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable


class ListFragment : BaseFragment<FragmentListBinding, ListScreenViewModel>(
    FragmentListBinding::inflate
) {

    companion object {

        fun createBundle(cocktailToScrollId: Long): Bundle {
            return bundleOf(COCKTAIL_TO_SCROLL_ID_KEY to cocktailToScrollId)
        }

        private const val COCKTAIL_TO_SCROLL_ID_KEY = "COCKTAIL_TO_SCROLL_ID_KEY"
    }

    private var firstOpening = true

    override val viewModel: ListScreenViewModel by lazyViewModel {
        requireContext().appComponent().listScreenViewModel().create()
    }

    override fun inject() {
        requireContext().appComponent().inject(this)
    }

    override fun initViews() {
        setBottomAppBarBackground()
        with (binding) {
            cocktailsRecyclerView.adapter = CocktailsAdapter() {
                viewModel.openCocktailDetails(it)
            }
            addButton.setOnClickListener {
                viewModel.createCocktail()
            }
            shareImageView.setOnClickListener {
                onShareClick()
            }
        }
    }

    private fun setBottomAppBarBackground() {
        val radius = resources.getDimension(R.dimen.bottomappbar_corners_radius)
        val bottomBarBackground = binding.bottomAppBar.background as MaterialShapeDrawable
        bottomBarBackground.shapeAppearanceModel =
            bottomBarBackground.shapeAppearanceModel.toBuilder()
                .setTopRightCorner(CornerFamily.ROUNDED, radius)
                .setTopLeftCorner(CornerFamily.ROUNDED, radius).build()
    }

    override fun subscribe() {
        with (viewModel) {
            isLoading.observe(viewLifecycleOwner) {
                binding.loadingProgressBar.isGone = !it
                if (it) {
                    binding.noCocktailsGroup.isGone = true
                    binding.cocktailsRecyclerView.isGone = true
                }
            }

            cocktails.observe(viewLifecycleOwner) {
                binding.noCocktailsGroup.isGone = it.isNotEmpty()
                binding.cocktailsRecyclerView.isGone = it.isEmpty()
                binding.shareImageView.isGone = it.isEmpty()
                getCocktailsAdapter().setList(it)

                if (firstOpening) {
                    binding.cocktailsRecyclerView.scrollToPosition(it.indexOfFirst { it.id == arguments?.getLong(COCKTAIL_TO_SCROLL_ID_KEY) })
                    firstOpening = false
                }
            }

            loadCocktails()
        }
    }

    private fun getCocktailsAdapter(): CocktailsAdapter
        = binding.cocktailsRecyclerView.adapter as CocktailsAdapter

    private fun onShareClick() {
        val cocktails = viewModel.cocktails.value?.reversed()?.take(4) ?: return
        val shareText = generateShareText(cocktails.map { it.name })

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    private fun generateShareText(cocktails: List<String>): String {
        val displayedCocktails = cocktails.joinToString(", ")

        return getString(R.string.share_message, displayedCocktails)
    }
}