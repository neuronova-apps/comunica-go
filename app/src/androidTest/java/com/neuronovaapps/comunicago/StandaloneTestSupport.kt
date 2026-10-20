package com.neuronovaapps.comunicago

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeContentTestRule

internal fun ComposeContentTestRule.awaitCommunicationCatalog() {
    waitUntil(15_000) { onAllNodesWithTag("communication-bottom-bar").fetchSemanticsNodes().isNotEmpty() }
    waitForIdle()
}
internal fun ComposeContentTestRule.openCommunicationCategories() {
    onNodeWithTag("communication-destination-HOME").performClick()
    if (onAllNodesWithTag("communication-category-grid").fetchSemanticsNodes().isEmpty()) {
        onNodeWithTag("communication-home").performScrollToKey("explore")
        onNodeWithTag("communication-explore").performClick()
    }
}
internal fun ComposeContentTestRule.selectCommunicationCategory(id: String?) {
    openCommunicationCategories()
    onNodeWithTag("communication-category-grid").performScrollToKey("categories")
    val tag = "communication-category-" + (id ?: "all")
    if (onAllNodesWithTag(tag).fetchSemanticsNodes().isEmpty()) {
        onNodeWithTag("communication-category-toggle").performScrollTo().performClick()
    }
    onNodeWithTag(tag).performScrollTo().performClick()
}
internal fun ComposeContentTestRule.scrollCommunicationTo(key: String) {
    if (key in setOf("presentation", "personalize", "back")) {
        onNodeWithTag("communication-destination-SETTINGS").performClick()
        onNodeWithTag(when (key) {
            "presentation" -> "communication-presentation-GUIDED"
            "personalize" -> "communication-personalize"
            else -> "communication-back-to-modules"
        }).performScrollTo()
    } else if (key.startsWith("VOC_") || key in setOf("categories", "count", "vocabulary-title")) {
        openCommunicationCategories()
        onNodeWithTag("communication-category-grid").performScrollToKey(key)
    } else {
        onNodeWithTag("communication-destination-HOME").performClick()
        if (onAllNodesWithTag("communication-view-full-message").fetchSemanticsNodes().isNotEmpty()) {
            onNodeWithTag("communication-view-full-message").performClick()
        }
        onNodeWithTag("communication-home").performScrollToKey(key)
    }
}