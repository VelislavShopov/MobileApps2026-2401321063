package com.example.padelconnect

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class CreateMatchFlowTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun userNavigatesToCreateMatch_fillsCompleteForm_andSubmitsSuccessfully() {
        // 1. Navigate from Home -> Create Match Screen
        composeTestRule.onNodeWithText("Quick Actions").assertIsDisplayed()
        composeTestRule.onNodeWithText("Create a Match").performClick()

        // 2. Verify top app bar title on Create Match screen
        composeTestRule.onNodeWithText("Create Padel Match").assertIsDisplayed()

        // 3. Verify submit button is initially disabled (form is incomplete)
        composeTestRule.onNodeWithText("Create Match").assertIsNotEnabled()

        // 4. Pin location on map
        composeTestRule.onNodeWithTag("map", useUnmergedTree = true)
            .performClick()

        // 5. Open the Date Picker dialog
        composeTestRule.onNodeWithTag("date_select", useUnmergedTree = true).performClick()
        composeTestRule.onNodeWithText("Confirm", useUnmergedTree = true).performClick()

        // 6. Select Start Time via TimePickerDialog
        composeTestRule.onNodeWithText("Select Start Time").performClick()
        composeTestRule.onNodeWithText("Confirm").performClick()

        // 7. Select End Time via TimePickerDialog
        composeTestRule.onNodeWithText("Select End Time").performClick()
        composeTestRule.onNodeWithText("Confirm").performClick()

        // 8. Enter Creator Name
        composeTestRule.onNodeWithText("Your Name / Host Name")
            .performTextInput("Alex")

        // 9. Verify the submit button is now ENABLED
        composeTestRule.onNodeWithText("Create Match").assertIsEnabled()

        composeTestRule.onNodeWithText("Create Match").performScrollTo()

        // 10. Click Create Match and verify navigation back to Home
        composeTestRule.onNodeWithText("Create Match").performClick()

        // 11. Check whether home page has loaded
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule
                .onAllNodesWithText("Quick Actions")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
    }
}