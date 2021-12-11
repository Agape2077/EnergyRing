package cn.vove7.energy_ring.energystyle

import android.util.Log
import android.view.View
import android.widget.FrameLayout
import cn.vove7.energy_ring.App
import cn.vove7.energy_ring.monitor.MemoryMonitor
import cn.vove7.energy_ring.monitor.MonitorListener
import cn.vove7.energy_ring.ui.view.RingView
import cn.vove7.energy_ring.util.Config
import cn.vove7.energy_ring.util.getColorByRange
import cn.vove7.energy_ring.util.weakLazy

/**
 * # RingStyle
 *
 * @author Vove
 * 2020/5/11
 */
class RingStyle : EnergyStyle,MonitorListener, RotateAnimatorSupporter() {

    private val ringViewDelegate = weakLazy {
        RingView(App.INS).apply {
            layoutParams = FrameLayout.LayoutParams(Config.size, Config.size)
        }
    }

    override val displayView: View by ringViewDelegate

    override fun onAnimatorUpdate(rotateValue: Float) {
        displayView.rotation = rotateValue
    }

    override fun setColor(color: Int) {
        (displayView as RingView).apply {
            mainColor = color
        }
    }

    override fun update(progress: Int?) {
        (displayView as RingView).apply {
            strokeWidthF = Config.strokeWidthF
            if (progress != null) {
                this.progress =  MemoryMonitor(this@RingStyle).getProgress()
            }
            if (Config.colorMode == 2) {
                doughnutColors = Config.colorsDischarging
            } else {
                mainColor = getColorByRange(this.progressf, Config.colorsDischarging, Config.colorsCharging)
            }
            bgColor = Config.ringBgColor
            reSize(Config.size)
            requestLayout()
        }
    }

    override fun onRemove() {
        super.onRemove()
        ringViewDelegate.clearWeakValue()
    }

    override fun onProgress(ps: Int) {
        (displayView as RingView).apply {
            Log.d(TAG, "update monitor p ----> ${1 - Config.doubleRingChargingIndex} $ps")
            this.progress = ps
            mainColor = getColorByRange(this.progressf, Config.colorsDischarging, Config.colorsCharging)
            invalidate()
        }
    }
}