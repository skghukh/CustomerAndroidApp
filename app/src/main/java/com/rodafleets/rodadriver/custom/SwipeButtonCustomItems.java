package com.rodafleets.rodadriver.custom;

public abstract class SwipeButtonCustomItems {
    //These are the default values if we don't choose to set them later:
    public int gradientColor1 = 0xFF765267;
    public int gradientColor2 = 0xFF5A374B;
    public int gradientColor2Width = 400;
    public int gradientColor3 = 0xFF5A374B;
    public int postConfirmationColor = 0xFF765267;
    public double actionConfirmDistanceFraction = 0.7;
    public String buttonPressText = "Loading....";

    public String actionConfirmText = null;

    public int getGradientColor1() {
        return gradientColor1;
    }

    public SwipeButtonCustomItems setGradientColor1(int gradientColor1) {
        this.gradientColor1 = gradientColor1;
        return this;
    }

    public int getGradientColor2() {
        return gradientColor2;
    }

    public SwipeButtonCustomItems setGradientColor2(int gradientColor2) {
        this.gradientColor2 = gradientColor2;
        return this;
    }

    public int getGradientColor2Width() {
        return gradientColor2Width;
    }

    public SwipeButtonCustomItems setGradientColor2Width(int gradientColor2Width) {
        this.gradientColor2Width = gradientColor2Width;
        return this;
    }

    public int getGradientColor3() {
        return gradientColor3;
    }

    public SwipeButtonCustomItems setGradientColor3(int gradientColor3) {
        this.gradientColor3 = gradientColor3;
        return this;
    }

    public double getActionConfirmDistanceFraction() {
        return actionConfirmDistanceFraction;
    }

    public SwipeButtonCustomItems setActionConfirmDistanceFraction(double actionConfirmDistanceFraction) {
        this.actionConfirmDistanceFraction = actionConfirmDistanceFraction;
        return this;
    }

    public String getButtonPressText() {
        return buttonPressText;
    }

    public SwipeButtonCustomItems setButtonPressText(String buttonPressText) {
        this.buttonPressText = buttonPressText;
        return this;
    }

    public String getActionConfirmText() {
        return actionConfirmText;
    }

    public SwipeButtonCustomItems setActionConfirmText(String actionConfirmText) {
        this.actionConfirmText = actionConfirmText;
        return this;
    }

    public int getPostConfirmationColor() {
        return postConfirmationColor;
    }

    public SwipeButtonCustomItems setPostConfirmationColor(int postConfirmationColor) {
        this.postConfirmationColor = postConfirmationColor;
        return this;
    }

    //These methods listed below can be overridden in the instance of SwipeButton and
    public void onButtonPress(){

    }

    public void onSwipeCancel(){

    }

    abstract public void onSwipeConfirm();
}