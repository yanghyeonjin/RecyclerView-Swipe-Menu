package com.example.recyclerviewswipemenu

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kotlin.math.max
import kotlin.math.min

enum class ButtonsState {
    GONE,
    LEFT_VISIBLE,
    RIGHT_VISIBLE
}

class SwipeController(private val buttonsActions: SwipeControllerActions) : ItemTouchHelper.Callback() {
    private var swipeBack = false
    private var buttonShowedState = ButtonsState.GONE
    private var buttonInstance: RectF? = null
    private var currentItemViewHolder: RecyclerView.ViewHolder? = null
    private val buttonWidth = 300f

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(0, LEFT or RIGHT)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

    }

    override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
        if (swipeBack) {
            swipeBack = buttonShowedState != ButtonsState.GONE
            return 0
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (actionState == ACTION_STATE_SWIPE) {
            if (buttonShowedState != ButtonsState.GONE) {
                var currentX = dX
                if (buttonShowedState == ButtonsState.LEFT_VISIBLE) currentX = max(dX, buttonWidth)
                if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) currentX = min(dX, -buttonWidth);
                super.onChildDraw(c, recyclerView, viewHolder, currentX, dY, actionState, isCurrentlyActive);
            }
            else {
                setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }

        if (buttonShowedState == ButtonsState.GONE) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
        currentItemViewHolder = viewHolder
    }


    private fun setTouchListener(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: ViewHolder,
        dX: Float, dY: Float,
        actionState: Int, isCurrentlyActive: Boolean
    ) {
        recyclerView.setOnTouchListener { v, event ->
            swipeBack = event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_UP

            if (swipeBack) {
                if (dX < -buttonWidth) buttonShowedState = ButtonsState.RIGHT_VISIBLE
                else if (dX > buttonWidth) buttonShowedState = ButtonsState.LEFT_VISIBLE

                if (buttonShowedState != ButtonsState.GONE) {
                    setTouchDownListener(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                    setItemsClickable(recyclerView, false)
                }
            }

            false
        }
    }

    private fun setTouchDownListener(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: ViewHolder,
        dX: Float, dY: Float,
        actionState: Int, isCurrentlyActive: Boolean
    ) {
        recyclerView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                setTouchUpListener(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
            false
        }
    }

    private fun setTouchUpListener(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: ViewHolder,
        dX: Float, dY: Float,
        actionState: Int, isCurrentlyActive: Boolean
    ) {
        recyclerView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                super@SwipeController.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    0f,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                recyclerView.setOnTouchListener { v, event -> false }
                setItemsClickable(recyclerView, true)
                swipeBack = false

                if (buttonsActions != null && buttonInstance != null && buttonInstance!!.contains(event.x, event.y)) {
                    if (buttonShowedState == ButtonsState.LEFT_VISIBLE) {
                        buttonsActions.onLeftClicked(viewHolder.adapterPosition)
                    } else if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) {
                        buttonsActions.onRightClicked(viewHolder.adapterPosition)
                    }
                }
                buttonShowedState = ButtonsState.GONE
                currentItemViewHolder = null
            }
            false
        }
    }

    private fun setItemsClickable(
        recyclerView: RecyclerView,
        isClickable: Boolean
    ) {
        for (i in 0 until recyclerView.childCount) {
            recyclerView.getChildAt(i).isClickable = isClickable
        }
    }

    private fun drawButtons(c: Canvas, viewHolder: ViewHolder) {
        val buttonWidthWithoutPadding = buttonWidth - 20
        val corners = 16f

        val itemView = viewHolder.itemView
        val p = Paint()

        val leftButton = RectF(
            itemView.left.toFloat(),
            itemView.top.toFloat(),
            itemView.left + buttonWidthWithoutPadding,
            itemView.bottom.toFloat()
        )
        p.color = Color.BLUE
        c.drawRoundRect(leftButton, corners, corners, p)
        drawText("EDIT", c, leftButton, p)

        val rightButton = RectF(
            itemView.right - buttonWidthWithoutPadding,
            itemView.top.toFloat(),
            itemView.right.toFloat(),
            itemView.bottom.toFloat())
        p.color = Color.RED
        c.drawRoundRect(rightButton, corners, corners, p)
        drawText("DELETE", c, rightButton, p)

        buttonInstance = null
        if (buttonShowedState == ButtonsState.LEFT_VISIBLE) {
            buttonInstance = leftButton
        } else if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) {
            buttonInstance = rightButton
        }
    }

    private fun drawText(
        text: String,
        c: Canvas,
        button: RectF,
        p: Paint
    ) {
        val textSize = 60f
        p.color = Color.WHITE
        p.isAntiAlias = true
        p.textSize = textSize

        val textWidth = p.measureText(text)
        c.drawText(text, button.centerX() - (textWidth / 2), button.centerY() + (textSize / 2), p)
    }

    fun onDraw(c: Canvas) {
        currentItemViewHolder?.let {
            drawButtons(c, it)
        }
    }
}