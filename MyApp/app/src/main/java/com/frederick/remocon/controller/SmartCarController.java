package com.frederick.remocon.controller;

/**
 * Created by Frederick.
 */

public interface SmartCarController {

    void turnLeft(int degree);
    void turnRight(int degree);
    void speedUp(int amount);
    void speedDown(int amount);
}
