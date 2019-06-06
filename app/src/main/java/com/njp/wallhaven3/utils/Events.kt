package com.njp.wallhaven3.utils

/**
 * EventBus辅助事件
 */

enum class ScrollEvent {
    EVENT_SCROLL_UP,
    EVENT_SCROLL_DOWN
}

data class ScrollToEvent(var position: Int, var isSmooth: Boolean)



