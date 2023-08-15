package ru.ragefalcon.mastergym_android.view.elements

import android.util.Log
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import kotlinx.coroutines.delay


class CommonOpenItemPanel<T : Any>(
    /**
     * whenOpen - вызывается в момент когда CommonOpenItemPanel переходит в открытое состояние, т.е. начинается процесс
     * растягивания элемента.
     * */
    val whenOpen: (T) -> Unit,
    /**
     * itemMainOpen - data элемент, который будет разворачиваться и соответственно отвечать за новое состояние.
     *
     * startOpenAnimation - отвечает за момент когда начинается открытие и в openedItem передается уже со значением true, чтобы начать сворачивание, нужно установить значение false. В будущем лучше передавать сюда функцию закрытия.
     *
     * selection - переменная где хранится текущий развернутый элемент списка.
     *
     * finishAnimation - является индикатором завершения анимации разворачивания элемента.
     * */
    val openedItem: @Composable (itemMainOpen: T, startOpenAnimation: MutableState<Boolean>, selection: SingleSelectionType<T>, finishAnimation: State<Boolean>) -> Unit,
    /**
     * list - список основных элементов.
     *
     * modifierList - применяется к основному элементу LazyColumn внутри функции. Можно это сделать после дргих свойств
     * Modifier. ... .then(modifierList)
     *
     * selection - переменная где хранится текущий развернутый элемент списка.
     *
     * openSpis_Index_Item - функция, которая стартует разворачивание элемента на весь экран. В нее нужно передать
     * индекс элемента в списке(поэтому желательно использовать itemsIndexed), вторым аргументом передается сам элемент,
     * который будет разворачиваться. Третий аргумент передается если растягиваться должен
     * не весь Item из списка(имеется ввиду отображаемый графический объект),
     * а отдельный его элемент(например, анимация должна растягивать аватарку контакта на весь экран),
     * тогда нужно передать Rect, в котором будут координаты этого элемента внутри всего Item.
     *
     * ПРИМЕР получения координат через Modifier:
     *  Modifier.onGloballyPositioned {
     *                     it.parentCoordinates?.boundsInParent()?.let { parentRect ->
     *                         it.boundsInParent().let { childRect ->
     *                             neededRect = Rect(parentRect.topLeft + childRect.topLeft, childRect.size)
     *                         }
     *                     }
     *                 }
     *
     * lazyListState - необходимо передать во внутренний LazyColumn, без этого координаты для анимации будут
     * рассчитываться неверно.
     * */
    val mainSpis: @Composable ColumnScope.(list: List<T>, modifierList: Modifier, selection: SingleSelectionType<T>, openSpis_Index_Item: (Int, T, Rect?) -> Unit, lazyListState: LazyListState) -> Unit
) {

    val rectListTree = mutableStateOf(Rect(Offset(0f, 0f), Size(0f, 0f)))
    val rectParent = mutableStateOf(Rect(Offset(0f, 0f), Size(0f, 0f)))
    val elementRect = mutableStateOf(Rect(Offset(0f, 0f), Size(0f, 0f)))

    val offsetItem = mutableStateOf(0)
    val sizeItem = mutableStateOf(0)

    val openTS: MutableState<Boolean> = mutableStateOf(false)
    val selectionMainSpis = SingleSelectionType<T>()

    val startOpenAnimation = mutableStateOf(false)
    val finishAnimation = mutableStateOf(true)
    val finishTop = mutableStateOf(true)
    val finishStart = mutableStateOf(true)
    val finishEnd = mutableStateOf(true)
    val finishBottom = mutableStateOf(true)
    val state: LazyListState = LazyListState()

    @Composable
    fun Show(
        list: List<T>,
        modifier: Modifier = Modifier
    ) {
        LaunchedEffect(openTS.value) {
            if (openTS.value) {
                selectionMainSpis.selected?.let { mainItem ->
                    whenOpen(mainItem)
                }
            }
        }

        Column(modifier.onGloballyPositioned {
            rectParent.value = it.boundsInParent()
        }, horizontalAlignment = Alignment.CenterHorizontally) {
            if (openTS.value) {
                OpenMainSpis()
            } else {
                mainSpis(list, Modifier.onGloballyPositioned { itemLay ->
                    rectListTree.value = itemLay.boundsInParent()
                }, selectionMainSpis, { index, item, rectInItem ->
                    selectionMainSpis.selected = item
                    openTS.value = true
                    val visibleItem = state.layoutInfo.visibleItemsInfo.find { it.index == index }
                    elementRect.value = rectInItem ?: Rect(Offset(0f, 0f), Size(0f, 0f))
                    Log.d("myTag", "elementRect = ${elementRect.value}")
                    offsetItem.value = visibleItem?.offset ?: 0
                    sizeItem.value = visibleItem?.size ?: 0
                }, state)
            }
        }
    }

    private fun getTop() = rectListTree.value.top + offsetItem.value + elementRect.value.top
    private fun getStart() = rectListTree.value.left + elementRect.value.left
    private fun getEnd() =
        rectParent.value.width - if (elementRect.value.size.width != 0f) (rectListTree.value.left + elementRect.value.right) else rectListTree.value.right

    private fun getBottom() =
        rectParent.value.height - rectListTree.value.top - offsetItem.value - (if (elementRect.value.size.width != 0f) elementRect.value.bottom else sizeItem.value.toFloat())

    fun testFinishAnimation() {
        if (finishTop.value && finishStart.value && finishEnd.value && finishBottom.value) {
            finishAnimation.value = true
        }
        if (!startOpenAnimation.value && openTS.value) {
            openTS.value = false
        }
    }

    val durationAnim = 250

    @Composable
    private fun OpenMainSpis() {
        val firstStart = remember { mutableStateOf(true) }

        LaunchedEffect(Unit) {
            delay(100)
            if (firstStart.value) {
                finishTop.value = getTop() <= 0f
                finishStart.value = getStart() <= 0f
                finishEnd.value = getEnd() <= 0f
                finishBottom.value = getBottom() <= 0f
                finishAnimation.value = false
                startOpenAnimation.value = true
                firstStart.value = false
            }
        }

        val topOffsetTree: Float by animateFloatAsState(
            targetValue = if (startOpenAnimation.value) 0f else zeroIfMinus(getTop()),
            animationSpec = tween(durationMillis = durationAnim, easing = FastOutSlowInEasing)
        ) {
            finishTop.value = true
            testFinishAnimation()
        }
        val startOffsetTree: Float by animateFloatAsState(
            targetValue = if (startOpenAnimation.value) 0f else zeroIfMinus(getStart()),
            animationSpec = tween(durationMillis = durationAnim, easing = FastOutSlowInEasing)
        ) {
            finishStart.value = true
            testFinishAnimation()
        }
        val endOffsetTree: Float by animateFloatAsState(
            targetValue = if (startOpenAnimation.value) 0f else zeroIfMinus(getEnd()),
            animationSpec = tween(durationMillis = durationAnim, easing = FastOutSlowInEasing)
        ) {
            finishEnd.value = true
            testFinishAnimation()
        }
        val bottomOffsetTree: Float by animateFloatAsState(
            targetValue = if (startOpenAnimation.value) 0f else zeroIfMinus(getBottom()),
            animationSpec = tween(durationMillis = durationAnim, easing = FastOutSlowInEasing)
        ) {
            finishBottom.value = true
            testFinishAnimation()
        }

        with(LocalDensity.current) {
            Box(
                Modifier.padding(
                    top = topOffsetTree.toDp(),
                    bottom = bottomOffsetTree.toDp(),
                    start = startOffsetTree.toDp(),
                    end = endOffsetTree.toDp()
                )
            ) {
                selectionMainSpis.selected?.let { itemMainOpen ->
                    openedItem(itemMainOpen, startOpenAnimation, selectionMainSpis, finishAnimation)
                }
            }
        }
    }

    fun zeroIfMinus(value: Float): Float = if (value < 0f) 0f else value

}

