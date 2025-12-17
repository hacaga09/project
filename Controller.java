package com.example.game;

import javafx.scene.Group;

public class Controller {


    public void moveUp(Group map) {
        map.setLayoutY(map.getLayoutY()+ chr.speed);
    }
    public void moveDown(Group map) {
        map.setLayoutY(map.getLayoutY()- chr.speed);
    }
    public void moveLeft(Group map) {
        map.setLayoutX(map.getLayoutX()+ chr.speed);
    }
    public void moveRight(Group map) {
        map.setLayoutX(map.getLayoutX()- chr.speed);
    }
}