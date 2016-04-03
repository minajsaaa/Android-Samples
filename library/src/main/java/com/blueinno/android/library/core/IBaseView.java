package com.blueinno.android.library.core;

public interface IBaseView {

    int getLayoutContentView();
    void initialize();
    void createChildren();
    void configureListener();
    void setProperties();

}
