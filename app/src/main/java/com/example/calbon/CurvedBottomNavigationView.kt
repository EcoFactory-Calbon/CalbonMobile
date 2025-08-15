package com.example.calbon

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CurvedBottomNavigationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BottomNavigationView(context, attrs, defStyleAttr) {

    // Paint para o fundo da barra (cinza)
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FF212121")
        style = Paint.Style.FILL
    }

    // Paint para o recorte (preto)
    private val cutoutPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FF000000") // Cor do recorte: preto
        style = Paint.Style.FILL
    }

    private var selectedPosition = 0

    // Largura e altura do recorte
    private var cutoutWidth = 200f
    private var cutoutHeight = 300f

    // Raio dos cantos
    private val cutoutCornerRadiusTop = 60f
    private val cutoutCornerRadiusBottom = 160f

    // Posição vertical do recorte
    private val verticalOffset = 120f

    // Referência do FAB
    var fab: FloatingActionButton? = null
        set(value) {
            field = value
            value?.post { invalidate() }
        }

    init {
        background = null
        setBackgroundColor(Color.TRANSPARENT)
        setWillNotDraw(false)
    }

    fun updateSelectedPosition(position: Int) {
        selectedPosition = position
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        val width = width.toFloat()
        val height = height.toFloat()

        val rect = RectF(0f, 0f, width, height)
        canvas.drawRect(rect, backgroundPaint)

        val itemWidth = width / menu.size()
        val centerX = (selectedPosition * itemWidth) + (itemWidth / 2f)

        val left = centerX - cutoutWidth / 2
        val top = -verticalOffset
        val right = centerX + cutoutWidth / 2
        val bottom = cutoutHeight - verticalOffset

        // Raio diferente para topo e fundo
        val radii = floatArrayOf(
            cutoutCornerRadiusTop, cutoutCornerRadiusTop,
            cutoutCornerRadiusTop, cutoutCornerRadiusTop,
            cutoutCornerRadiusBottom, cutoutCornerRadiusBottom,
            cutoutCornerRadiusBottom, cutoutCornerRadiusBottom
        )

        val cutoutRect = RectF(left, top, right, bottom)
        val cutoutPath = Path()
        cutoutPath.addRoundRect(cutoutRect, radii, Path.Direction.CW)

        // Desenha o recorte preto
        canvas.drawPath(cutoutPath, cutoutPaint)

        super.onDraw(canvas)
    }
}
