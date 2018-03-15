package edu.stanford.cs108.bunnyworld;

import java.util.List;

/**
 * Created by xiaofeng on 3/13/18.
 */

public class ShapeSingleton {
    private static final ShapeSingleton ourInstance = new ShapeSingleton();

    static ShapeSingleton getInstance() {
        return ourInstance;
    }

    // Shape is non-null if Advanced button clicked
    Shape selectedShape;
    List<Shape> shapes;

    public Shape getSelectedShape() {
        return selectedShape;
    }

    public void setSelectedShape(Shape selectedShape) {
        this.selectedShape = selectedShape;
    }

    public List<Shape> getSelectedShapeContainer() {
        return shapes;
    }

    public void setSelectedShapeContainer(List<Shape> shapes) {
        this.shapes = shapes;
    }


    private ShapeSingleton() {
    }

}
