package org.example;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString @AllArgsConstructor
class Point {
    @Getter @Setter
    private int x, y;

}