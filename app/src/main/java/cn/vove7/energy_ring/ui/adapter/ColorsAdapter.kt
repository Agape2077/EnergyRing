package cn.vove7.energy_ring.ui.adapter

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import cn.vove7.energy_ring.R
import cn.vove7.energy_ring.floatwindow.FloatRingWindow
import cn.vove7.energy_ring.util.Config
import cn.vove7.energy_ring.util.pickColor

/**
 * # ColorsAdapter
 *
 * @author Vove
 * 2020/5/11
 */
class ColorsAdapter : RecyclerView.Adapter<ColorsAdapter.ColorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val colorView = TextView(parent.context)
        colorView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12f)
        colorView.layoutParams = FrameLayout.LayoutParams(-2, -2).also {
            it.setMargins(10, 10, 10, 10)
        }
        return ColorViewHolder(colorView)
    }

    private val hasPlus get() = Config.colors.size < 6

    override fun getItemCount(): Int = Config.colors.size + if (hasPlus) 1 else 0

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        (holder.itemView as TextView).apply {
            if (hasPlus && position == itemCount - 1) {
                setOnLongClickListener(null)
                setOnClickListener {
                    pickColor(context)
                }
                text = ""
                background = ContextCompat.getDrawable(context, R.drawable.ic_add_circle)
            } else {
                text = "         \n          "
                setBackgroundColor(Config.colors[position])
                setOnClickListener {
                    pickColor(context, holder.adapterPosition)
                }
                setOnLongClickListener {
                    val cs = Config.colors
                    if (cs.size <= 1) {
                        Toast.makeText(context, "最少设置一个颜色", Toast.LENGTH_SHORT).show()
                        return@setOnLongClickListener true
                    }
                    kotlin.runCatching {
                        Config.colors = cs.toMutableList().apply { removeAt(position) }.toIntArray()
                        notifyItemRemoved(holder.adapterPosition)
                        FloatRingWindow.update()
                    }
                    true
                }
            }
        }
    }

    private fun pickColor(context: Context, pos: Int? = null) {
        pickColor(context, initColor = pos?.let { Config.colors.getOrNull(it) }) { c ->
            if (pos == null) {
                Config.colors = Config.colors.toMutableList().apply { add(c) }.toIntArray()
                notifyDataSetChanged()
            } else {
                Config.colors = Config.colors.also { it[pos] = c }
                notifyItemChanged(pos)
            }
            FloatRingWindow.update()
        }
    }

    class ColorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}