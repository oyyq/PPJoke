# ppjoke
jetpack 客户端

项目预览:
https://user-images.githubusercontent.com/26019986/111860601-a1034800-8983-11eb-9245-7cd098631453.mp4

仿皮皮虾构建了短视频App

1. Navigation框架结合APT自动化构建APP路由导航，定制FixFragmentNavigator解决FragmentNavigator替换Fragment问题，改进为FragmentTransaction.hide(Fragment)/show(Fragment)。
2. DataBinding绑定界面组件到数据源。
3. Room数据库的使用，OkHttp网络库的封装，实现帖子列表页预加载缓存，后网络更新。
4. 定制AbsPagedListAdapter，实现了Paging添加HeaderView。
5. PagedList实现列表增删改，新评论添加在评论列表首部，评论可删除。 
6. PagedList分页失败后，SmartRefreshLayout接管手动继续向后分页。
7. 定制解决黏性事件的StickyLiveData。 
8. 帖子列表页自动定位播放视频帖子，跳转视频详情页实现无缝续播，定制CoordinatorLayout.Behavior实现手势分发，放大缩小播放区域。




