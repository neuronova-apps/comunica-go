package com.neuronovaapps.comunicago

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.neuronovaapps.comunicago.data.preferences.TextScaleMode
import com.neuronovaapps.comunicago.data.preferences.ThemeMode
import com.neuronovaapps.comunicago.ui.screens.welcome.WelcomeScreen
import com.neuronovaapps.comunicago.ui.theme.ComunicaGoTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class WelcomeAccessibilityTest {
    @get:Rule val rule = createComposeRule()
    private data class Scenario(val theme: ThemeMode = ThemeMode.DAY, val dark: Boolean = false,
        val contrast: Boolean = false, val scale: TextScaleMode = TextScaleMode.NORMAL,
        val width: Int = 400, val height: Int = 760)

    @Test fun startStaysVisibleAtTheBottomAcrossThemesScalesAndSmallScreens() {
        var scenario by mutableStateOf(Scenario())
        var starts = 0
        rule.setContent {
            val current = scenario
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, 1.6f)) {
                ComunicaGoTheme(current.theme.usesDarkTheme(current.dark), current.scale.scaleFactor, current.contrast) {
                    Box(Modifier.requiredSize(current.width.dp, current.height.dp)) {
                        key(current) { WelcomeScreen { starts++ } }
                    }
                }
            }
        }
        val appearances = listOf(Triple(ThemeMode.DAY, false, false), Triple(ThemeMode.NIGHT, false, false),
            Triple(ThemeMode.SYSTEM, false, false), Triple(ThemeMode.SYSTEM, true, false), Triple(ThemeMode.DAY, false, true))
        var expectedStarts = 0
        appearances.forEach { (theme, dark, contrast) ->
            TextScaleMode.entries.forEach { scale ->
                listOf(400 to 760, 320 to 480, 480 to 320).forEach { (width, height) ->
                    rule.runOnIdle { scenario = Scenario(theme, dark, contrast, scale, width, height) }
                    val action = rule.onNodeWithTag("welcome-start").assertIsDisplayed().assertHasClickAction()
                        .assertHeightIsAtLeast(48.dp).assertWidthIsAtLeast(48.dp)
                    val bounds = action.fetchSemanticsNode().boundsInRoot
                    val root = rule.onNodeWithTag("comunicago-welcome").fetchSemanticsNode().boundsInRoot
                    val brand = rule.onNodeWithText("NeuroNova Apps").assertIsDisplayed().fetchSemanticsNode().boundsInRoot
                    assertTrue("Start remains in the lower part: $scenario", bounds.bottom >= root.top + root.height * .55f)
                    assertTrue("Action does not overlap the brand: $scenario", bounds.bottom <= brand.top)
                    assertTrue("Action stays within the screen: $scenario", bounds.top >= root.top && bounds.bottom <= root.bottom)
                    rule.onNodeWithText("ComunicaGo").performScrollTo()
                        .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading))
                    action.assertIsDisplayed().performClick()
                    expectedStarts++
                    rule.runOnIdle { assertEquals(expectedStarts, starts) }
                }
            }
        }
    }
}
