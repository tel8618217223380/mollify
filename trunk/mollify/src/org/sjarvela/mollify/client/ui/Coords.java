package org.sjarvela.mollify.client.ui;

public class Coords {
	int x, y, width, height;

	public Coords(int x, int y, int width, int height) {
		super();
		this.height = height;
		this.width = width;
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
