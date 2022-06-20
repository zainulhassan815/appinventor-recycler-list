package com.dreamers.recyclerlist

enum class ListManager {
    Linear,
    Grid,
    Staggered
}

enum class ListSnapHelper {
    Linear,
    Pager,
    None
}

enum class ItemAnimator {
    Default,

    // Cool
    LandingAnimator,

    // Fade
    FadeInAnimator,
    FadeInDownAnimator,
    FadeInUpAnimator,
    FadeInLeftAnimator,
    FadeInRightAnimator,

    // Scale
    ScaleInAnimator,
    ScaleInTopAnimator,
    ScaleInBottomAnimator,
    ScaleInLeftAnimator,
    ScaleInRightAnimator,

    // Flip
    FlipInTopXAnimator,
    FlipInBottomXAnimator,
    FlipInLeftYAnimator,
    FlipInRightYAnimator,

    // Slide
    SlideInLeftAnimator,
    SlideInRightAnimator,
    OvershootInLeftAnimator,
    OvershootInRightAnimator,
    SlideInUpAnimator,
    SlideInDownAnimator
}