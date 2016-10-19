Name: James He
Date: October 19, 2016

Project 1:

Algorithms Implemented:

- Bresenham Line Algorithm
- Flood-fill Algorithm

Methodology:
- Handling one shape is straightforward: Use the Bresenham method to draw the borders and use flood-fill to color the interior.
- To handle two shapes, what I did was draw the 'front' shape first, fill it in with its color, and then draw the 'rear' shape next, ignore any previously colored pixel, and fill in its color.
  - Theoretically, the union of the two shapes will be filled this way (as well as for problem 3), but the Java components I worked had a few bugs.

To Run:
- Go to src->comp_geo_project1 and run the Comp_Geo_Project1 Java file in a Java environment (I used NetBeans). There were some bugs with Eclipse.
- To try and run different types of input files, change line 172 with the appropriate file name
- Inside the zip folder should also contain three different input files (Input1, Input2, and Input3)
	- Input 1 - Problem 1 (1 shape, differing border and interior colors)
	- Input 2 - Problem 2 (2 shapes, union color)
	- Input 3 - Problem 3 (2 shapes, intersection is colored with 'front' color)

Bugs:
- Note that there appears to be very thin white lines in the coloring, this may be due to the fact that several pixels are not shown in the Bresenham line draw
  and thus not filled in the floodfill (since there was no pixel colored in that column to begin with).
- In Inputs 2 and 3, there is a section near the bottom of the shape that is not filled in red.
  - A possible reason is that it may be due to the Graphics component of the BufferedImage class
  - It is possible that JFrame fills in its Graphics component differently and at some point, the max frame level was reached.
  - The Graphics component renders the images and the exact mechanics are all Java-specific and its intricacies were not something I was able to completely unravel

Extra Credit:
- The flood-fill algorithm is written based on the horizontal line sweep method and is not implemented recursively.