package com.example.recyclerviewswipemenu

abstract class SwipeControllerActions {
    open fun onLeftClicked(position: Int) {}
    open fun onRightClicked(position: Int) {}
}