package com.neuronovaapps.comunicago.ui.screens.communication.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.neuronovaapps.comunicago.data.communication.*
import com.neuronovaapps.comunicago.domain.communication.*
import com.neuronovaapps.comunicago.ui.theme.LocalHighContrast

internal val LocalCommunicationPictograms = staticCompositionLocalOf<CommunicationPictograms?> { null }
internal val PictogramStatusKey = SemanticsPropertyKey<CommunicationPictogramStatus>("CommunicationPictogramStatus")
internal var SemanticsPropertyReceiver.pictogramStatus by PictogramStatusKey

/** Curated release: only the bank's explicitly validated pairs may display. */
internal object CommunicationPictogramDisplayReview {
    val approved: Boolean get() = true
}

/** Decorative local image. The surrounding card/row owns the label and every action. */
@Composable
internal fun CommunicationPictogram(word: CommunicationVocabularyItem, height: Dp, modifier: Modifier = Modifier) {
    if (!CommunicationPictogramDisplayReview.approved) return
    val id = word.validatedPictogramId() ?: return
    val context = LocalContext.current.applicationContext
    val injected = LocalCommunicationPictograms.current
    val repository = remember(context, injected) { injected ?: DefaultCommunicationPictograms.get(context) }
    var image by remember(repository, id, word.vocabId) {
        mutableStateOf(CommunicationPictogramImage(CommunicationPictogramStatus.NOT_STORED))
    }
    LaunchedEffect(repository, id, word.vocabId) {
        repository.observe(id).collect { image = it }
    }
    val file = image.file
    var decoded by remember(file) { mutableStateOf(false) }
    var failed by remember(file) { mutableStateOf(false) }
    val displayed = when {
        failed -> CommunicationPictogramStatus.FAILED
        file != null && !decoded -> CommunicationPictogramStatus.LOADING
        else -> image.status
    }
    val request = remember(file, context) {
        file?.let { ImageRequest.Builder(context).data(it).diskCachePolicy(CachePolicy.DISABLED).build() }
    }
    Surface(modifier.fillMaxWidth().height(height).testTag("communication-pictogram-${word.vocabId}")
        .semantics { pictogramStatus = displayed },
        color = if (file != null) Color.White else MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(if (LocalHighContrast.current) 2.dp else 1.dp, MaterialTheme.colorScheme.outline),
        shape = MaterialTheme.shapes.small) {
        if (request != null && !failed) AsyncImage(model = request, contentDescription = null,
            contentScale = ContentScale.Fit, modifier = Modifier.fillMaxSize().padding(4.dp),
            onSuccess = { decoded = true }, onError = { failed = true })
    }
}
