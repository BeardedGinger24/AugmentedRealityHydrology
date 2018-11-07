package edu.calstatela.jplone.watertrekapp.Data;

public class Vector2 {
    double x, y;

    Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2 add(Vector2 vector) {
        x += vector.x;
        y += vector.y;
        return this;
    }
}
