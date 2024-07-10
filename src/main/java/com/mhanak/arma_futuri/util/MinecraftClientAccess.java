package com.mhanak.arma_futuri.util;

import org.apache.commons.lang3.tuple.Pair;

public interface MinecraftClientAccess {
    void arma_futuri$addCameraVelocity(float x, float y);
    void arma_futuri$setCameraVelocity(float x, float y);
    Pair<Float, Float> arma_futuri$getCameraVelocity();
}
