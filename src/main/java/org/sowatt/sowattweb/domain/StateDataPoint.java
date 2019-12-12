package org.sowatt.sowattweb.domain;

import tuwien.auto.calimero.datapoint.Datapoint;

public class StateDataPoint<E> {
    private Datapoint datapoint;
    private E value;

    public Datapoint getDatapoint() {
        return datapoint;
    }

    public StateDataPoint<E> setDatapoint(Datapoint datapoint) {
        this.datapoint = datapoint;
        return this;
    }

    public E getValue() {
        return value;
    }

    public StateDataPoint<E> setValue(E value) {
        this.value = value;
        return this;
    }
}
