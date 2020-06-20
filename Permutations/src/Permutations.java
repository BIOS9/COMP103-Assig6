// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2018T2, Assignment 6
 * Name: Matthew Corfiatis
 * Username: CorfiaMatt
 * ID: 300447277
 */

import ecs100.*;

import javax.swing.*;
import java.util.*;
import java.awt.Color;

/**
 *  Show all permutations of a list of items
 *  The items are names of image files (of emojis)
 *  Displays each possible permutation as a column of images.
 *  If there are too many for the screen, it highlights and keeps replacing the last column.
 *
 *  For the core, you only have to write one method - the extendPermutations(...) method
 *  which does the recursive search.  
 */
public class Permutations {

    public static final List<String> IMG_NAMES =
        Arrays.asList("smiling.png", "omg.png", "halo.png", "crying.png","angry.png",
                      "sleeping.png", "astonished.png", "smiling-eyes.png", "sunglasses.png",
                      "sweat.png", "blue.png", "brown.png", "gray.png", "navy.png", "orange.png",
                      "pink.png");

    public static final double DISPLAY_LEFT = 10;
    public static final double DISPLAY_TOP = 20;
    public static final double IMAGE_SIZE = 40;
    public static final double DISPLAY_SEP = IMAGE_SIZE + 2;

    private JButton pauseButton;

    private List<String> selectedList;  // current list to permute and display

    private boolean displayPaused = false;
    private int nextCol = 0;            // current row to display.
    private long permutationCount = 0;  // number of permutations found so far.

    /**
     * Construct a new Permutations object, setting up the GUI
     */
    public Permutations(){
            setupGUI();
            selectedList = new ArrayList<String>();
            reset();
        }

        /**
         * Buttons to reset and run permutations.
         * Mouse to select images to permute
         */
        public void setupGUI(){
            UI.addButton("Reset list", this::reset);
            UI.addButton("Permute", this::findPermutations);
            UI.addButton("Permute Iterative", this::bogoPermutations);
            pauseButton = UI.addButton("Pause display", this::playPauseDisplay);
            UI.addButton("Clear display", this::clear);
            UI.addButton("Quit", UI::quit);
            UI.setMouseListener(this::doMouse);
            UI.setWindowSize(1050,800);
            UI.setDivider(0);
        }


        /**
         * Finds and prints all permutations of the items in selectedList,
         * by calling a recursive method, passing in a set of the items to permute
         * and an empty list to build up.
         * Prints the total number of permutations in the message window (with
         *  UI.printMessage(...);
         */

    public void findPermutations(){
        UI.printMessage(String.format("permuting %d items", selectedList.size()));
        UI.println(String.format("permuting %d items", selectedList.size()));
        nextCol = 2;
        Set<String> source = new HashSet<>(selectedList); //a set of the current items
        permutationCount = 0;
        extendPermutation(new ArrayList<>(), source);
        UI.printMessage(String.format("FINISHED: %d  items: %,d permutations",
                                      source.size(), permutationCount));
        UI.println(String.format("FINISHED: %d  items: %,d permutations",
                source.size(), permutationCount));
    }

    /**
     * Pauses display of each new permutation
     */
    private void playPauseDisplay()
    {
        if(displayPaused)
        {
            UI.println("Permutations so far: " + permutationCount);
            pauseButton.setText("Pause display");
            displayPaused = false;
        }
        else
        {
            pauseButton.setText("Resume display");
            displayPaused = true;
        }
    }

    /**
     * Recursive method to build all permutations possible by adding the
     *  remaining items on to the end of the permutation built up so far 
     * If there are no remaining items, then permutationSoFar is complete,
     *   so display permutationSoFar and increment permutationCount.
     *   Use the display(...) method to display a permutation
     * Otherwise,
     *  for each of the remaining items,
     *    Extend the permutation with one of the item, and do a recursive call to extend it more
     *     - remove the item from remaining items
     *     - add it to the permutation so far
     *     - do the recursive call 
     *     - remove the item from the end of the permutation and
     *     - put it back into the remaining items.
     *
     * Remember: you can't modify a collection that you are currently iterating through!!
     *  you may need to make a copy of the remaining items to iterate through it.
     */
    public void extendPermutation(List<String> permutationSoFar, Set<String> remaining){
        if(remaining.isEmpty())
        {
            ++permutationCount;
            if(!displayPaused)
                display(permutationSoFar);
        }
        else
        {
            for(String item : remaining)
            {
                Set<String> r = new HashSet<>(remaining);
                r.remove(item);
                permutationSoFar.add(item);
                extendPermutation(permutationSoFar, r);
                permutationSoFar.remove(permutationSoFar.size() - 1);
            }
        }
    }

