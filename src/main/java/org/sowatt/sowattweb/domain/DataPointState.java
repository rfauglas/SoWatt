package org.sowatt.sowattweb.domain;

import tuwien.auto.calimero.datapoint.Datapoint;

public class DataPointState<E> {
    private Datapoint datapoint;
    private E value;

    public Datapoint getDatapoint() {
        return datapoint;
    }

    public DataPointState<E> setDatapoint(Datapoint datapoint) {
        this.datapoint = datapoint;
        return this;
    }

    public E getValue() {
        return value;
    }

    public DataPointState<E> setValue(E value) {
        this.value = value;
        return this;
    }
}
