package com.mooc.ppjoke.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.mooc.libcommon.view.EmptyView;
import com.mooc.ppjoke.R;
import com.mooc.ppjoke.databinding.LayoutRefreshViewBinding;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;



public abstract class AbsListFragment<T, M extends AbsViewModel<T>> extends Fragment implements OnRefreshListener, OnLoadMoreListener {

    protected LayoutRefreshViewBinding binding;
    protected RecyclerView mRecyclerView;
    protected SmartRefreshLayout mRefreshLayout;
    protected EmptyView mEmptyView;
    protected PagedListAdapter<T, RecyclerView.ViewHolder> adapter;
    protected M mViewModel;
    protected DividerItemDecoration decoration;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutRefreshViewBinding.inflate(inflater, container, false);
        binding.getRoot().setFitsSystemWindows(true);


        mRecyclerView = binding.recyclerView;
        mRefreshLayout = binding.refreshLayout;
        mEmptyView = binding.emptyView;

        mRefreshLayout.setEnableRefresh(true);
        mRefreshLayout.setEnableLoadMore(true);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setOnLoadMoreListener(this);

        adapter = getAdapter();
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setItemAnimator(null);

        //默认给列表中的Item 一个 10dp的ItemDecoration
        decoration = new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        decoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.list_divider));
        mRecyclerView.addItemDecoration(decoration);

        genericViewModel();
        return binding.getRoot();
    }


    private void genericViewModel() {
        //利用 子类传递的 泛型参数实例化出absViewModel 对象。
        ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        Type[] arguments = type.getActualTypeArguments();
        if (arguments.length > 1) {
            Type argument = arguments[1];
            Class modelClaz = ((Class) argument).asSubclass(AbsViewModel.class);
            mViewModel = (M) ViewModelProviders.of(this).get(modelClaz);

            mViewModel.getPageData().observe(this, pagedList -> submitList(pagedList));

            mViewModel.getBoundaryPageData().observe(this, hasData -> finishRefresh(hasData));
        }
    }


    public void submitList(PagedList<T> result) {
        //只有当新数据集合大于0 的时候，才调用adapter.submitList
        //否则可能会出现 页面----有数据----->被清空-----空布局
        if (result.size() > 0) {
            adapter.submitList(result);
        }
        finishRefresh(result.size() > 0);
    }


    public void finishRefresh(boolean hasData) {
        PagedList<T> currentList = adapter.getCurrentList();
        hasData = hasData || currentList != null && currentList.size() > 0;

        RefreshState state = mRefreshLayout.getState();
        if (state.isFooter && state.isOpening) {
            mRefreshLayout.finishLoadMore();
        } else if (state.isHeader && state.isOpening) {
            mRefreshLayout.finishRefresh();
        }

        if (hasData) {
            mEmptyView.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 因而 我们在 onCreateView的时候 创建了 PagedListAdapter
     * 所以，如果arguments 有参数需要传递到Adapter 中，那么需要在getAdapter()方法中取出参数。
     *
     * @return
     */
    public abstract PagedListAdapter getAdapter();
}




/**
 * 从5-7节讲完LiveData工作原理后, 到5-8节3'30秒都在将PagedList和DataSource结合
 * 加载初始化数据的
 *
 * 3'30秒后开始讲解PagedList加载分页数据的
 * 分页加载逻辑的实现是在AsyncPagedListDiffer中, 通过AsyncPagedListDiffer.getItem方法
 * 在列表上滑的过程中, getItem方法不断被调用
 *
 * 此方法中调用到mPagedList.loadAround(index), loadAround调用了loadAroundInternal(abstract,
 * 具体实现见ContiguousPagedList, SnapshotPagedList...)
 * 具体实现:
 * int prependItems 向前填充的数量,  >0则执行schedulePrepend(), 其中异步执行了mDataSource.dispatchLoadBefore(...) -> 我们自定义的DataSource的loadBefore,
 * int appendItems 向后填充的数量, >0则执行scheduleAppend(), 其中异步执行了mDataSource.dispatchLoadAfter(..) -> 我们自定义的DataSource的loadAfter,
 *
 *          mDataSource.dispatchLoadAfter(..)中传递了一个重要参数mReceiver
 *          (PageResult.Receiver实例, 在ContiguousPagedList中声明时直接写了实现),
 *          课程中声称每次上拉加载分页的时候数据回调会到mReceiver.onPageResult中,
 *          判断了是PageResult.INIT类型: 将初始化数据放到mStorage(PageStorage实例)
 *          还是PageREsult.APPEND类型: 将追加数据放到mStorage中去, mStorage.appendPage()的最后回调了callback.onPageAppended(..)(目测实现是ContiguousPagedList.onPageAppended),
 *          中间调用了notifyChanged & notifyINserted, 这2个方法中需要我们在adapter.submitList调用到的AsyncPageListDiffer.submitList方法中添加的callback(PagedList.Callback实例)
 *
 *          callback.onInserted, callback.onRemoved, callback.onChanged.. 最后到了adapter.notifyItemRangeInserted / notifyItemRangeRemoved ...方法, 达到了UI变更的目的
 *
 *          除了ContiguousPagedList.onPageAppended被回调,
 *          ContiguousPagedList.onEmptyAppend也可能被回调, 一旦onEmptyAppend被回调,
 *          那么再怎么上拉加载也不会向后分页了, todo 问题来了, 这时候mDataSource被invalidate了吗 ?
 *
 */




