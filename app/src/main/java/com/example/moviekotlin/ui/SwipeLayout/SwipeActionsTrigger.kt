package berhane.biniam.swipy.swipe


interface SwipeActionsTrigger {
    fun swipeAction(isCurrentlyActive: Boolean, index: Int, swipeDir: SwipeDirections)
}