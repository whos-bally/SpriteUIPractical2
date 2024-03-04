
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.util.ArrayList;
import javax.swing.ImageIcon;

/**
    The Animation class manages a series of images (frames) and
    the amount of time to display each frame.
*/
public class Animation {

    private ArrayList frames;
    private int currFrameIndex;
    private long animTime;
    private long totalDuration;


    /**
        Creates a new, empty Animation.
    */
    public Animation() {
        frames = new ArrayList();
        totalDuration = 0;
        start();
    }


    /**
        Adds an image to the animation with the specified
        duration (time to display the image).
    */
    public synchronized void addFrame(Image image,
        long duration)
    {
        totalDuration += duration;
        frames.add(new AnimFrame(image, totalDuration));
    }


    /**
        Starts this animation over from the beginning.
    */
    public synchronized void start() {
        animTime = 0;
        currFrameIndex = 0;
    }


    /**
        Updates this animation's current image (frame), if
        neccesary.
    */
    public synchronized void update(long elapsedTime) {
        if (frames.size() > 1) {
            animTime += elapsedTime;

            if (animTime >= totalDuration) {
                animTime = animTime % totalDuration;
                currFrameIndex = 0;
            }

            while (animTime > getFrame(currFrameIndex).endTime) {
                currFrameIndex++;
            }
        }
    }


    /**
        Gets this Animation's current image. Returns null if this
        animation has no images.
    */
    public synchronized Image getImage() {
        if (frames.size() == 0) {
            return null;
        }
        else {
            return getFrame(currFrameIndex).image;
        }
    }

    /**
     * Loads a complete animation from an animation sheet and adds each
     * frame in the sheet to the animation with the given frameDuration.
     * 
     * @param fileName	The path to the file to load the animations from
     * @param rows		How many rows there are in the sheet
     * @param columns	How many columns there are in the sheet
     * @param frameDuration	The duration of each frame
     */
    public void loadAnimationFromSheet(String fileName, int columns, int rows, int frameDuration)
    {
    	Image sheet = new ImageIcon(fileName).getImage();
    	Image[] images = getImagesFromSheet(sheet, columns, rows);
    	
    	for (int i=0; i<images.length; i++)
    	{
    		addFrame(images[i], frameDuration);
    	}
    }
    

    
    /**
     * Loads a set of images from a sprite sheet so that they can be added to an animation.
     * Courtesy of Donald Robertson.
     * 
     * @param sheet
     * @param rows
     * @param columns
     * @return
     */
    private Image[] getImagesFromSheet(Image sheet, int columns, int rows) {

        // basic method to achieve split of sprite sheet
        // overloading could be used to achieve more complex things 
    	// such as sheets where all images are not the same dimensions
        // deliberately 'overcommented' for clarity when integrating with
    	// main engine

        // initialise image array to return
        Image[] split = new Image[rows*columns];

        // easiest way to count as going through sprite sheet as though it is a 2d array
        int count = 0;

        // initialise width & height of split up images
        int width = sheet.getWidth(null)/columns;
        int height = sheet.getHeight(null)/rows;

        // for each column in each row
        for(int i = 0; i < rows; i++) 
        {
            for(int j = 0; j < columns; j++) 
            {
            	// create an image filter
            	// top left (x) = j*width, (y) = i*height
            	// extract rectangular region of width and height from origin x,y
            	ImageFilter cropper = new CropImageFilter(j*width,i*height, width, height);
            	
                // create image source based on original sprite sheet with filter applied
                // results in image source for cropped image being generated
                FilteredImageSource cropped = new FilteredImageSource(sheet.getSource(), cropper);
                
                // create a new image using generated image source and store in appropriate array element
                split[count] = Toolkit.getDefaultToolkit().createImage(cropped);
                        
                // increment count to prevent elements being overwritten
                count++;
            }
        }

        // return array
        return split;
    }
    
    private AnimFrame getFrame(int i) {
        return (AnimFrame)frames.get(i);
    }


    private class AnimFrame {

        Image image;
        long endTime;

        public AnimFrame(Image image, long endTime) {
            this.image = image;
            this.endTime = endTime;
        }
    }
}
