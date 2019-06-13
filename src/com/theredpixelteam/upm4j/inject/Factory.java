package com.theredpixelteam.upm4j.inject;

import com.theredpixelteam.redtea.util.Cluster;

public interface Factory {
    public Object produce(Cluster arguments) /*throws IllegalArgumentException*/;
}
