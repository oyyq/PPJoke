package com.mooc.ppjoke.ui.home;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.paging.ItemKeyedDataSource;
import androidx.paging.PagedList;
import com.alibaba.fastjson.TypeReference;
import com.mooc.libnetwork.ApiResponse;
import com.mooc.libnetwork.ApiService;
import com.mooc.libnetwork.JsonCallback;
import com.mooc.libnetwork.Request;
import com.mooc.ppjoke.ui.AbsViewModel;
import com.mooc.ppjoke.model.Feed;
import com.mooc.ppjoke.ui.MutablePageKeyedDataSource;
import com.mooc.ppjoke.ui.login.UserManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


public class HomeViewModel extends AbsViewModel<Feed> {

    private volatile boolean witchCache = true;
    private MutableLiveData<PagedList<Feed>> cacheLiveData = new MutableLiveData<>();
    private AtomicBoolean loadAfter = new AtomicBoolean(false);
    private String mFeedType;

    @Override
    public DataSource createDataSource() {
        return new FeedDataSource();
    }

    public MutableLiveData<PagedList<Feed>> getCacheLiveData() {
        return cacheLiveData;
    }

    public void setFeedType(String feedType) {
        mFeedType = feedType;
    }

    class FeedDataSource extends ItemKeyedDataSource<Integer, Feed> {
        @Override
        public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Feed> callback) {
            loadData(params.requestedInitialKey == null? 0: params.requestedInitialKey, params.requestedLoadSize, callback);
            witchCache = false;
        }

        //何时调用loadAdter & loadBefore 不是人为控制, 有点迷, 能够确定AsyncPagedListDiffer#getItem(int) 以及AsyncPagedListDiffer#latchPagedList(..)
        //触发差分异计算的时候会触发loadAfter / loadBefore ..
        @Override
        public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Feed> callback) {
            loadData(params.key, params.requestedLoadSize, callback);
        }

        @Override
        public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Feed> callback) {
            callback.onResult(Collections.emptyList());
        }

        @NonNull
        @Override
        public Integer getKey(@NonNull Feed item) {
            return item.id;
        }
    }


    /**
     * 难点1. ItemKeyedDataSource.loadInitial: 先加载 缓存 再加载 网络, 不能连续两次调 LoadInitialCallback.onResult,
     *        ==> 我们需要"2"个pagedList, cacheLiveData专门加载缓存, AbsViewModel.pagedList专门加载网络
     *
     * @param key
     * @param count
     * @param callback
     */
    private void loadData(int key, int count, ItemKeyedDataSource.LoadCallback<Feed> callback) {
        if (key > 0) {
            //key > 0 --> 向后分页
            loadAfter.set(true);
        }

        //feeds/queryHotFeedsList
        Request request = ApiService.get("/feeds/queryHotFeedsList")
                .addParam("feedType", mFeedType)
                .addParam("userId", UserManager.get().getUserId())
                .addParam("feedId", key)
                .addParam("pageCount", count)
                .responseType(new TypeReference<ArrayList<Feed>>() {
                }.getType());


        if (witchCache) {
            request.cacheStrategy(Request.CACHE_ONLY);
            request.execute(new JsonCallback<List<Feed>>() {
                @Override
                public void onCacheSuccess(ApiResponse<List<Feed>> response) {
                    MutablePageKeyedDataSource dataSource = new MutablePageKeyedDataSource<Feed>();
                    dataSource.data.addAll(response.body);

                    PagedList pagedList = dataSource.buildNewPagedList(config);
                    cacheLiveData.postValue(pagedList);

                    // TODO 下面的不可取, 否则接着第二次调callback.onResult(..) 会报 如下: 原因在于连续callback.onResult并不会触发差分计算, 后面差分前面, 导致数据混乱
                    // java.lang.IllegalStateException: callback.onResult already called, cannot call again.
                    //if (response.body != null) {
                    //  callback.onResult(response.body);
                    // }

                }
            });
        }

        try {
            Request netRequest = witchCache ? request.clone() : request;
            netRequest.cacheStrategy(key == 0 ? Request.NET_CACHE : Request.NET_ONLY);
            ApiResponse<List<Feed>> response = netRequest.execute();
            List<Feed> data = response.body == null ? Collections.emptyList() : response.body;

            callback.onResult(data);

            if (key > 0) {
                //通过BoundaryPageData发送数据 告诉UI层 是否应该主动关闭上拉加载分页的动画
                ((MutableLiveData) getBoundaryPageData()).postValue(data.size() > 0);
                loadAfter.set(false);
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        Log.e("loadData", "loadData: key:" + key);

    }


    public void loadAfter(int id, ItemKeyedDataSource.LoadCallback<Feed> callback) {
        if (loadAfter.get()) {
            callback.onResult(Collections.emptyList());
            return;
        }
        ArchTaskExecutor.getIOThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                loadData(id, config.pageSize, callback);
            }
        });
    }

}

/**
 * LiveData原理比较简单, 读源码可知:
 *      observe(..)方法:
 *          构造LifecycleBoundObserver wrapper 对象,放在liveData.mObservers(Map)里, 同时
 *          owner.getLifecycle().addObserver(wrapper) 和lifecycleOwner owner绑定, 监听owner的生命周期变化,
 *
 *          LifecycleBoundObserver实现了LifecycleEventObserver接口并实现了onStateChanged方法, 一旦监听到owner的
 *          生命周期在onStarted / onResume 就通过( ObserverWrapper.activeStateChanged)方法 通知LiveData dispatchingValue
 *          来considerNotify自己
 *
 *          同时onStateChanged在onwer.onDestroy方法被回调后 LiveData.removeObserver会将wrapper从LiveData.mObservers中移除
 *          并且observer和 owner解绑
 *
 *          ObserverWrapper内部有标记位: mLastVersion 来和LiveData.mVersion对齐, 避免收到旧数据
 *
 *      observeForever方法:
 *          不需要与lifecycleowner绑定, 一旦调用, LiveData立即将LiveData.mData传给observer, observer.onChanged被调用
 *          不能自动从LIveData.mObservers中移除, 必须手动外部移除
 *
 */




