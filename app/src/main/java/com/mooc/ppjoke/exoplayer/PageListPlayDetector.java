package com.mooc.ppjoke.exoplayer;

import android.util.Pair;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.mooc.libcommon.extention.AbsPagedListAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 列表视频自动播放 检测逻辑
 */
public class PageListPlayDetector {

    //收集一个个的能够进行视频播放的 对象, 面向接口
    private List<IPlayTarget> mTargets = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private IPlayTarget playingTarget;

    //为什么不用holder.adapterposition()作为target的索引了 ? 怀疑add holder与remove holder时adapterposition变化
    public void addTarget(IPlayTarget target) {
        mTargets.add(target);
    }

    public void removeTarget(IPlayTarget target) {
        Iterator<IPlayTarget> iterator = mTargets.iterator();
        while (iterator.hasNext()){
            IPlayTarget next = iterator.next();

            if(next == target) {
                if (playingTarget != null && playingTarget == next) {
                    playingTarget.inActive();
                    playingTarget = null;
                }
                iterator.remove();
                break;
            }

        }
    }


    //不同RecyclerView对应不同
    public PageListPlayDetector(LifecycleOwner owner, RecyclerView recyclerView) {
        mRecyclerView = recyclerView;

        owner.getLifecycle().addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    playingTarget = null;
                    mTargets.clear();

                    mRecyclerView.removeCallbacks(delayAutoPlay);               //View.removeCallbacks(..) <=> View.post(Runnable)
                    mRecyclerView.removeOnScrollListener(scrollListener);
                    owner.getLifecycle().removeObserver(this);
                }
            }
        });
        mRecyclerView.getAdapter().registerAdapterDataObserver(mDataObserver);
        mRecyclerView.addOnScrollListener(scrollListener);
    }


    RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                autoPlay();
            }
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (dx == 0 && dy == 0) {
                //时序问题。当执行了AdapterDataObserver#onItemRangeInserted  可能还没有被布局到RecyclerView上。
                //所以此时 recyclerView.getChildCount()还是等于0的。
                //等childView 被布局到RecyclerView上之后，会执行onScrolled（）方法
                //并且此时 dx,dy都等于0
                postAutoPlay();
            } else {
                if (playingTarget != null && playingTarget.isPlaying() && !isTargetInBounds(playingTarget)) {
                    playingTarget.inActive();
                }
            }
        }
    };



    private void postAutoPlay() {
        mRecyclerView.post(delayAutoPlay);
    }

    Runnable delayAutoPlay = new Runnable() {
        @Override
        public void run() {
            autoPlay();
        }
    };


    //被AbsPagedListAdapter#AdapterDataObserverProxy代理
    private final RecyclerView.AdapterDataObserver mDataObserver = new RecyclerView.AdapterDataObserver() {
        /**
         * 回调onItemRangexxx方法后 :
         * RecyclerView可能还没调addView, 所以在autoPlay()中拿不到即将要添加到RecyclerView上的IPlayTarget
         * @param positionStart
         * @param itemCount
         */
        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            postAutoPlay();
        }
    };



    private void autoPlay() {
        if (mTargets.size() <= 0 || mRecyclerView.getChildCount() <= 0) {
            return;
        }

        if (playingTarget != null && playingTarget.isPlaying() && isTargetInBounds(playingTarget)) {
            return;
        }

        IPlayTarget activeTarget = null;
        for (IPlayTarget target : mTargets) {

            boolean inBounds = isTargetInBounds(target);
            if (inBounds) {
                activeTarget = target;
                break;
            }
        }

        if (activeTarget != null) {
            if (playingTarget != null) {
                playingTarget.inActive();
            }
            playingTarget = activeTarget;
            activeTarget.onActive();
        }
    }




    /**
     * 检测 IPlayTarget 所在的 viewGroup 是否至少还有一半的大小在屏幕内
     * @param target
     * @return
     */
    private boolean isTargetInBounds(IPlayTarget target) {
        ViewGroup owner = target.getOwner();
        ensureRecyclerViewLocation();
        if (!owner.isShown() || !owner.isAttachedToWindow()) {
            return false;
        }

        int[] location = new int[2];
        owner.getLocationOnScreen(location);
        int center = location[1] + owner.getHeight() / 2;
        //承载视频播放画面的ViewGroup它需要至少一半的大小 在RecyclerView上下范围内
        return center >= rvLocation.first && center <= rvLocation.second;
    }



    private Pair<Integer, Integer> rvLocation = null;
    private Pair<Integer, Integer> ensureRecyclerViewLocation() {
        if (rvLocation == null) {
            int[] location = new int[2];
            mRecyclerView.getLocationOnScreen(location);

            int top = location[1];
            int bottom = top + mRecyclerView.getHeight();

            rvLocation = new Pair(top, bottom);
        }
        return rvLocation;
    }


    public void onPause() {
        if (playingTarget != null) {
            playingTarget.inActive();
        }
    }

    public void onResume() {
        if (playingTarget != null) {
            playingTarget.onActive();
        }
    }
}
