package org.example;

import lombok.Getter;
import lombok.Setter;

class Point {
    @Getter @Setter
    private int x, y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
}