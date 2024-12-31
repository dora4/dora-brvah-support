package dora.brvah.decoration

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class DividerGridItemDecoration : RecyclerView.ItemDecoration {

    private var widthDivider: Drawable?
    private var heightDivider: Drawable?

    constructor(context: Context) {
        val a: TypedArray = context.obtainStyledAttributes(ATTRS)
        widthDivider = a.getDrawable(0)
        heightDivider = widthDivider
        a.recycle()
    }

    constructor(
        context: Context,
        @DrawableRes widthDividerRes: Int,
        @DrawableRes heightDividerRes: Int
    ) {
        widthDivider = context.getDrawable(widthDividerRes)
        heightDivider = context.getDrawable(heightDividerRes)
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        drawHorizontal(c, parent)
        drawVertical(c, parent)
    }

    private fun getSpanCount(parent: RecyclerView): Int {
        // 列数
        var spanCount = -1
        val layoutManager: RecyclerView.LayoutManager? = parent.layoutManager
        if (layoutManager is GridLayoutManager) {
            spanCount = layoutManager.spanCount
        } else if (layoutManager is StaggeredGridLayoutManager) {
            spanCount = layoutManager
                .spanCount
        }
        return spanCount
    }

    fun drawHorizontal(c: Canvas, parent: RecyclerView) {
        val childCount: Int = parent.childCount
        for (i in 0 until childCount) {
            val child: View = parent.getChildAt(i)
            val params: RecyclerView.LayoutParams = child
                .layoutParams as RecyclerView.LayoutParams
            val left: Int = child.left - params.leftMargin
            val right: Int = (child.right + params.rightMargin
                    + widthDivider!!.intrinsicWidth)
            val top: Int = child.bottom + params.bottomMargin
            val bottom = top + widthDivider!!.intrinsicHeight
            widthDivider!!.setBounds(left, top, right, bottom)
            widthDivider!!.draw(c)
        }
    }

    fun drawVertical(c: Canvas, parent: RecyclerView) {
        val childCount: Int = parent.childCount
        for (i in 0 until childCount) {
            val child: View = parent.getChildAt(i)

            val params: RecyclerView.LayoutParams = child
                .layoutParams as RecyclerView.LayoutParams
            val top: Int = child.top - params.topMargin
            val bottom: Int = child.bottom + params.bottomMargin
            val left: Int = child.right + params.rightMargin
            val right = left + heightDivider!!.intrinsicWidth

            heightDivider!!.setBounds(left, top, right, bottom)
            heightDivider!!.draw(c)
        }
    }

    private fun isLastColumn(
        parent: RecyclerView, pos: Int, spanCount: Int,
        childCount: Int
    ): Boolean {
        var childCount = childCount
        val layoutManager: RecyclerView.LayoutManager? = parent.layoutManager
        if (layoutManager is GridLayoutManager) {
            if ((pos + 1) % spanCount == 0) {
                return true
            }
        } else if (layoutManager is StaggeredGridLayoutManager) {
            val orientation: Int = layoutManager
                .orientation
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                // 如果是最后一列，则不需要绘制右边
                if ((pos + 1) % spanCount == 0) {
                    return true
                }
            } else {
                childCount -= childCount % spanCount
                // 如果是最后一列，则不需要绘制右边
                if (pos >= childCount) return true
            }
        }
        return false
    }

    private fun isLastRaw(
        parent: RecyclerView, pos: Int, spanCount: Int,
        childCount: Int
    ): Boolean {
        var childCount = childCount
        val layoutManager: RecyclerView.LayoutManager? = parent.layoutManager
        if (layoutManager is GridLayoutManager) {
            childCount -= childCount % spanCount
            if (pos >= childCount)  // 如果是最后一行，则不需要绘制底部
                return true
        } else if (layoutManager is StaggeredGridLayoutManager) {
            val orientation: Int = layoutManager
                .orientation
            // StaggeredGridLayoutManager 且纵向滚动
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                childCount -= childCount % spanCount
                // 如果是最后一行，则不需要绘制底部
                if (pos >= childCount) return true
            } else {
                // 如果是最后一行，则不需要绘制底部
                if ((pos + 1) % spanCount == 0) {
                    return true
                }
            }
        }
        return false
    }

    override fun getItemOffsets(
        outRect: Rect, itemPosition: Int,
        parent: RecyclerView
    ) {
        val spanCount = getSpanCount(parent)
        val childCount: Int = parent.adapter?.itemCount ?: 0
        // 如果是最后一行，则不需要绘制底部
        if (isLastRaw(parent, itemPosition, spanCount, childCount)) {
            outRect[0, 0, heightDivider!!.intrinsicWidth] = 0
        } else if (isLastColumn(parent, itemPosition, spanCount, childCount)) {
            outRect[0, 0, 0] = widthDivider!!.intrinsicHeight
        } else {
            outRect[0, 0, heightDivider!!.intrinsicWidth] = widthDivider!!.intrinsicHeight
        }
    }

    companion object {
        private val ATTRS = intArrayOf(android.R.attr.listDivider)
    }
}
