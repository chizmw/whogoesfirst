package net.chizography.droid.whosfirst;

public class CircleArea {
    int radius;
    int centerX;
    int centerY;
	
	boolean needs_wiping = false;
	boolean first_player = false;

    CircleArea(int centerX, int centerY, int radius) {
        this.radius = radius;
        this.centerX = centerX;
        this.centerY = centerY;
    }

    @Override
    public String toString() {
        return "Circle[" + centerX + ", " + centerY + ", " + radius + "]";
    }
}
