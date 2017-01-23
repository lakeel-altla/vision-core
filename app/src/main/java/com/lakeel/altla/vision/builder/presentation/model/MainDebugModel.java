package com.lakeel.altla.vision.builder.presentation.model;

public final class MainDebugModel {

    public boolean localized;

    public final Translation ad2SsTranslation = new Translation();

    public final Translation ad2DTranslation = new Translation();

    public final Translation ss2DTranslation = new Translation();

    public final class Translation {

        public double x;

        public double y;

        public double z;
    }
}
