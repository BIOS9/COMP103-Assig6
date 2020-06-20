// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2018T2, Assignment 6
 * Name: Matthew Corfiatis
 * Username: CorfiaMatt
 * ID: 300447277
 */

import ecs100.*;
import java.util.*;
import java.awt.Color;



public class Maze {

    public static final double MAZE_LEFT = 10;
    public static final double MAZE_TOP = 10;
    public static final double CELL_SIZE = 20;

    private List<String> directions = Arrays.asList("NORTH", "SOUTH", "EAST", "WEST");

    private static final Random RANDOM = new Random();

    private int size;
    private Cell[][] cells;
    private Cell goal;

    /**
     * Getter to get the maze size
     * @return Integer value indicating the width and height of the maze.
     */
    public int getSize() {
        return size;
    }

    /**
     * Make a maze of the specified size
     */
    public Maze(int sz) {
        size = Math.max(4, Math.min(37, sz));  // ensure size is between 4 and 37

        this.cells = new Cell[size][size];
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                cells[row][col] = new Cell(row, col);
            }
        }
        goal = cells[size*2/3][size*2/3];
        generateGraph();
    }

    /**
     * Unvisits all cells
     */
    public void reset(){
        for (int row=0; row<size; row++){
            for (int col=0; col<size; col++){
                cells[row][col].unvisit();
            }
        }
    }

    /**
     * Return the goal cell of the maze
     */
    public Cell getGoal() {
        return goal;
    }

    /**
     * Return the cell at position (x,y).
     * if (x,y) is off the maze, returns cells[0][0]
     */
    public Cell getCellAt(double x, double y){
        int row = (int)((y-MAZE_TOP)/CELL_SIZE);
        int col = (int)((x-MAZE_LEFT)/CELL_SIZE);
        if (row>=0 && row<size && col>=0 && col<size){
            return cells[row][col];
        }
        else {
            return cells[0][0];
        }
    }

    /**
     * Draw the maze.
     */
    public void draw() {
        UI.clearGraphics();

        UI.setColor(Color.BLACK);
        UI.fillRect(MAZE_LEFT-1, MAZE_TOP-1, size * CELL_SIZE + 2, size * CELL_SIZE + 2);

        for (int row=0; row<size; row++){
            for (int col=0; col<size; col++){
                cells[row][col].draw(Color.white);
            }
        }

        goal.draw(Color.GREEN);
    }


    // ---------GENERATING THE PATHS------------------------------
    /**
     * Generates graph of paths starting from [0,0]
     * Then removes all the visited markers
     */
    private void generateGraph() {
        extendPathsFrom(cells[0][0]);
        reset();
    }

    /**
     * Does a random recursive depth first search of the maze,
     * Visits each cell the first time it gets to a cell
     * For each possible neighbouring cell that hasn't already been visited,
     *  it adds the cell to the neighbours of this cell (and vice versa)
     *  and extends the paths from those neighbours.
     * It also links to approximately 3% of the visited neighbouring cells.
     * 
     */
    
    private void extendPathsFrom(Cell cell){
        cell.visit();
        ArrayList<String> dirs = new ArrayList<String>(directions);
        Collections.shuffle(dirs);
        for (String dir : dirs){
            Cell next = getNextCell(cell, dir);
            if ( next != null){
                if (!next.isVisited()){
                    cell.addNeighbour(next);
                    next.addNeighbour(cell);
                    extendPathsFrom(next);
                }
                else if (Math.random() < 0.03) {
                    cell.addNeighbour(next);
                    next.addNeighbour(cell);
                }
            }
        }
    }

    /**
     * Return the next cell over in the given direction.
     * If the next cell would be over the edge of the maze, then returns null
     */
    private Cell getNextCell(Cell cell, String direction) {
        int row = cell.getRow();
        int col = cell.getCol();

        if (direction=="NORTH" && row>0)           { return cells[row-1][col]; }
        else if (direction=="SOUTH" && row<size-1) { return cells[row+1][col]; }
        else if (direction=="WEST"  && col>0)      { return cells[row][col-1]; }
        else if (direction=="EAST"  && col<size-1) { return cells[row][col+1]; }
        else                                       { return null; }
    }

    public static void main(String[] args){
        new MazeSearch();
    }


}
