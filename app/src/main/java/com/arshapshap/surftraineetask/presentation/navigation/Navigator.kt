package com.arshapshap.surftraineetask.presentation.navigation

import androidx.navigation.NavController
import com.arshapshap.surftraineetask.R
import com.arshapshap.surftraineetask.presentation.screens.details.DetailsFragment
import com.arshapshap.surftraineetask.presentation.screens.editing.EditingFragment
import com.arshapshap.surftraineetask.presentation.screens.list.ListFragment

class Navigator : MainRouter {

    private var navController: NavController? = null

    fun attachNavController(navController: NavController, graph: Int) {
        navController.setGraph(graph)
        this.navController = navController
    }

    fun detachNavController(navController: NavController) {
        if (this.navController == navController) {
            this.navController = null
        }
    }

    override fun openCocktailsListWithScroll(id: Long) {
        if (!canNavigate(R.id.editingFragment, R.id.listFragment))
            return
        navController?.navigate(R.id.action_editingFragment_to_listFragment, ListFragment.createBundle(id))
    }

    override fun openCocktailDetails(id: Long) {
        if (!canNavigate(R.id.listFragment, R.id.detailsFragment))
            return
        navController?.navigate(R.id.action_listFragment_to_detailsFragment, DetailsFragment.createBundle(id))
    }

    override fun openCocktailEditing(id: Long) {
        if (!canNavigate(R.id.detailsFragment, R.id.editingFragment))
            return
        navController?.navigate(R.id.action_detailsFragment_to_editingFragment, EditingFragment.createBundle(id))
    }

    override fun openCocktailCreating() {
        if (!canNavigate(R.id.listFragment, R.id.editingFragment))
            return
        navController?.navigate(R.id.action_listFragment_to_editingFragment)
    }

    override fun closeCurrentFragment() {
        if (navController?.currentDestination == null)
            return
        navController?.popBackStack()
    }

    private fun canNavigate(startDestination: Int, endDestination: Int)
        = navController?.currentDestination?.id == startDestination
            && navController?.currentDestination?.id != endDestination
}