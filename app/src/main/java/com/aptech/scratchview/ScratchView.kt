package com.aptech.scratchview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * Custom view that creates a scratch card effect by overlaying a gray layer on an image
 * and allowing the user to "scratch" it off using touch gestures.
 *
 * @param context Application context.
 * @param attr Attribute set used in XML to customize the view.
 */
class ScratchView(context: Context, attr: AttributeSet) : View(context, attr) {
private val cornerRadius=100f
    // Bitmap to store the scratchable overlay (gray layer)
    private var scratchBitmap: Bitmap? = null

    // Canvas to draw on the scratchable overlay
    private var scratchCanvas: Canvas? = null

    // Paint object to control the erasing effect
    private val scratchPaint = Paint()

    // Path object to track the user's touch movements
    private val scratchPath = Path()
    private var isRevealed = false // Track if the overlay is already removed

    init {
        // Initialize the scratchPaint with properties that allow "erasing"
        scratchPaint.apply {
            alpha = 0 // Makes the erased parts fully transparent
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) // Enables erasing effect
            style = Paint.Style.STROKE // Stroke style to allow freehand erasing
            strokeWidth = 90f // Width of the eraser brush
            strokeCap = Paint.Cap.ROUND // Ensures smooth edges while erasing
            strokeJoin = Paint.Join.ROUND // Smooth transition between erasing strokes
        }
    }


    /**
     * Called when the view's size changes (e.g., when first created or after a rotation).
     * Creates a new bitmap to act as the scratchable overlay.
     */
    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        // Log.d("RESULT", "WIDTH-$width height $height oldwidth$oldWidth oldheight$oldHeight")
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.img)
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true)
        // Create a bitmap the same size as the view to serve as the scratch layer
        scratchBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        // Scale the overlay bitmap to fit the view size
        // Create a canvas for the scratch layer and fill it with a gray color
        scratchCanvas = Canvas(scratchBitmap!!).apply {
            // drawColor(Color.GRAY) // Initial scratchable area (full gray)
            scaledBitmap?.let {
                drawBitmap(it, 0f, 0f, null)
            }
        }


    }

    /**
     * Draws the scratchable overlay onto the screen.
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (!isRevealed) {
            scratchBitmap?.let { bitmap ->
                val path = Path().apply {
                    addRoundRect(
                        RectF(0f, 0f, width.toFloat(), height.toFloat()),
                        cornerRadius, cornerRadius,
                        Path.Direction.CW
                    )
                }
                // canvas.save()
                canvas.clipPath(path) // Apply rounded corners
                canvas.drawBitmap(bitmap, 0f, 0f, null)
                // canvas.restore()
            }
        }
    }

    /**
     * Handles touch events to create the scratch effect.
     * Tracks the user's finger movement and erases portions of the overlay.
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isRevealed) return true // Ignore touch if already revealed

        val x = event.x // Get X coordinate of the touch event
        val y = event.y // Get Y coordinate of the touch event

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // Start a new path at the touch point
                scratchPath.moveTo(x, y)
            }
            MotionEvent.ACTION_MOVE -> {
                // Draw a line from the last touch point to the new touch point
                scratchPath.lineTo(x, y)

                // Erase the path from the scratch layer using the scratchPaint
                scratchCanvas?.drawPath(scratchPath, scratchPaint)

                // Redraw the view to show the updated scratch effect
                invalidate()
                checkScratchedPercentage()
            }
            MotionEvent.ACTION_UP -> {
                // Reset the path after lifting the finger
                scratchPath.reset()
            }
        }
        return true // Indicates that the touch event was handled
    }

    /**
     * Checks the percentage of the scratched (transparent) area in the scratchable bitmap.
     * If more than 50% of the bitmap is scratched, it marks the scratch card as revealed.
     *
     * Steps:
     * 1. Retrieves the bitmap used for scratching.
     * 2. Gets the width and height of the bitmap.
     * 3. Extracts pixel data from the bitmap into an integer array.
     * 4. Counts the total number of pixels and the number of fully transparent pixels.
     * 5. Calculates the percentage of transparent pixels.
     * 6. If the scratched percentage exceeds 50%, sets `isRevealed` to `true`
     *    and calls `invalidate()` to refresh the view.
     */
    private fun checkScratchedPercentage() {
        scratchBitmap?.let { bitmap ->
            val width = bitmap.width
            val height = bitmap.height
            val pixels = IntArray(width * height)
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

            val totalPixels = pixels.size
            val transparentPixels = pixels.count { it == Color.TRANSPARENT }

            val scratchedPercentage = (transparentPixels.toFloat() / totalPixels) * 100

            if (scratchedPercentage > 60) {
                isRevealed = true
                invalidate() // Redraw to remove the overlay
            }
        }
    }

}
