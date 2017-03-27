package com.lakeel.altla.vision.builder.presentation.model;

import com.lakeel.altla.vision.model.Area;
import com.lakeel.altla.vision.model.AreaDescription;
import com.lakeel.altla.vision.model.AreaScope;

import org.parceler.Parcel;
import org.parceler.Transient;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

@Parcel(Parcel.Serialization.BEAN)
public final class AreaSettingsModel {

    private AreaScope areaScope = AreaScope.UNKNOWN;

    private Area area;

    private AreaDescription areaDescription;

    @Transient
    @NonNull
    public AreaScope getAreaScope() {
        return areaScope;
    }

    public void setAreaScope(@NonNull AreaScope areaScope) {
        this.areaScope = areaScope;
    }

    public int getAreaScopeValue() {
        return areaScope.getValue();
    }

    public void setAreaScopeValue(int areaScopeValue) {
        areaScope = AreaScope.toAreaScope(areaScopeValue);
    }

    @Nullable
    public Area getArea() {
        return area;
    }

    public void setArea(@Nullable Area area) {
        this.area = area;
    }

    @Nullable
    public AreaDescription getAreaDescription() {
        return areaDescription;
    }

    public void setAreaDescription(@Nullable AreaDescription areaDescription) {
        this.areaDescription = areaDescription;
    }
}
