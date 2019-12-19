package org.sowatt.sowattweb.domain;

import tuwien.auto.calimero.datapoint.Datapoint;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ButtonState {
    LocalDateTime datePressed;
    Boolean pressed =false;

    public Boolean getPressed() {
        return pressed;
    }

    public ButtonState setPressed(Boolean pressed) {
        this.pressed = pressed;
        return this;
    }

    List<Datapoint> datapoints = new ArrayList<>();

    public List<Datapoint> getDatapoints() {
        return datapoints;
    }

    public LocalDateTime getDatePressed() {
        return datePressed;
    }

    public ButtonState setDatePressed(LocalDateTime datePressed) {
        this.datePressed = datePressed;
        return this;
    }
}
