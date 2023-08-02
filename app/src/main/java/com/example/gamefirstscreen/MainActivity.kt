package com.example.gamefirstscreen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import coil.size.Scale
import com.example.gamefirstscreen.ui.FocusableComposable
import com.example.gamefirstscreen.ui.theme.GameFirstScreenTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import java.lang.Integer.min


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GameFirstScreenTheme {

                val mainViewModel by viewModels<MainViewModel>()
                mainViewModel.getAllMovies()

                var selectedMovie by remember { mutableStateOf<Movies?>(null) }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        SliderView(viewModel = mainViewModel, onMovieSelected = { movie ->
                            selectedMovie = movie
                        })
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun SliderView(viewModel: MainViewModel, onMovieSelected: (Movies) -> Unit) {
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Show details of selected movie
        var selectedMovie by remember { mutableStateOf<Movies?>(null) }

        val movieList = viewModel.movieListResponse
        if (movieList.isNotEmpty() && selectedMovie == null) {
            selectedMovie = movieList[0] // Select the first movie as the default selected movie
        }

        Row(
            modifier = Modifier
                .padding(16.dp)
        ) {
            // Phần bên trái hiển thị thông tin chi tiết của phim
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
                    .align(CenterVertically)
            ) { selectedMovie?.let { movie -> MovieDetails(movie = movie) } }

            // Phần bên phải hiển thị hình ảnh của phim
            selectedMovie?.let { movie ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(0.8f)
                ) {
                    val painter = rememberImagePainter(
                        data = movie.imageUrl,
                        builder = {
                            placeholder(R.drawable.placeholder)
                            scale(Scale.FILL)
                        })
                    Image(
                        painter = painter,
                        contentDescription = "",
                        Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            FocusableComposable() { focused ->
                ElevatedCard(
                    modifier = Modifier.padding(10.dp),
                    shape = CircleShape,
                    colors = with(MaterialTheme.colorScheme) {
                        cardColors(
                            containerColor = if (focused) primaryContainer else surface,
                            contentColor = onPrimaryContainer
                        )
                    }
                )
                {
                    Icon(
                        imageVector = Icons.Filled.ChevronLeft,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier
                            .clickable {
                                if (pagerState.currentPage > 0) {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                    }
                                }
                            }
                            .size(54.dp)
                            .clip(CircleShape)
                    )
                }
            }


            // HorizontalPager
            Box(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            ) {
                HorizontalPager(
                    state = pagerState,
                    count = (viewModel.movieListResponse.size + 3) / 4,
                    modifier = Modifier.fillMaxSize()
                ) { pageIndex ->
                    val startIndex = pageIndex * 4
                    val endIndex = min(startIndex + 4, viewModel.movieListResponse.size)
                    val moviesToDisplay =
                        viewModel.movieListResponse.subList(startIndex, endIndex)

                    // Sử dụng MovieRow và truyền danh sách phim và hàm onMovieSelected vào
                    MovieRow(
                        movies = moviesToDisplay,
                        selectedMovie = selectedMovie,
                        onMovieSelected = { movie ->
                            selectedMovie = movie
                        }
                    )
                }
            }

            //Next arrow
            FocusableComposable() { focused ->
                ElevatedCard(
                    modifier = Modifier.padding(10.dp),
                    shape = CircleShape,
                    colors = with(MaterialTheme.colorScheme) {
                        cardColors(
                            containerColor = if (focused) primaryContainer else surface,
                            contentColor = onPrimaryContainer
                        )
                    }
                )
                {
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier
                            .clickable {
                                if (pagerState.currentPage < pagerState.pageCount - 1) {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                    }
                                }
                            }
                            .size(54.dp)
                            .clip(CircleShape)
                    )
                }
            }

        }

        DotsIndicator(
            pagerState = pagerState,
            activeDotColor = MaterialTheme.colorScheme.secondary,
            inactiveDotColor = Color.Gray,
            dotSize = 12.dp,
            padding = 8.dp
        )
    }
}

@Composable
fun MovieDetails(movie: Movies) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).align(End),
            horizontalArrangement = Arrangement.End // Đặt các Text về phía bên phải
        ) {
            Text(
                text = "Tên phim: ${movie.name}",
                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                textAlign = TextAlign.End
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).align(End),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "Thể loại: ${movie.category}",
                style = TextStyle(fontSize = 14.sp),
                textAlign = TextAlign.End)
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).align(End),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "Mô tả: ${movie.desc}",
                style = TextStyle(fontSize = 14.sp),
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
fun MovieRow(movies: List<Movies>, selectedMovie: Movies?, onMovieSelected: (Movies) -> Unit) {
    Row(modifier = Modifier.fillMaxSize()) {
        for (movie in movies) {
            MovieItem(
                movie = movie,
                isSelected = movie == selectedMovie,
                onMovieClick = {
                    onMovieSelected(movie)
                }
            )
        }
    }
}

