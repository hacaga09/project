package com.example.gameproject;

import javafx.scene.layout.Pane;

public class Controller {

    public void up(Pane map) {
        map.setLayoutY(map.getLayoutY() + chr.speed);
    }
    public void down(Pane map) {
        map.setLayoutY(map.getLayoutY() - chr.speed);
    }
    public void left(Pane map) {
        map.setLayoutX(map.getLayoutX() + chr.speed);
    }
    public void right(Pane map) {
        map.setLayoutX(map.getLayoutX() - chr.speed);
    }
}
