package itis.ru.custompiechart

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import kotlin.random.Random


class CustomPieChart @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var lastRandomValue: Int = -1
    private var currentRandomValue: Int = -1
    private var arcRect: RectF = RectF()
    private var paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var chartData: List<Float> = listOf()
    private var scaledData: MutableList<Float> = mutableListOf()
    private val colors = intArrayOf(Color.GREEN, Color.BLUE, Color.RED, Color.YELLOW)
    private var colorRandmizer = Random(colors.size)

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (scaledData.isNotEmpty()) {
            arcRect.top = 0f
            arcRect.left = 0f
            arcRect.right = width.toFloat()
            arcRect.bottom = width.toFloat()

            /* var startPoint = 0f
             canvas?.drawArc(arcRect, startPoint, 90f, true, paint)
             arcRect.right =  arcRect.right - 10
             canvas?.drawArc(arcRect, 90f, 90f, true, paint)
             arcRect.bottom = arcRect.bottom - 10
             canvas?.drawArc(arcRect, 180f, 90f, true, paint)
             arcRect.right =  arcRect.right + 10

             canvas?.drawArc(arcRect, 270f, 90f, true, paint)
 */
            var startPoint = 0f
            scaledData.forEach {
                paint.color = getRandomColor()
                canvas?.drawArc(arcRect, startPoint, it, true, paint)
                startPoint += it
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    fun setData(data: List<Float>) {
        chartData = data
        scaledData = scaleData()
        invalidate()
    }

    private fun toPx(dp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        )
    }

    private fun scaleData(): MutableList<Float> {
        val scaledValues = mutableListOf<Float>()
        val total = getTotalData()
        for (i in chartData.indices) {
            scaledValues.add(chartData[i] / total * 360)
        }
        return scaledValues
    }

    private fun getTotalData(): Float {
        var total = 0f
        for (i in chartData)
            total += i
        return total
    }

    private fun getRandomColor(): Int {
        currentRandomValue = colorRandmizer.nextInt(0, colors.size)
        return if (currentRandomValue != lastRandomValue) {
            lastRandomValue = currentRandomValue
            colors[currentRandomValue]
        } else getRandomColor()
    }
}
