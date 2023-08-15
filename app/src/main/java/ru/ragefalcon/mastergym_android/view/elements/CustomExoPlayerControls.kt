package ru.ragefalcon.mastergym_android.view.elements

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.Player.STATE_ENDED
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.delay
import ru.ragefalcon.mastergym_android.R
import ru.ragefalcon.mastergym_android.global.appConst
import ru.ragefalcon.mastergym_android.ui.theme.Purple200
import java.util.concurrent.TimeUnit

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun VideoPlayer(
    videoUri: String = "https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4",
    modifier: Modifier = Modifier,
    funcClose: () -> Unit = {}
) {

    val context = LocalContext.current

    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .apply {
                setSeekBackIncrementMs(PLAYER_SEEK_BACK_INCREMENT)
                setSeekForwardIncrementMs(PLAYER_SEEK_FORWARD_INCREMENT)
            }
            .build()
            .apply {
                setMediaItem(
                    MediaItem.Builder()
                        .apply {
                            setUri(videoUri)
                            setMediaMetadata(
                                MediaMetadata.Builder()
                                    .setDisplayTitle("")
                                    .build()
                            )
                        }
                        .build()
                )
                prepare()
                playWhenReady = true
                repeatMode = Player.REPEAT_MODE_ALL
            }
    }

    var shouldShowControls by remember { mutableStateOf(false) }

    var isPlaying by remember { mutableStateOf(exoPlayer.isPlaying) }

    var totalDuration by remember { mutableStateOf(0L) }

    var currentTime = remember { mutableStateOf(0L) }

    var bufferedPercentage by remember { mutableStateOf(0) }

    var playbackState by remember { mutableStateOf(exoPlayer.playbackState) }

    val speedSlowMo = remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        DisposableEffect(key1 = Unit) {
            val listener =
                object : Player.Listener {
                    override fun onEvents(
                        player: Player,
                        events: Player.Events
                    ) {
                        super.onEvents(player, events)
                        totalDuration = player.duration.coerceAtLeast(0L)
                        currentTime.value = player.currentPosition.coerceAtLeast(0L)
                        bufferedPercentage = player.bufferedPercentage
                        isPlaying = player.isPlaying
                        playbackState = player.playbackState
                    }
                }

            exoPlayer.addListener(listener)

            onDispose {
                exoPlayer.removeListener(listener)
                exoPlayer.release()
            }
        }
        LaunchedEffect(isPlaying) {
            while (isPlaying) {
                delay(500)
                currentTime.value = exoPlayer.currentPosition

            }
        }

        AndroidView(
            modifier =
            Modifier.clickable {
                shouldShowControls = shouldShowControls.not()
            }.clipToBounds(),
            factory = {
                PlayerView(context).apply {
                    player = exoPlayer
                    useController = false
                    layoutParams =
                        FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM // Заполнение с обрезкой
                }
            }
        )

        PlayerControls(
            modifier = Modifier.fillMaxSize(),
            isVisible = { shouldShowControls },
            isPlaying = { isPlaying },
            playbackState = { playbackState },
            onNormalSpeed = {
                exoPlayer.setPlaybackSpeed(1f)
            },
            setSlowMo = {
                exoPlayer.setPlaybackSpeed(0.25f)
            },
            onPauseToggle = {
                when {
                    exoPlayer.isPlaying -> {
                        exoPlayer.pause()
                    }

                    exoPlayer.isPlaying.not() &&
                            playbackState == STATE_ENDED -> {
                        exoPlayer.seekTo(0)
                        exoPlayer.playWhenReady = true
                    }

                    else -> {
                        // play the video
                        // it's already paused
                        exoPlayer.play()
                    }
                }
                isPlaying = isPlaying.not()
            },
            totalDuration = { totalDuration },
            currentTime = currentTime,
            bufferedPercentage = { bufferedPercentage },
            onSeekChanged = { timeMs: Float ->
                exoPlayer.seekTo(timeMs.toLong())
            },
            speedSlowMo = speedSlowMo,
            funcFullscreen = funcClose
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun PlayerControls(
    modifier: Modifier = Modifier,
    isVisible: () -> Boolean,
    isPlaying: () -> Boolean,
    speedSlowMo: MutableState<Boolean>,
    onNormalSpeed: () -> Unit,
    setSlowMo: () -> Unit,
    onPauseToggle: () -> Unit,
    totalDuration: () -> Long,
    currentTime: MutableState<Long>,
    bufferedPercentage: () -> Int,
    playbackState: () -> Int,
    onSeekChanged: (timeMs: Float) -> Unit,
    funcFullscreen: () -> Unit
) {

    val visible = remember(isVisible()) { isVisible() }

    AnimatedVisibility(
        modifier = modifier,
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(modifier = Modifier) {

            BottomControls(
                modifier =
                Modifier.align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .animateEnterExit(
                        enter =
                        slideInVertically(
                            initialOffsetY = { fullHeight: Int ->
                                fullHeight
                            }
                        ),
                        exit =
                        slideOutVertically(
                            targetOffsetY = { fullHeight: Int ->
                                fullHeight
                            }
                        )
                    ),
                totalDuration = totalDuration,
                currentTime = currentTime,
                bufferedPercentage = bufferedPercentage,
                onSeekChanged = onSeekChanged,
                isPlaying = isPlaying,
                setNormalSpeed = onNormalSpeed,
                setSlowMo = setSlowMo,
                speedSlowMo = speedSlowMo,
                onPauseToggle = onPauseToggle,
                playbackState = playbackState,
                funcFullscreen = funcFullscreen
            )
        }
    }
}

@Composable
private fun TopControl(modifier: Modifier = Modifier, title: () -> String) {
    val videoTitle = remember(title()) { title() }

    Text(
        modifier = modifier.padding(16.dp),
        text = videoTitle,
        style = MaterialTheme.typography.h6,
        color = Purple200
    )
}

@Composable
private fun CenterControls(
    modifier: Modifier = Modifier,
    isPlaying: () -> Boolean,
    playbackState: () -> Int,
    onReplayClick: () -> Unit,
    onPauseToggle: () -> Unit,
    onForwardClick: () -> Unit
) {
    val isVideoPlaying = remember(isPlaying()) { isPlaying() }

    val playerState = remember(playbackState()) { playbackState() }

    Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceEvenly) {
        IconButton(modifier = Modifier.size(40.dp), onClick = onReplayClick) {
            Image(
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                painter = painterResource(id = androidx.media3.ui.R.drawable.exo_legacy_controls_previous),// ic_replay_5),
                contentDescription = "Replay 5 seconds"
            )
        }

        IconButton(modifier = Modifier.size(40.dp), onClick = onPauseToggle) {
            Image(
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                painter =
                when {
                    isVideoPlaying -> {
                        painterResource(id = androidx.media3.ui.R.drawable.exo_icon_pause)//ic_pause)
                    }

                    isVideoPlaying.not() && playerState == STATE_ENDED -> {
                        painterResource(id = androidx.media3.ui.R.drawable.exo_ic_rewind)//ic_replay)
                    }

                    else -> {
                        painterResource(id = androidx.media3.ui.R.drawable.exo_icon_play)//ic_play)
                    }
                },
                contentDescription = "Play/Pause"
            )
        }

        IconButton(modifier = Modifier.size(40.dp), onClick = onForwardClick) {
            Image(
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                painter = painterResource(id = androidx.media3.ui.R.drawable.exo_legacy_controls_next),//ic_forward_10),
                contentDescription = "Forward 10 seconds"
            )
        }
    }
}

