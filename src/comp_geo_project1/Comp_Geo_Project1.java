package comp_geo_project1;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.*;

public class Comp_Geo_Project1 extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private BufferedImage canvas;
        private HashMap<Integer, ArrayList<Integer>> visited1; 
        private HashMap<Integer, ArrayList<Integer>> visited2; 
        
        // Constructor
        public Comp_Geo_Project1(int width, int height) {
            canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB); // Image renderer
            visited1 = new HashMap<>(); // Keeps track of all pixels visited for polygon1
            visited2 = new HashMap<>(); // Keeps track of all pixels visited for polygon2 (trivial for a single shape)
        }
	
	// Parses an input line for the x and y coordinates
	public ArrayList<Integer> parseLine(ArrayList<Integer> xy, String line) {
            boolean comma = false; // flag when comma is hit
            String firstCoordinate = "";
            String secondCoordinate = "";
            for (int i = 0; i < line.length(); i++) {
		String current = line.substring(i, i+1);
		if (!current.equals("(") && !current.equals(")")) {
                    if (current.equals(","))
			comma = true;
                    else {
			if (!comma)
                            firstCoordinate += current;
			else
                            secondCoordinate += current;
                    }
		}
            }
            xy.add(Integer.parseInt(firstCoordinate));
            xy.add(Integer.parseInt(secondCoordinate)-200); // CHANGE HERE - Adjusted to fit screen size
            return xy;
	}

	// Parses an input line for the color
	public Color parseColor(String line) {
            Color color = new Color(0, 0, 0);
            String c1 = "";
            String c2 = "";
            String c3 = "";
            int commacount = 0;
            for (int i = 0; i < line.length(); i++) {
		String s = line.substring(i, i+1);
		if (s.equals("(") || s.equals(" ") || s.equals(")"))
                    continue;
                if (s.equals(",")) {
                    commacount++;
                    continue;
                }
                if (commacount == 0)
                    c1 += s;
                else if (commacount == 1)
                    c2 += s;
                else
                    c3 += s;
            }
            color = new Color(Integer.parseInt(c1), Integer.parseInt(c2), Integer.parseInt(c3));
            return color;
        }
	
	// flag: parameter that keeps track of which shape is being drawn (specifically for problems 2 and 3)
	// Color c: represents the color to be filled
	// Color orig: original color of the pixel
	// Color asdf: to distinguish between union and intersect cases
	public void floodfill(Graphics g, ArrayList<Integer> x, ArrayList<Integer> y, Color c, Color asdf, Color orig, int flag) {
            // Find max and min x and y coordinates
            int maxx = x.get(0);
            int minx = x.get(0);
            int maxy = y.get(0);
            int miny = y.get(0);
            for (int i = 0; i < y.size(); i++) {
		if (y.get(i) > maxy)
                    maxy = y.get(i);
		if (y.get(i) < miny)
                    miny = y.get(i);
		if (x.get(i) > maxx)
                    maxx = x.get(i);
		if (x.get(i) < minx)
                    minx = x.get(i);
            }
            // Implements the flood-fill algorithm using horizontal line sweep
            for (int i = miny; i <= maxy; i++) {
                int count = 0;
                boolean u = false; // keeps track of if row has only one pixel in shape
                ArrayList<Integer> maxxx = new ArrayList<>();
                for (int j = minx; j <= maxx; j++) {
                    boolean v1 = false;
                    if (visited1.get((Integer)j) != null) {
                        for (int o = 0; o < visited1.get((Integer)j).size(); o++)
                            if (visited1.get((Integer)j).get(o) == i)
                                v1 = true;
                        }
                        if (v1) {
                            if (flag == 1) {
                                g.setColor(orig);
                                g.drawLine(j, i, j, i);
                                count++;            
                            }
                            else {
                                maxxx.add(j);
                                count = 1;
                                u = true;
                            }
                            continue;
                        }
			if (count > 1)
                            continue;
			if (canvas.getRGB(j, i) == orig.getRGB())
                            count++;
                        else if ((count == 1) && canvas.getRGB(j, i) != c.getRGB()) {
                            if (flag == 1) {
                                if (visited1.get((Integer)j) != null) {
                                    ArrayList<Integer> a = visited1.get((Integer)j);
                                    a.add(i);
                                    visited1.put((Integer)j, a);
                                }
                                else {
                                    ArrayList<Integer> li = new ArrayList<>();
                                    li.add(i);
                                    visited1.put((Integer)j, li);    
                                }
                            }
                            g.setColor(c);
                            g.drawLine(j, i, j, i);
                        }
		}
                // For cases when for a row x, there is only one point of the shape present
                if (count == 1 && !u) {
                    for (int z = minx; z<= maxx; z++) {
                        g.setColor(getBackground());
			g.drawLine(z, i, z, i);
                    }
		}
                // For cases when for a row x, there is only one point of the shape present (for the second shape)
                if (flag == 2) {
                    if (count == 1) {
                        if (maxxx.size() != 0) {
                            for (int zz = maxxx.get(maxxx.size()-1); zz <= maxx; zz++) {
				g.setColor(getBackground());
				g.drawLine(zz, i, zz, i);
                            }
                        }
                    }
                }
            }
	}
	
	public void paint(Graphics g) {
            // These two arrays will be of same size, assuming no incorrect input ('incorrect' as in giving only x-coordinate without the y, etc.)
            ArrayList<Integer> x = new ArrayList<>(); // holds all x-coordinates
            ArrayList<Integer> y = new ArrayList<>(); // holds all y-coordinates
            ArrayList<ArrayList<Integer>> xxyy = new ArrayList<>();
            ArrayList<Color> colors = new ArrayList<>();
            try {
                // INPUT FILE here
		File file = new File("Input1.txt"); 
                ///////////////////////////////////
                
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String first = bufferedReader.readLine();
		colors.add(new Color(255, 0, 0));
		// Problem 1: If only one polygon is given
		if (first.equalsIgnoreCase("P")) {
                    // This while loop essentially reads in a line of text and parses it for the x and y-coordinates
                    x = new ArrayList<>();
                    y = new ArrayList<>();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
			ArrayList<Integer> xy = new ArrayList<>();
			xy = parseLine(xy, line);
			x.add(xy.get(0));
			y.add(xy.get(1));
                    }
                    xxyy.add(x);
                    xxyy.add(y);
		}
		else {
                    x = new ArrayList<>();
                    y = new ArrayList<>();
				
                    String line;
                    line = bufferedReader.readLine();
                    line = bufferedReader.readLine();
                    colors.add(parseColor(line));
                    while ((line = bufferedReader.readLine()) != null && !line.equalsIgnoreCase("p2")) {
			ArrayList<Integer> xy = new ArrayList<>();
			xy = parseLine(xy, line);		
                        x.add(xy.get(0));
			y.add(xy.get(1));
                    }
                    xxyy.add(x);
                    xxyy.add(y);
                    line = bufferedReader.readLine();
                    colors.add(parseColor(line));
                    x = new ArrayList<>();
                    y = new ArrayList<>();
                    while ((line = bufferedReader.readLine()) != null) {
			ArrayList<Integer> xy = new ArrayList<>();
			xy = parseLine(xy, line);			
			x.add(xy.get(0));
			y.add(xy.get(1));
                    }
                    xxyy.add(x);
                    xxyy.add(y);
		}
		if (xxyy.size() > 2) {
                    int minx1 = xxyy.get(0).get(0);
                    int minx2 = xxyy.get(2).get(0);
                    for (int i = 0; i < xxyy.get(0).size(); i++)
			if (xxyy.get(0).get(i) < minx1)
                            minx1 = xxyy.get(0).get(i);
                    for (int i = 0; i < xxyy.get(2).size(); i++) 
                        if (xxyy.get(2).get(i) < minx2)
                            minx2 = xxyy.get(2).get(i);
                    if (minx2 < minx1) {
                        ArrayList<Integer> temp1 = xxyy.get(0);
                        ArrayList<Integer> temp2 = xxyy.get(1);
                        xxyy.remove(1);
                        xxyy.remove(0);
                        xxyy.add(temp1);
                        xxyy.add(temp2);
                        Color temp = colors.get(0);
                        colors.remove(0);
                        colors.add(temp);
                    }	
                }
                fileReader.close();
            } catch (IOException e) {
		e.printStackTrace();
            }
		
            // Implements the Bresenham line algorithm
            // Loops through all points, connecting each pair
            for (int k = 0; k < xxyy.size(); k += 2) {
		x = xxyy.get(k);
		y = xxyy.get(k+1);
		Color color = colors.get(k/2);
		for (int j = 0; j < x.size(); j++) {
                    // Gets the two points that will be connected
                    int x1 = x.get(j);
                    int y1 = y.get(j);
                    int x2;
                    int y2;
                    // If end of array as reached, will connect the last point with the first point
                    if (j+1 == x.size()) {
			x2 = x.get(0);
			y2 = y.get(0);
                    }
                    else  {
                        x2 = x.get(j+1);
			y2 = y.get(j+1);
                    } 
                    // Width and height of region we are working with
                    int width = x2 - x1;
                    int height = y2 - y1;
                    // Signifies change between x and y-coordinates
                    int dx1 = 0; 
                    int dy1 = 0;
                    int dx2 = 0;
                    int dy2 = 0;
		    
                    if (width < 0) {
			dx1 = -1;
			dx2 = -1;
                    }
                    else if (width > 0) { 
			dx1 = 1;
			dx2 = 1;
                    }
                    if (height < 0) 
			dy1 = -1; 
                    else if (height > 0) 
			dy1 = 1;
                    int longest = Math.abs(width);
                    int shortest = Math.abs(height);
		    if (!(longest > shortest)) {
		       	longest = Math.abs(height);
		       	shortest = Math.abs(width);
		       	if (height < 0) 
                            dy2 = -1; 
		       	else if (height > 0) 
                            dy2 = 1;
		        dx2 = 0;            
		    }
		    int num = longest/2;
		    // This loop paints the specific pixel and updates the coordinates
		    for (int i = 0; i <= longest; i++) {
                        // Adds pixel to corresponding visited arraylist then colors the pixel
		    	canvas.setRGB(x1, y1, color.getRGB());
                        if (k == 0) {
                            if (visited1.get((Integer)x1) != null) {
                                ArrayList<Integer> b = visited1.get((Integer)x1);
                                b.add(y1);
                                visited1.put((Integer)x1, b);
                            }
                            else {
                                ArrayList<Integer> li = new ArrayList<>();
                                li.add(y1);
                                visited1.put((Integer)x1, li);    
                            }
                        }
                        else {
                            if (visited2.get((Integer)x1) != null) {
                                ArrayList<Integer> b = visited2.get((Integer)x1);
                                b.add(y1);
                                visited2.put((Integer)x1, b);
                            }
                            else {
                                ArrayList<Integer> li = new ArrayList<>();
                                li.add(y1);
                                visited2.put((Integer)x1, li);    
                            }
                                    
                        }
                        if (canvas.getRGB(x1,y1) == getBackground().getRGB()) {
                            g.setColor(color);
                            g.drawLine(x1, y1, x1, y1);
                        }
		    	num += shortest;
		    	if (!(num < longest)) {
                            num -= longest;
                            x1 += dx1 ;
                            y1 += dy1 ;
		    	}
                        else {
                            x1 += dx2 ;
                            y1 += dy2 ;
		    	}
		    }	
		}
                // Next, fill in the shape
		if (xxyy.size() == 2)
                    floodfill(g, x, y, Color.YELLOW, color, color, 1);
		else {
                    if (k == 2)
                        floodfill(g, x, y, colors.get(k/2), colors.get((k-1)/2), colors.get(k/2), 2);
                    else
                        floodfill(g, x, y, colors.get(k/2), colors.get((k-1)/2), colors.get(k/2), 1);
                }	
            }
	}
        
        // main method: users Java's JFrame component to render image
	public static void main(String[] args) {
            JFrame frame1 = new JFrame("Project 1");	
		
            frame1.add(new Comp_Geo_Project1(1000, 1000));
            frame1.setSize(1000, 1000);
            frame1.setLocationRelativeTo(null);
            frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame1.setVisible(true);
	}

}

