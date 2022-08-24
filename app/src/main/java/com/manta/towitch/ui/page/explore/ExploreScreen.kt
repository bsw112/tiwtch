package com.manta.towitch.ui.page.explore

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.*
import com.google.android.material.math.MathUtils.lerp
import com.manta.towitch.R
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.ColorFilter
import com.manta.towitch.common.HCenter
import com.manta.towitch.common.HSpacer
import com.manta.towitch.common.VSpacer
import com.manta.towitch.data.entity.Stream
import com.manta.towitch.ui.page.home.MainViewModel
import com.manta.towitch.ui.theme.Black
import com.manta.towitch.ui.theme.White
import com.manta.towitch.ui.theme.title_tab
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ExploreScreen(mainViewModel: MainViewModel) {
    val streams = mainViewModel.recommendedStream.collectAsState()
    val pagerState = rememberPagerState(streams.value.size * 5)
    val scope = rememberCoroutineScope()

    if (streams.value.isNotEmpty()) {
        val currentStream = streams.value[pagerState.currentPage % streams.value.size]

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            item {
                Text(
                    "찾기",
                    fontSize = title_tab,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(all = 15.dp)
                )
                VSpacer(dp = 50.dp)
                if (streams.value.isNotEmpty()) {
                    DiscreteScrollView(streams.value.take(8), pagerState, scope)
                }
                VSpacer(dp = 20.dp)
                Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(color = Black)) {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(currentStream.userName)
                            }
                            append(" 방송 중 ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(currentStream.gameName)
                            }
                        }
                    },
                    modifier = Modifier.padding(horizontal = 15.dp)
                )
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 15.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    listOf("게임", "리얼라이프", "e스포츠", "음악", "크리에이티브").forEach {
                        Button(
                            onClick = { /*TODO*/ }, modifier = Modifier
                                .padding(horizontal = 3.dp, vertical = 5.dp)
                                .width(150.dp)
                        ) {
                            HCenter {
                                Text(it, color = White)
                                Spacer(Modifier.weight(1f))
                                Image(
                                    painter = painterResource(id = R.drawable.ic_baseline_search_24),
                                    contentDescription = "",
                                    colorFilter = ColorFilter.tint(White)
                                )
                            }
                        }
                    }

                }
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun DiscreteScrollView(streams: List<Stream>, pagerState: PagerState, scope: CoroutineScope) {
    LaunchedEffect(pagerState) {
        scope.launch {
            pagerState.scrollToPage(3)
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxWidth()
    ) { page ->
        Box(
            Modifier
                .graphicsLayer {
                    // Calculate the absolute offset for the current page from the
                    // scroll position. We use the absolute value which allows us to mirror
                    // any effects for both directions
                    val pageOffset = calculateCurrentOffsetForPage(page).absoluteValue

                    lerp(0.85f, 1f, 1f - pageOffset.coerceIn(0f, 1f))
                        .also { scale ->
                            scaleX = scale
                            scaleY = scale

                        }

                    alpha = lerp(0.5f, 1f, 1f - pageOffset.coerceIn(0f, 1f))
                }
        ) {
            GlideImage(
                imageModel = streams[page % streams.size].getSizedThumbnailUrl(1024, 512),
                modifier = Modifier
                    .width(300.dp)
                    .height(150.dp),
                contentScale = ContentScale.FillWidth
            ) {

            }
        }
    }


}