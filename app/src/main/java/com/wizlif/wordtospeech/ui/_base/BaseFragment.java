package com.wizlif.wordtospeech.ui._base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.Navigator;

import com.tbruyelle.rxpermissions2.RxPermissions;

import dagger.android.support.DaggerFragment;

public abstract class BaseFragment<T extends ViewDataBinding, V extends BaseViewModel> extends DaggerFragment {

    private static final String TAG = "BaseFragment";

    private BaseActivity mActivity;
    private View mRootView;
    private T mViewDataBinding;
    private V mViewModel;

    private NavController mNavController;

    private RxPermissions mPermission;

    public RxPermissions getMPermission() {
        return mPermission;
    }

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    public abstract int getBindingVariable();

    /**
     * @return layout resource id
     */
    public abstract
    @LayoutRes
    int getLayoutId();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewDataBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        mRootView = mViewDataBinding.getRoot();
        setHasOptionsMenu(true);
        mPermission = new RxPermissions(requireActivity());
        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            mNavController = Navigation.findNavController(mRootView);
        } catch (Exception e) {
//            Timber.e(e);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = getViewModel();
    }

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    public abstract V getViewModel();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity) {
            BaseActivity activity = (BaseActivity) context;
            this.mActivity = activity;
//            activity.onFragmentAttached();
        }
    }


    public BaseActivity getBaseActivity() {
        return mActivity;
    }

    public T getViewDataBinding() {
        return mViewDataBinding;
    }

    public void hideKeyboard() {
        if (mActivity != null) {
            mActivity.hideKeyboard();
        }
    }

    public void showKeyboard() {
        if (mActivity != null) {
            mActivity.showKeyboard();
        }
    }


    protected <Z> Intent newIntent(Class<Z> clazz) {
        return new Intent(getBaseActivity(), clazz);
    }

    protected <Z> void newActivity(Class<Z> clazz) {
        startActivity(newIntent(clazz));
    }

    //    @Inject


    protected NavController getmNavController() {
        return mNavController;
    }

    protected void navigateSafe(NavDirections navDirections) {
        if (mNavController != null && navDirections != null) {
            if (mNavController.getCurrentDestination() != null && mNavController.getCurrentDestination().getId() != navDirections.getActionId()) {
                mNavController.navigate(navDirections);
            }
        }
    }

    public void navigateSafe(NavDirections navDirections, NavOptions navOptions) {
        if (mNavController != null && navDirections != null) {
            if (mNavController.getCurrentDestination() != null && mNavController.getCurrentDestination().getId() != navDirections.getActionId()) {
                mNavController.navigate(navDirections, navOptions);
            }
        }
    }

    public void navigateSafe(NavDirections navDirections, Navigator.Extras navExtras) {
        if (mNavController != null && navDirections != null) {
            if (mNavController.getCurrentDestination() != null && mNavController.getCurrentDestination().getId() != navDirections.getActionId()) {
                mNavController.navigate(navDirections, navExtras);
            }
        }
    }
}
