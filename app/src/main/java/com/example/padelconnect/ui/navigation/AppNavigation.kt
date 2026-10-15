package com.example.padelconnect.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.padelconnect.ui.create_match.CreateMatchScreen
import com.example.padelconnect.ui.edit_match.EditMatchScreen
import com.example.padelconnect.ui.home.HomeScreen
import com.example.padelconnect.ui.match_list.MatchFilter
import com.example.padelconnect.ui.match_list.MatchListScreen

object Routes{
    const val HOME = "home"
    const val CREATE_MATCH = "create_match"
    const val MATCH_LIST = "match_list"
    const val MY_MATCHES = "my_matches"
    const val EDIT_MATCH = "edit_match"
}

@Composable
fun AppNavigation(){
    val navigationController = rememberNavController()

    NavHost(
        navigationController,
        Routes.HOME
    ){
        composable(Routes.HOME){
            HomeScreen(
                onCreateMatchClick = {
                    navigationController.navigate(Routes.CREATE_MATCH)
                },
                onNavigateToEditMatchClick = { matchToEdit ->
                    navigationController.navigate("${Routes.EDIT_MATCH}/${matchToEdit.id}")
                },
                onViewMatchesClick = {
                    navigationController.navigate(Routes.MATCH_LIST)
                },
                onViewMyMatchesClick = {
                    navigationController.navigate(Routes.MY_MATCHES)
                }
            )
        }

        composable(Routes.MATCH_LIST) {
            MatchListScreen(
                onNavigateToCreateMatch = {
                    navigationController.navigate(Routes.CREATE_MATCH)
                },
                onNavigateToEditMatchClick = { matchToEdit ->
                    navigationController.navigate("${Routes.EDIT_MATCH}/${matchToEdit.id}")
                },
                onNavigateBack = {
                    navigationController.popBackStack()
                }
            )
        }

        composable(Routes.MY_MATCHES) {
            MatchListScreen(
                initialFilter = MatchFilter.MY_MATCHES,
                onNavigateToCreateMatch = {
                    navigationController.navigate(Routes.CREATE_MATCH)
                },
                onNavigateToEditMatchClick = { matchToEdit ->
                    navigationController.navigate("${Routes.EDIT_MATCH}/${matchToEdit.id}")
                },
                onNavigateBack = {
                    navigationController.popBackStack()
                }
            )
        }

        composable(Routes.CREATE_MATCH) {
            CreateMatchScreen ({ navigationController.popBackStack() })
        }

        composable(
            route = "${Routes.EDIT_MATCH}/{matchId}",
            arguments = listOf(navArgument("matchId") { type = NavType.StringType })
        ) {
            EditMatchScreen(
                onNavigateBack = { navigationController.popBackStack() }
            )
        }
    }


}