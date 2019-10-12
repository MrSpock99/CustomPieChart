package itis.ru.custompiechart

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import kotlin.math.roundToInt
import kotlin.random.Random

class CustomBarChart @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var textPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var chartData: List<Float> = listOf()
    private var scaledData: MutableList<Float> = mutableListOf()
    private val colors = intArrayOf(Color.GREEN, Color.BLUE, Color.RED, Color.YELLOW)
    private var colorRandmizer = Random(colors.size)
    private var lastRandomValue: Int = -1
    private var currentRandomValue: Int = -1
    private var spaceBetween = toPx(60f)

    init {
        this.setLayerType(LAYER_TYPE_SOFTWARE, null)
        paint.style = Paint.Style.FILL
        paint.strokeWidth = toPx(4f)
        paint.maskFilter = BlurMaskFilter(
            1f, BlurMaskFilter.Blur.INNER
        )

        textPaint = Paint()
        textPaint.textSize = toPx(10f)
        textPaint.typeface = Typeface.DEFAULT_BOLD

        if (attrs != null) {
            val ta: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomBarChart)
            setSpaceBetween(
                ta.getDimension(R.styleable.CustomBarChart_spaceBetweenBars, spaceBetween)
                    .roundToInt()
            )
            ta.recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = 400 + paddingLeft + paddingRight
        val desiredHeight = 800 + paddingTop + paddingBottom
        setMeasuredDimension(
            measureDimension(desiredWidth, widthMeasureSpec),
            measureDimension(desiredHeight, heightMeasureSpec)
        )
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (scaledData.isEmpty())
            return
        var starty =
            resources.displayMetrics.heightPixels / 2

        var startx = 0f

        var initX = startx
        var initY = starty

        val rectHeight = 50

        scaledData.forEachIndexed { i, it ->
            startx = initX + (i + 1) * spaceBetween

            paint.color = getRandomColor()

            canvas?.drawText(
                i.toString(),
                startx,
                (initY + spaceBetween),
                textPaint
            )

            canvas?.drawRect(
                (startx - rectHeight),
                initY.toFloat(),
                (startx + rectHeight),
                (initY - it * spaceBetween),
                paint
            )

        }
    }

    fun setData(data: List<Float>) {
        chartData = data
        scaledData = scaleData()
        invalidate()
    }

    fun setSpaceBetween(space: Int) {
        spaceBetween = toPx(space.toFloat())
    }

    private fun measureDimension(contentSize: Int, measureSpec: Int): Int {
        val mode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        return when (mode) {
            MeasureSpec.EXACTLY -> specSize
            MeasureSpec.AT_MOST -> if (contentSize < specSize) {
                contentSize
            } else {
                specSize
            }
            MeasureSpec.UNSPECIFIED -> contentSize
            else -> contentSize
        }
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
            scaledValues.add(chartData[i] / total * 10)
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
