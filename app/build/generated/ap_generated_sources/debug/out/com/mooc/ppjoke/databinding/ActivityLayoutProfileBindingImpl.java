package com.mooc.ppjoke.databinding;
import com.mooc.ppjoke.R;
import com.mooc.ppjoke.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class ActivityLayoutProfileBindingImpl extends ActivityLayoutProfileBinding  {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.appbar, 9);
        sViewsWithIds.put(R.id.collapsing_toolbar, 10);
        sViewsWithIds.put(R.id.profile_info, 11);
        sViewsWithIds.put(R.id.top_user_info, 12);
        sViewsWithIds.put(R.id.action_back, 13);
        sViewsWithIds.put(R.id.tab_layout, 14);
        sViewsWithIds.put(R.id.view_pager, 15);
    }
    // views
    @NonNull
    private final androidx.coordinatorlayout.widget.CoordinatorLayout mboundView0;
    @NonNull
    private final android.widget.TextView mboundView6;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public ActivityLayoutProfileBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 16, sIncludes, sViewsWithIds));
    }
    private ActivityLayoutProfileBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 1
            , (android.widget.ImageView) bindings[13]
            , (com.google.android.material.appbar.AppBarLayout) bindings[9]
            , (com.mooc.ppjoke.view.PPImageView) bindings[1]
            , (com.google.android.material.appbar.CollapsingToolbarLayout) bindings[10]
            , (android.widget.TextView) bindings[3]
            , (android.widget.TextView) bindings[4]
            , (android.widget.TextView) bindings[2]
            , (android.widget.LinearLayout) bindings[11]
            , (android.widget.TextView) bindings[5]
            , (com.google.android.material.tabs.TabLayout) bindings[14]
            , (com.mooc.ppjoke.view.PPImageView) bindings[7]
            , (android.widget.TextView) bindings[8]
            , (androidx.constraintlayout.widget.ConstraintLayout) bindings[12]
            , (androidx.viewpager2.widget.ViewPager2) bindings[15]
            );
        this.authorAvatarLarge.setTag(null);
        this.fansCount.setTag(null);
        this.followCount.setTag(null);
        this.likeCount.setTag(null);
        this.mboundView0 = (androidx.coordinatorlayout.widget.CoordinatorLayout) bindings[0];
        this.mboundView0.setTag(null);
        this.mboundView6 = (android.widget.TextView) bindings[6];
        this.mboundView6.setTag(null);
        this.scoreCount.setTag(null);
        this.topAuthorAvatar.setTag(null);
        this.topAuthorName.setTag(null);
        setRootTag(root);
        // listeners
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x4L;
        }
        requestRebind();
    }

    @Override
    public boolean hasPendingBindings() {
        synchronized(this) {
            if (mDirtyFlags != 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean setVariable(int variableId, @Nullable Object variable)  {
        boolean variableSet = true;
        if (BR.user == variableId) {
            setUser((com.mooc.ppjoke.model.User) variable);
        }
        else if (BR.expand == variableId) {
            setExpand((java.lang.Boolean) variable);
        }
        else {
            variableSet = false;
        }
            return variableSet;
    }

    public void setUser(@Nullable com.mooc.ppjoke.model.User User) {
        updateRegistration(0, User);
        this.mUser = User;
        synchronized(this) {
            mDirtyFlags |= 0x1L;
        }
        notifyPropertyChanged(BR.user);
        super.requestRebind();
    }
    public void setExpand(@Nullable java.lang.Boolean Expand) {
        this.mExpand = Expand;
        synchronized(this) {
            mDirtyFlags |= 0x2L;
        }
        notifyPropertyChanged(BR.expand);
        super.requestRebind();
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
            case 0 :
                return onChangeUser((com.mooc.ppjoke.model.User) object, fieldId);
        }
        return false;
    }
    private boolean onChangeUser(com.mooc.ppjoke.model.User User, int fieldId) {
        if (fieldId == BR._all) {
            synchronized(this) {
                    mDirtyFlags |= 0x1L;
            }
            return true;
        }
        return false;
    }

    @Override
    protected void executeBindings() {
        long dirtyFlags = 0;
        synchronized(this) {
            dirtyFlags = mDirtyFlags;
            mDirtyFlags = 0;
        }
        java.lang.CharSequence stringConvertConvertSpannableUserScoreScoreCountAndroidStringScoreCount = null;
        int userLikeCount = 0;
        java.lang.String userName = null;
        java.lang.String userAvatar = null;
        boolean androidxDatabindingViewDataBindingSafeUnboxExpand = false;
        int userScore = 0;
        int userFollowCount = 0;
        java.lang.CharSequence stringConvertConvertSpannableUserFollowerCountFansCountAndroidStringFansCount = null;
        int expandViewGONEViewVISIBLE = 0;
        com.mooc.ppjoke.model.User user = mUser;
        java.lang.CharSequence stringConvertConvertSpannableUserLikeCountLikeCountAndroidStringLikeCount = null;
        java.lang.String userDescription = null;
        int userFollowerCount = 0;
        java.lang.Boolean expand = mExpand;
        java.lang.CharSequence stringConvertConvertSpannableUserFollowCountFollowCountAndroidStringFollowCount = null;

        if ((dirtyFlags & 0x5L) != 0) {



                if (user != null) {
                    // read user.likeCount
                    userLikeCount = user.likeCount;
                    // read user.name
                    userName = user.name;
                    // read user.avatar
                    userAvatar = user.avatar;
                    // read user.score
                    userScore = user.score;
                    // read user.followCount
                    userFollowCount = user.followCount;
                    // read user.description
                    userDescription = user.description;
                    // read user.followerCount
                    userFollowerCount = user.followerCount;
                }


                // read StringConvert.convertSpannable(user.likeCount, @android:string/like_count)
                stringConvertConvertSpannableUserLikeCountLikeCountAndroidStringLikeCount = com.mooc.ppjoke.utils.StringConvert.convertSpannable(userLikeCount, likeCount.getResources().getString(R.string.like_count));
                // read StringConvert.convertSpannable(user.score, @android:string/score_count)
                stringConvertConvertSpannableUserScoreScoreCountAndroidStringScoreCount = com.mooc.ppjoke.utils.StringConvert.convertSpannable(userScore, scoreCount.getResources().getString(R.string.score_count));
                // read StringConvert.convertSpannable(user.followCount, @android:string/follow_count)
                stringConvertConvertSpannableUserFollowCountFollowCountAndroidStringFollowCount = com.mooc.ppjoke.utils.StringConvert.convertSpannable(userFollowCount, followCount.getResources().getString(R.string.follow_count));
                // read StringConvert.convertSpannable(user.followerCount, @android:string/fans_count)
                stringConvertConvertSpannableUserFollowerCountFansCountAndroidStringFansCount = com.mooc.ppjoke.utils.StringConvert.convertSpannable(userFollowerCount, fansCount.getResources().getString(R.string.fans_count));
        }
        if ((dirtyFlags & 0x6L) != 0) {



                // read androidx.databinding.ViewDataBinding.safeUnbox(expand)
                androidxDatabindingViewDataBindingSafeUnboxExpand = androidx.databinding.ViewDataBinding.safeUnbox(expand);
            if((dirtyFlags & 0x6L) != 0) {
                if(androidxDatabindingViewDataBindingSafeUnboxExpand) {
                        dirtyFlags |= 0x10L;
                }
                else {
                        dirtyFlags |= 0x8L;
                }
            }


                // read androidx.databinding.ViewDataBinding.safeUnbox(expand) ? View.GONE : View.VISIBLE
                expandViewGONEViewVISIBLE = ((androidxDatabindingViewDataBindingSafeUnboxExpand) ? (android.view.View.GONE) : (android.view.View.VISIBLE));
        }
        // batch finished
        if ((dirtyFlags & 0x5L) != 0) {
            // api target 1

            com.mooc.ppjoke.view.PPImageView.setImageUrl(this.authorAvatarLarge, userAvatar, true, 0);
            androidx.databinding.adapters.TextViewBindingAdapter.setText(this.fansCount, stringConvertConvertSpannableUserFollowerCountFansCountAndroidStringFansCount);
            androidx.databinding.adapters.TextViewBindingAdapter.setText(this.followCount, stringConvertConvertSpannableUserFollowCountFollowCountAndroidStringFollowCount);
            androidx.databinding.adapters.TextViewBindingAdapter.setText(this.likeCount, stringConvertConvertSpannableUserLikeCountLikeCountAndroidStringLikeCount);
            androidx.databinding.adapters.TextViewBindingAdapter.setText(this.mboundView6, userDescription);
            androidx.databinding.adapters.TextViewBindingAdapter.setText(this.scoreCount, stringConvertConvertSpannableUserScoreScoreCountAndroidStringScoreCount);
            com.mooc.ppjoke.view.PPImageView.setImageUrl(this.topAuthorAvatar, userAvatar, true, (int)0);
            androidx.databinding.adapters.TextViewBindingAdapter.setText(this.topAuthorName, userName);
        }
        if ((dirtyFlags & 0x6L) != 0) {
            // api target 1

            this.topAuthorAvatar.setVisibility(expandViewGONEViewVISIBLE);
            this.topAuthorName.setVisibility(expandViewGONEViewVISIBLE);
        }
    }
    // Listener Stub Implementations
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): user
        flag 1 (0x2L): expand
        flag 2 (0x3L): null
        flag 3 (0x4L): androidx.databinding.ViewDataBinding.safeUnbox(expand) ? View.GONE : View.VISIBLE
        flag 4 (0x5L): androidx.databinding.ViewDataBinding.safeUnbox(expand) ? View.GONE : View.VISIBLE
    flag mapping end*/
    //end
}