@Composable
fun MovieItem(movie: Movies, isSelected: Boolean, onMovieClick: () -> Unit) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val spacing = 8.dp // Khoảng cách giữa các item trong MovieRow
    val itemWidth = (screenWidth - (spacing * 5) - 54.dp - 54.dp) / 4

    Column(
        modifier = Modifier
            .clickable(onClick = onMovieClick)
            .width(itemWidth)
            .fillMaxHeight()
            .padding(spacing),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.BottomCenter) {
            val painter = rememberImagePainter(
                data = movie.imageUrl,
                builder = {
                    placeholder(R.drawable.placeholder)
                    scale(Scale.FILL)
                })
            Image(
                painter = painter,
                contentDescription = "",
                Modifier
                    .padding(spacing)
                    .clip(RoundedCornerShape(20.dp))
                    .fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Text(
                text = movie.name,
                Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(8.dp)
                    .background(if (isSelected) Color.Red else Color.LightGray.copy(alpha = 0.60F))
                    .padding(8.dp),
                textAlign = TextAlign.Start,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun DotsIndicator(pagerState: PagerState, activeDotColor: Color, inactiveDotColor: Color, dotSize: Dp, padding: Dp) {

    val pageCount = pagerState.pageCount

    Row(
        horizontalArrangement = Arrangement.spacedBy(dotSize / 2),
        verticalAlignment = CenterVertically,
        modifier = Modifier.padding(padding)
    ) {
        for (pageIndex in 0 until pageCount) {
            val color = if (pageIndex == pagerState.currentPage) activeDotColor else inactiveDotColor
            Dot(color, dotSize)
        }
    }
}

@Composable
fun Dot(color: Color, size: Dp) {
    Box(modifier = Modifier
        .size(size)
        .clip(CircleShape)
        .background(color)
        .padding(4.dp) // Padding to create distance between dots
    )
}


//@Composable
//fun DotsIndicator(totalDots: Int, selectedIndex: Int) {
//    LazyRow(
//        modifier = Modifier
//            .fillMaxWidth()
//            .wrapContentHeight(),
//        horizontalArrangement = Arrangement.Center
//    ) {
//        items(totalDots) { index ->
//            if (index == selectedIndex) {
//                Box(
//                    modifier = Modifier
//                        .size(10.dp)
//                        .clip(CircleShape)
//                        .background(color = Color.DarkGray)
//                )
//            } else {
//                Box(
//                    modifier = Modifier
//                        .size(10.dp)
//                        .clip(CircleShape)
//                        .background(color = Color.LightGray)
//                )
//            }
//
//            if (index != totalDots - 1) {
//                Spacer(modifier = Modifier.padding(horizontal = 2.dp))
//            }
//        }
//    }
//}


//@OptIn(ExperimentalPagerApi::class)
//@Composable
//fun PagerIndicator(
//    pagerState: PagerState,
//    indicatorCount: Int = 5,
//    indicatorSize: Dp = 16.dp,
//    indicatorShape: Shape = CircleShape,
//    space: Dp = 8.dp,
//    activeColor: Color = Color(0xffEC407A),
//    inActiveColor: Color = Color.LightGray,
//    onClick: ((Int) -> Unit)? = null
//) {
//
//    val listState = rememberLazyListState()
//
//    val totalWidth: Dp = indicatorSize * indicatorCount + space * (indicatorCount - 1)
//    val widthInPx = LocalDensity.current.run { indicatorSize.toPx() }
//
//    val currentItem by remember {
//        derivedStateOf {
//            pagerState.currentPage
//        }
//    }
//
//    val itemCount = pagerState.pageCount
//
//    LaunchedEffect(key1 = currentItem) {
//        val viewportSize = listState.layoutInfo.viewportSize
//        listState.animateScrollToItem(
//            currentItem,
//            (widthInPx / 2 - viewportSize.width / 2).toInt()
//        )
//    }
//
//    LazyRow(
//        modifier = Modifier.width(totalWidth),
//        state = listState,
//        contentPadding = PaddingValues(vertical = space),
//        horizontalArrangement = Arrangement.spacedBy(space),
//        userScrollEnabled = false
//    ) {
//
//        items(itemCount) { index ->
//
//            val isSelected = (index == currentItem)
//
//            // Index of item in center when odd number of indicators are set
//            // for 5 indicators this is 2nd indicator place
//            val centerItemIndex = indicatorCount / 2
//
//            val right1 =
//                (currentItem < centerItemIndex &&
//                        index >= indicatorCount - 1)
//
//            val right2 =
//                (currentItem >= centerItemIndex &&
//                        index >= currentItem + centerItemIndex &&
//                        index <= itemCount - centerItemIndex + 1)
//            val isRightEdgeItem = right1 || right2
//
//            // Check if this item's distance to center item is smaller than half size of
//            // the indicator count when current indicator at the center or
//            // when we reach the end of list. End of the list only one item is on edge
//            // with 10 items and 7 indicators
//            // 7-3= 4th item can be the first valid left edge item and
//            val isLeftEdgeItem =
//                index <= currentItem - centerItemIndex &&
//                        currentItem > centerItemIndex &&
//                        index < itemCount - indicatorCount + 1
//
//            Box(
//                modifier = Modifier
//                    .graphicsLayer {
//                        val scale = if (isSelected) {
//                            1f
//                        } else if (isLeftEdgeItem || isRightEdgeItem) {
//                            .5f
//                        } else {
//                            .8f
//                        }
//                        scaleX = scale
//                        scaleY = scale
//
//                    }
//
//                    .clip(indicatorShape)
//                    .size(indicatorSize)
//                    .background(
//                        if (isSelected) activeColor else inActiveColor,
//                        indicatorShape
//                    )
//                    .then(
//                        if (onClick != null) {
//                            Modifier
//                                .clickable {
//                                    onClick.invoke(index)
//                                }
//                        } else Modifier
//                    )
//            )
//        }
//    }
//}
