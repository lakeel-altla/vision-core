package com.lakeel.altla.vision.builder.presentation.view;

public interface ConsoleView {

    void onUpdateLocalized(boolean localized);

    void onUpdateAd2SsTranslation(double x, double y, double z);

    void onUpdateAd2DTranslation(double x, double y, double z);

    void onUpdateSs2DTranslation(double x, double y, double z);
}
