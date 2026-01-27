package com.example.gameproject;

import javafx.scene.layout.Pane;

public class Controller {

    public void moveUp(Pane map) {
        map.setLayoutY(map.getLayoutY() + chr.speed);
    }
    public void moveDown(Pane map) {
        map.setLayoutY(map.getLayoutY() - chr.speed);
    }
    public void moveLeft(Pane map) {
        map.setLayoutX(map.getLayoutX() + chr.speed);
    }
    public void moveRight(Pane map) {
        map.setLayoutX(map.getLayoutX() - chr.speed);
    }
}
