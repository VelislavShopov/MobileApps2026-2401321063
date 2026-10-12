package com.example.padelconnect.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.padelconnect.ui.create_match.CreateMatchScreen
import com.example.padelconnect.ui.home.HomeScreen
import com.example.padelconnect.ui.match_list.MatchListScreen

object Routes{
    const val HOME = "home"
    const val CREATE_MATCH = "create_match"
    const val MATCH_LIST = "match_list"
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
                onViewMatchesClick = {
                    navigationController.navigate(Routes.MATCH_LIST)
                }
            )
        }

        composable(Routes.MATCH_LIST) {
            MatchListScreen(
                onNavigateToCreateMatch = {
                    navigationController.navigate(Routes.CREATE_MATCH)
                }
            )
        }

        composable(Routes.CREATE_MATCH) {
            CreateMatchScreen ({ navigationController.popBackStack() })

        }
    }


}