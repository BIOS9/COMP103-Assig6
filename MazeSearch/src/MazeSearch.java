// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2018T2, Assignment 6
 * Name: Matthew Corfiatis
 * Username: CorfiaMatt
 * ID: 300447277
 */

import ecs100.UI;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Search for a path to the goal in a maze.
 * The maze consists of a graph of Cells:
 *  Each cell has a collection of neighbouring cells.
 *  Each cell can be "visited" and it will remember that it has been visited
 *  A Cell is Iterable, so that you can iterate through its neighbour cells with
 *    for(Cell neigh : cell){....
 *
 * The maze has a goal cell (in the bottom right corner)
 * The user can click on a cell, and the program will search for a path
 * from that cell to the goal.
 * 
 * Every cell that is looked at during the search is coloured  yellow, and then,
 * if the cell turns out to be on a dead end, it is coloured red.
 */

public class MazeSearch {

    public static final int DELAY = 20;

    private Maze maze;
    private String search = "first";   // "first", "all", or "shortest"
    private int pathCount = 0;
    private boolean isRunning = false;
    private boolean isPaused = false;
    private boolean stop = false;

    public MazeSearch() {
        setupGui();
        makeMaze(10);
    }
        
    public void setupGui(){
        UI.addTextField("Maze Size", (String v)->{makeMaze(Integer.parseInt(v));});
        UI.setMouseListener(this::doMouse);
        UI.addButton("Regenerate",    ()->{makeMaze(maze.getSize());});
        UI.addButton("Reset",    ()->{if(!isRunning) { maze.reset(); maze.draw(); }});
        UI.addButton("First path",    ()->{search="first";});
        UI.addButton("All paths",     ()->{search="all";});
        UI.addButton("Shortest path", ()->{search="shortest";});
        UI.addButton("Stop",    ()->{if(!stop) stop = true;});
        UI.addButton("Quit", UI::quit);
    }

    /**
     * Creates a new maze and draws it .
     */
    public void makeMaze(int size){
        if(isRunning)
        {
            UI.println("You cannot do this while the search is running.");
            return;
        }
        maze = new Maze(size);
        maze.draw();
    }

    /**
     * Clicking the mouse on a cell should make the program
     * search for a path from the clicked cell to the goal.
     */
    public void doMouse(String action, double x, double y){
        if (action.equals("released")){
            if(isRunning) return;
            UI.clearText();
            maze.reset();
            maze.draw();
            isRunning = true;
            pathCount = 0;
            Cell start = maze.getCellAt(x, y);
            try {
                if (search == "first") {
                    exploreCell(start);
                } else if (search == "all") {
                    exploreCellAll(start);
                }
                if (search == "shortest") {
                    exploreCellShortest(start);
                }
            }
            catch (Error ex) //Catch exception thrown when user stops the search
            {
                stop = false;
                UI.println("Operation aborted by user.");
            }
            if(!isPaused)
                isRunning = false;
        }
    }

    /**
     * Search for a path from a cell to the goal.
     * Return true if we got to the goal via this cell (and don't
     *  search for any more paths).
     * Return false if there is not a path via this cell.
     * 
     * If the cell is the goal, then we have found a path - return true.
     * If the cell is already visited, then abandon this path - return false.
     * Otherwise,
     *  Mark the cell as visited, and colour it yellow (and sleep for a short time)
     *  Recursively try exploring from the cell's neighbouring cells, returning true
     *   if a neighbour leads to the goal
     *  If no neighbour leads to a goal,
     *    colour the cell red (to signal failure)
     *    abandon the path - return false.
     */
    public boolean exploreCell(Cell cell) {
        if(stop) throw new Error();
        cell.visit();
        if (cell == maze.getGoal()) {
            cell.draw(Color.blue);   // to indicate finding the goal
            return true;
        }

        cell.draw(Color.yellow);
        UI.sleep(DELAY);

        for(Cell neighbour : cell) //Get neighbouring cells
        {
            if(!neighbour.isVisited())
            {
                if(exploreCell(neighbour)) //Recursive search for neighbours
                    return true;
            }
        }
        cell.draw(Color.red);
        UI.sleep(DELAY);
        return false;
    }


    /** COMPLETION
     * Search for all paths from a cell,
     * If we reach the goal, then we have found a complete path,
     *  so pause for 1 second
     * Otherwise,
     *  visit the cell, and colour it yellow
     *  Recursively explore from the cell's neighbours, 
     *  unvisit the cell and colour it white.
     * 
     */
    public void exploreCellAll(Cell cell) {
        if(stop) throw new Error();
        if (cell == maze.getGoal()) {
            pathCount++;
            UI.printMessage("Found " + pathCount + " paths");

            //Flash goal cell
            cell.draw(Color.blue);
            UI.sleep(150);
            cell.draw(Color.green);
            UI.sleep(150);
            cell.draw(Color.blue);
            UI.sleep(150);
            cell.draw(Color.green);
            UI.sleep(150);
        }
        else {
            cell.visit();
            cell.draw(Color.yellow);
            UI.sleep(DELAY);

            for (Cell neighbour : cell) { //Loop over neighbouring cells
                if (!neighbour.isVisited())
                    exploreCellAll(neighbour); //Recursively find other paths
            }

            cell.draw(Color.white);
            UI.sleep(DELAY);
            cell.unvisit();
        }
    }

    
    /** CHALLENGE
     * Search for shortest path from a cell,
     * Use Breadth first search.
     *
     * Used Dijkstras algorithm AKA modified breadth first search
     * FYI I used some code from my own assignment 1 (Sokoban) submission.
     */
    public void exploreCellShortest(Cell start) {
        PriorityQueue<List<Cell>> dijkstraRoutes = new PriorityQueue<>(Comparator.comparing(List::size)); //Map of routes. Key is route cost/distance, value is the path.
        ArrayList<Cell> routeBegin = new ArrayList<>(); //Begin the first route with the starting position.
        routeBegin.add(start); //Add start pos
        dijkstraRoutes.add(routeBegin); //Add start route to route list

        while (dijkstraRoutes.size() > 0) //Main loop to calculate optimal path
        {
            List<Cell> route = dijkstraRoutes.poll(); //Get route with lowest distance/cost
            if(route == null) //Ensure route exists and the route list is not empty
                return; //No route to target

            Cell lastMove = route.get(route.size() - 1); //Get the last move in the route

            for(Cell neighbour : lastMove) //Loop to process each of the neighbours
            {
                //Check if finished
                if (neighbour == maze.getGoal()) {
                    //Colour route green and goal blue
                    for(Cell routeMove : route)
                        routeMove.draw(Color.green);
                    neighbour.draw(Color.blue);   // to indicate finding the goal
                    return;
                }

                if (route.contains(neighbour))
                    continue; //Skip cells that are already in the route

                List<Cell> routeCopy = new ArrayList<>(route); //Clone route to avoid mutability issues
                routeCopy.add(neighbour);
                dijkstraRoutes.add(routeCopy); //Add the route back to the queue
            }
        }
    }

    public static void main(String[] args) {
        new MazeSearch();
    }
}

