import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import android.content.Context
import android.widget.TextView
import com.example.calbon.R

class PercentMarkerView(context: Context, layoutId: Int) : MarkerView(context, layoutId) {

    private val tvContent: TextView = findViewById(R.id.tvContent)

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        e?.let {
            val valor = (it.y * 100 / 5f).toInt() // supondo que 5 é o max de emissão
            tvContent.text = "$valor%"
        }
        super.refreshContent(e, highlight)
    }
}
