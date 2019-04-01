package resources;

import java.awt.Font;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Rectangle;

public class UIButton {

	private String text;
	private int x, y, width, height;
	private Color bg = Color.lightGray, hl = Color.black; // Background, Highlight
	private boolean highlighted = false;
	private Rectangle bounds;
	private TrueTypeFont font;
	
	public UIButton(String text, int x, int y, int width, int height) {
		this.text = text;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		bounds = new Rectangle(x, y, width, height);
		font = new TrueTypeFont(new Font("verdana", Font.BOLD, 30), true);
	}
	
	public void draw(Graphics g) {
		g.setColor((highlighted) ? hl : bg);
		g.fillRoundRect(x, y, width, height, 5);
		g.setColor((!highlighted) ? hl : bg);
		g.drawRoundRect(x, y, width, height, 5);
		g.setFont(font);
		g.drawString(text, (x + width / 2) - (g.getFont().getWidth(text) / 2), (y + height / 2) - (g.getFont().getHeight(text) / 2));
	}

	public void changeColours(Color bg, Color hl) {
		this.bg = bg;
		this.hl = hl;
	}
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public boolean isHighlighted() {
		return highlighted;
	}

	public void setHighlighted(boolean highlighted) {
		this.highlighted = highlighted;
	}

	public String getText() {
		return text;
	}

	public Rectangle getBounds() {
		return bounds;
	}
	
	
}
