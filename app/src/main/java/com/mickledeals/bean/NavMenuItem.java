package com.mickledeals.bean;

/**
 * Created by Nicky on 11/28/2014.
 */
public class NavMenuItem {

    private Class mNavClass; //can be activity or fragment
    private int mTitleRes;
    private int mIconRes;
    private boolean mNeedLogin;

    public NavMenuItem(Class navClass, int titleRes, int iconRes, boolean needLogin) {
        mNavClass = navClass;
        mTitleRes = titleRes;
        mIconRes = iconRes;
        mNeedLogin = needLogin;
    }

    public Class getNavClass() {
        return mNavClass;
    }

    public int getTitleRes() {
        return mTitleRes;
    }

    public int getIconRes() {
        return mIconRes;
    }

    public boolean needsLogin() {
        return mNeedLogin;
    }
}
