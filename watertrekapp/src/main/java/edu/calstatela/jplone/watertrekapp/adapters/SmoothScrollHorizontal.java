/*Reference https://mcochin.wordpress.com/2015/05/13/android-customizing-smoothscroller-for-the-recyclerview/ *
  Credit to Mochin
 */

package edu.calstatela.jplone.watertrekapp.adapters;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;

public class SmoothScrollHorizontal extends LinearLayoutManager {
    private static final float MILLISECONDS_PER_INCH = 1000f;
    private Context mContext;
    private int mOrientation;
    private boolean mReverseLayout;


    public SmoothScrollHorizontal(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        this.mContext = context;
        this.mOrientation = orientation;
        this.mReverseLayout = reverseLayout;


    }


    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView,
                                       RecyclerView.State state, final int position) {

        Log.d("rcyPosition", "smoothScrollToPosition: " + position);

        LinearSmoothScroller smoothScroller =
                new LinearSmoothScroller(mContext) {



                    //This controls the direction in which smoothScroll looks
                    //for your view
                    @Override
                    public PointF computeScrollVectorForPosition
                    (int targetPosition) {
                        return SmoothScrollHorizontal.this
                                .computeScrollVectorForPosition(targetPosition);
                    }

                    //This returns the milliseconds it takes to
                    //scroll one pixel.
                    @Override
                    protected float calculateSpeedPerPixel
                    (DisplayMetrics displayMetrics) {
                        return MILLISECONDS_PER_INCH/displayMetrics.densityDpi;
                    }
                };

        smoothScroller.setTargetPosition(position);
        this.startSmoothScroll(smoothScroller);
    }

}