@Composable
private fun BottomControls(
    modifier: Modifier = Modifier,
    isPlaying: () -> Boolean,
    playbackState: () -> Int,
    totalDuration: () -> Long,
    currentTime: MutableState<Long>,
    speedSlowMo: MutableState<Boolean>,
    bufferedPercentage: () -> Int,
    onSeekChanged: (timeMs: Float) -> Unit,
    onPauseToggle: () -> Unit,
    setNormalSpeed: () -> Unit,
    setSlowMo: () -> Unit,
    funcFullscreen: () -> Unit
) {

    val duration = remember(totalDuration()) { totalDuration() }

    val buffer = remember(bufferedPercentage()) { bufferedPercentage() }

    val isVideoPlaying = remember(isPlaying()) { isPlaying() }

    val playerState = remember(playbackState()) { playbackState() }


    Column(modifier = modifier.background(Color.Black.copy(alpha = 0.6f)).padding(bottom = appConst.paddingSmall)) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Slider(
                modifier = Modifier.padding(horizontal = 40.dp).fillMaxWidth(),
                value = buffer.toFloat(),
                enabled = false,
                onValueChange = { /*do nothing*/ },
                valueRange = 0f..100f,
                colors =
                SliderDefaults.colors(
                    disabledThumbColor = Color.Transparent,
                    disabledActiveTrackColor = Color.Gray
                )
            )

            Slider(
                modifier = Modifier.padding(horizontal = 40.dp).fillMaxWidth(),
                value = currentTime.value.toFloat(),
                onValueChange = onSeekChanged,
                valueRange = 0f..duration.toFloat(),
                colors =
                SliderDefaults.colors(
                    activeTrackColor = colorResource(R.color.myOrange),
                    thumbColor = colorResource(R.color.myOrange),
                    activeTickColor = colorResource(R.color.myOrange)
                )
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().height(60.dp).padding(top = 0.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = "${currentTime.value.formatMinSec()} - ${duration.formatMinSec()}",
                color = Color.White
            )

            IconButton(modifier = Modifier.size(40.dp), onClick = onPauseToggle) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    painter =
                    when {
                        isVideoPlaying -> {
                            painterResource(id = androidx.media3.ui.R.drawable.exo_icon_pause)//ic_pause)
                        }

                        isVideoPlaying.not() && playerState == STATE_ENDED -> {
                            painterResource(id = androidx.media3.ui.R.drawable.exo_ic_rewind)//ic_replay)
                        }

                        else -> {
                            painterResource(id = androidx.media3.ui.R.drawable.exo_icon_play)//ic_play)
                        }
                    },
                    contentDescription = "Play/Pause"
                )
            }

            IconButton(modifier = Modifier.size(40.dp), onClick = {
                if (speedSlowMo.value) setNormalSpeed() else setSlowMo()
                speedSlowMo.value = speedSlowMo.value.not()
            }) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    painter = painterResource(id = androidx.media3.ui.R.drawable.exo_ic_speed),//ic_forward_10),
                    contentDescription = "Forward 10 seconds",
                    colorFilter = ColorFilter.tint(if (speedSlowMo.value) colorResource(R.color.myOrange) else Color.White)
                )
            }

            IconButton(
                modifier = Modifier.padding(horizontal = 16.dp),
                onClick = {
                    funcFullscreen()
                }
            ) {
                Image(
                    contentScale = ContentScale.Crop,
                    painter = painterResource(id = androidx.media3.ui.R.drawable.exo_ic_fullscreen_exit),//ic_fullscreen),
                    contentDescription = "Enter/Exit fullscreen"
                )
            }
        }
    }
}

fun Long.formatMinSec(): String {
    return if (this == 0L) {
        "..."
    } else {
        String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(this),
            TimeUnit.MILLISECONDS.toSeconds(this) -
                    TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(this)
                    )
        )
    }
}

private const val PLAYER_SEEK_BACK_INCREMENT = 5 * 1000L // 5 seconds
private const val PLAYER_SEEK_FORWARD_INCREMENT = 10 * 1000L // 10 seconds