    /**
     * Bogo permutations iteratively finds all permutations for a given number of elements
     *
     * Calculates how many elements there should be then uses a hash set to store unique combinations.
     * Randomly generates a combination and adds it the set if its doesnt exist.
     */
    public void bogoPermutations(){
        UI.printMessage(String.format("permuting %d items iteratively using Bogo Permutations", selectedList.size()));
        nextCol = 2;
        permutationCount = 0;

        List<String> items = new ArrayList<>(selectedList);
        Set<String> combinations = new HashSet<>();
        int count = factorial(items.size());

        //Main loop
        while(permutationCount < count)
        {
            String combination = String.join("", items);
            if(combinations.add(combination)) //Try adding the combo
            {
                permutationCount++;
                display(items);
            }
            Collections.shuffle(items); //Generate new combo
        }

        UI.printMessage(String.format("FINISHED: %d  items: %,d permutations",
                items.size(), permutationCount));
    }

    /**
     * Calculates the factorial of a number, used in boboPermutations
     * @param input
     * @return
     */
    public int factorial(int input)
    {
        int total = input;
        int num = input;
        while(num > 1)
        {
            --num;
            total *= num;
        }
        return total;
    }

    /**
     * Select an image to add to the list to permute
     */
    public void doMouse(String action, double x, double y){
        if (action.equals("released")){
            int idx = (int) ((y-DISPLAY_TOP)/DISPLAY_SEP);
            if (idx >=0 && idx < IMG_NAMES.size()){
                String img = IMG_NAMES.get(idx);
                if (!selectedList.contains(img)) {
                    selectedList.add(img);
                    nextCol = 1;
                    display(selectedList);
                }
            }
        }
    }            


    /**
     * Reset the selection and redisplay.
     * Clears all the current permutations
     * (but doesn't stop the current permutation search if
     * it is running).
     */
    public void reset(){
        selectedList.clear();
        UI.clearGraphics();
        UI.drawString("select images:", DISPLAY_LEFT, DISPLAY_TOP-5);
        nextCol = 0;
        display(IMG_NAMES);
        nextCol = 2;
        double x = DISPLAY_LEFT + DISPLAY_SEP - 1;
        double bot = DISPLAY_TOP+DISPLAY_SEP*IMG_NAMES.size();
        UI.drawLine(x, DISPLAY_TOP, x, bot); 
        UI.drawLine(x+DISPLAY_SEP, DISPLAY_TOP, x+DISPLAY_SEP, bot); 
    }

    /**
     * Clears the display of permutations and resets the nextCol back to 2.
     * (the current permutation search continues running).
     */
    public void clear(){
        UI.eraseRect(DISPLAY_LEFT + DISPLAY_SEP *2, 0, 1050, 1000);
        nextCol = 2;
    }

    /**
     * Display a list of images in the next column.
     * The list should have the names of the image files.
     * Uses the field nextCol to specify where the column is.
     * If there is room, it then moves to the next column, but if
     * it is already at the end of the window, it highlights the
     * column and draws over previous list (with a slight delay)
     */
    public void display (List<String> items ){
        double x = DISPLAY_LEFT + nextCol*DISPLAY_SEP;
        double y = DISPLAY_TOP;
        // highlight and offset the final column (animated column)
        if (x>=930){
            UI.sleep(1);
            x+=10;
            UI.setColor(LIGHT_YELLOW);
            UI.fillRect(x,y,IMAGE_SIZE, DISPLAY_SEP*items.size());
            UI.setColor(Color.black);
        }
        else {
            UI.sleep(10);
            nextCol++;
            UI.eraseRect(x,y,IMAGE_SIZE, DISPLAY_SEP*items.size());
        }
        // permutation count at the top
        if (nextCol > 2)
        {
            UI.eraseRect(x,y-20,60,20);
            if (permutationCount>100000000000000L){
                UI.drawString(permutationCount/1000000000000L+"T", x, y-3);
            }
            else if (permutationCount>100000000000L){
                UI.drawString(permutationCount/1000000000L+"B", x, y-3);
            }
            else if (permutationCount>100000000){
                UI.drawString(permutationCount/1000000+"M", x, y-3);
            }
            else if (permutationCount>100000){
                UI.drawString(permutationCount/1000+"k", x, y-3);
            }
            else {UI.drawString(permutationCount+"", x, y-3);}
        }
        // images in permutation
        for (String it : items){
            UI.drawImage(it, x, y, IMAGE_SIZE, IMAGE_SIZE);
            y+= DISPLAY_SEP;
        }
    }
    private static final Color LIGHT_YELLOW = new Color(255, 255, 180);

    // Main
    public static void main(String[] arguments) {
        new Permutations();
    }
}
