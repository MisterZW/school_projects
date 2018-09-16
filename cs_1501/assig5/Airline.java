
/*  CS 1501 Summer 2018 (enrolled in W section)   Assignment #5  */

import java.io.*;
import java.util.*;
import javax.swing.JOptionPane;

/**
* <code>Airline</code> is a route manager for a fictional airline service. 
* Its purpose it to practice the practical implementation of several
* graph-related algorithms from CS 1501 (Algorithm Implementation).
* <p>
* The <code>Airline</code> program builds its graph representation from a user-input
* .txt file and allows the user the option to change route information
* and make queries for graph information via a simple swing GUI interface.
* The <code>Airline</code> program directs the results of most queries to the standard
* output (console). It provides the option to save any changes in route
* information back to the source .txt file before exiting if desired.
* <p>
* See {@link #menu() menu()} for a complete list of functionality. 
* The <code>Airline</code> class's functionality can also be accessed directly 
* without the use of the GUI menu.
* <p>
* <b>Dependencies: </b> <a href="IndexMinPQ.html">IndexMinPQ by Sedgewick</a>
*
* @author Zachary Whitney  zdw9   ID: 3320178
* @version 1.0
* @since July 24, 2018
*/

public class Airline
{
	String filename;
	private boolean dataIsAltered = false;  //prompt user to save if network changes

	private int v;      			 //the # of vertices
	private int e = 0;  			 //the # of edges
	private List<Vertex> graph;		 //the adjacency list for the graph
	private List<Edge> edges;        //makes accessing edges simpler for saving -- not strictly necessary

	/**
	* Reads and builds the airline network from the user-specified file
	* <p>
	* A correctly formatted input file specifies the following, in this order, all on separate lines: <br>
	* 1) the number of vertices (cities) in the graph <br>
	* 2) a complete list of names for each vertex, one per line -- MUST CORRESPOND CORRECTLY to value from (1) <br>
	* 3) a list of edges (routes) in the graph. This should be <tt>[vertexA id#] [VertexB id#] [distance] [price]</tt> <br>
		        in the form:  <tt>[int] [whitespace] [int] [whitespace] [int] [whitespace] [float] </tt> <br>
	* <p>
	* <code>Airline</code> stores the network as an adjacency list graph representation.
	* @param filename the name of the .txt file which contains the network vertices and edges to build from
	* @throws FileNotFoundException if file specified by filename does not exist
	* @throws RuntimeException (various) -- if the file specified does not meet the correct format
	*/
	
	public Airline(String filename) throws FileNotFoundException
	{
		this.filename =  filename;
		Scanner s = new Scanner(new FileInputStream(filename));
		
		v = s.nextInt();
		graph = new ArrayList<Vertex>(v);
		edges = new ArrayList<Edge>();

		graph.add(new Vertex("", 0));

		for(int i = 1; i <= v; i++)
		{
			Vertex newCity = new Vertex(s.next(), i);
			graph.add(newCity);
		}

		e = 0;
		Edge newEdge;
		int first, second, distance;
		double price;
		Vertex a, b;

		while(s.hasNext())
		{
			first = s.nextInt();
			second = s.nextInt();
			distance = s.nextInt();
			price = s.nextDouble();
			newEdge = new Edge(graph.get(first), graph.get(second), distance, price);
			edges.add(newEdge);
			a = graph.get(first);
			b = graph.get(second);
			a.add(newEdge);
			b.add(newEdge);
			e++;
		}
		s.close();
	}

	/**
	* Runs the GUI for <code>Airline</code>
	* <p>
	* Uses JOptionpane prompts to get a valid filename from the user, builds an Airline object, 
	* then invokes its swing GUI menu to showcase its functionality.
	*
	* @param args the command line arguments -- these are not utilized
	*/

	public static void main(String[] args)
	{
		Airline airline = null;

		String input = "";

		do
		{
			try
			{
				input = JOptionPane.showInputDialog(null, "Welcome to Zach's Airline Manager\n\n" +
					"Input the name of a .txt file to see flight information\n");
				if(input == null)
					System.exit(0);
				airline = new Airline(input);
			}
			catch (FileNotFoundException fnf)
			{
				JOptionPane.showMessageDialog(null, 
					String.format("Could not find %s\n\nPlease enter a valid filepath", input), 
						"FILE OPEN ERROR", JOptionPane.ERROR_MESSAGE);
			}
			catch (Exception ex)
			{
				JOptionPane.showMessageDialog(null, 
					String.format("There was a problem reading %s\n\nPlease enter the name of a valid file", input), 
						"FILE READ ERROR", JOptionPane.ERROR_MESSAGE);
			}

		} while(airline == null);
		 
		airline.menu();
		JOptionPane.showMessageDialog(null, "Thanks for using Zach's Airline Manager! Bye now!");
	}

	/**
	* Provides a GUI menu-driven loop as a user interface which allows the user to display, alter, and query the network.
	* <p>
	* The options are: <br>
	* A) Display all routes and cities in the network <br>
	* B) Add a new route <br>
	* C) Delete an existing route <br>
	* D) Show the shortest route which connects all cities in the network (MST) <br>
	* E) Show the cheapest path from one city to another <br>
	* F) Show the shortest path from one city to another (by total miles) <br>
	* G) Show the shortest path from one city to another (by # of trips) <br>
	* H) Print all trips whose cost is less than or equal to a specified amount <br>
	* S) Save changes to the network <br>
	* Q) Quit
	* <p>
	* The user can press letters A-H, S, or Q to select items from the menu without using the mouse.
	* Hitting the "Cancel" button is interpreted the same way as quit, which will give the user a chance to save changes.
	* <p>
	* The menu is just a GUI layer and switchboard for the functionality in <code>Airline</code>. All of the functionality
	* can be invoked independently of the menu by providing the required parameters to the public methods.
	*/

	public void menu()
	{
		String menuLabel = "Zach's Airline Manager";
		String[] menu = { "[ -- select an option from the list -- ]",
					  "A) Display all routes and cities in the network\n",
					  "B) Add a new route\n",
					  "C) Delete an existing route\n",
					  "D) Show the shortest route which connects all cities in the network (MST)\n",
					  "E) Show the cheapest path from one city to another\n",
					  "F) Show the shortest path from one city to another (by total miles)\n",
					  "G) Show the shortest path from one city to another (by # of trips)\n",
					  "H) Print all trips whose cost is less than or equal to a specified amount\n",
					  "S) Save changes to the network\n",
					  "Q) Quit\n"};

		menu_loop:
		while(true)
		{
			String selected = (String) JOptionPane.showInputDialog(null, "What would you like to do?", menuLabel,
				 JOptionPane.DEFAULT_OPTION, null, menu, "");
			if(selected == null)  //account for user hitting "cancel" button
			{
				promptForSave();
				break menu_loop;
			}

			char choice = selected.charAt(0);

			switch(choice)
			{
				case 'A':
					displayGraph();
					break;
				case 'B':
					addRoute();
					break;
				case 'C':
					deleteRoute();
					break;
				case 'D':
					MST();
					break;
				case 'E':
					cheapestPath();
					break;
				case 'F':
					shortestPath();
					break;
				case 'G':
					shortestHops();
					break;
				case 'H':
					showPathsCheaperThanX();
					break;
				case 'S':
					save();
					break;
				case 'Q':
				    promptForSave();
					break menu_loop;
				default:
					noSelection();
			}
		}
	}

	/*
	* Displays an error window if the user does not override the default selection when hitting OK in the menu
	*/
	private void noSelection()
	{
		JOptionPane.showMessageDialog(null, "No option selected", "NO OPTION SELECTED", JOptionPane.ERROR_MESSAGE);
	}

	/*
	* Gives the user a chance to save network changes, if any, before exiting (calls save() to do this)
	* Only called if using the menu interface because this method uses JOptionPane
	*/
	private void promptForSave()
	{
		if(dataIsAltered)
		{
			int reply = JOptionPane.showConfirmDialog(null, "Would you like to save changes to the network?",
				"Exiting -- there are unsaved changes", JOptionPane.YES_NO_OPTION);
			if(reply == JOptionPane.YES_OPTION)
				save();
		}
	}

	/*
	* Saves network changes interactively as part of the GUI menu
	* 
	* Not a part of the public interface because this method utilizes swing windows
	* Saves the file back to its original filename
	*/
	private void save()
	{
		try
		{
			save(filename);
			JOptionPane.showMessageDialog(null, filename + " was saved successfully",
						"Operation Complete", JOptionPane.PLAIN_MESSAGE);
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(null, "There was a problem saving the data",
						"FILE SAVE ERROR", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	* Saves network changes to file destructively -- does NOT sort edges
	* 
	* @param filepath the destination filepath for the saved information
	* @throws FileNotFoundException if file doesn't exist and can't be created
	* @throws SecurityException if write permission to the file is denied
	*/

	public void save(String filepath) throws FileNotFoundException, SecurityException
	{

		PrintWriter pw = new PrintWriter(filepath);
		pw.print(v);
		for(Vertex v : graph)
			pw.println(v.city);
		for(Edge edge : edges)
		{
			pw.print(edge.v1.id);
			pw.print(" ");
			pw.print(edge.v2.id);
			pw.print(" ");
			pw.print(edge.distance);
			pw.print(" ");
			pw.print(edge.price);
			pw.println();
		}
		pw.close();
	}

	/**
	* Sends a textual representation of the network state to stdout.
	* <p>
	* This method first prints the number of cities and routes in the graph. <br>
	* Then, it iterates though each city and prints its available routes. <br>
	* Note that this means each route will be printed twice, once from each end city's point of view,
	* because all of the routes are bidirectional.
	*/
	public void displayGraph()
	{
		System.out.println("There are " + v + " cities in our network");
		System.out.println("There are " + e + " bidirectional routes available to fly\n");

		System.out.println("Here are the cities and routes available:");
		for(Vertex v : graph)
		{
			System.out.println(v);
			for(Edge edge : v.list)
			{
				if(v == edge.v1)
					System.out.println("\t" + edge);
				else
					System.out.println("\t" + edge.reverseString());
			}
		}
	}

	/*
	* Adds a new flight to the network
	* 
	* Shows interactive GUI output on the success/failure of the operation to the user
	*/

	private void addRoute()
	{
		Vertex source = getSource();
		if(source == null)
			return;  //cancelled by user
		Vertex dest = getDestination();
		if(dest == null)
			return;  //cancelled by user

		int d = getDistance();
		if(d < 0) return;       //request cancelled by user
		double p = getPrice();
		if(p < 0) return;       //request cancelled by user

		int result = addRoute(source.id, dest.id, d, p);
		if(result == 0)
		{
			String output = String.format("Added new route from %s to %s. Distance: %d. Price: $%.2f.",
			source.city, dest.city, d, p);

			JOptionPane.showMessageDialog(null, output, "SUCCESS", JOptionPane.PLAIN_MESSAGE);
		}
		else if(result == 4)
		{
			JOptionPane.showMessageDialog(null, "That would be a REAL short flight.",
				"ERROR", JOptionPane.ERROR_MESSAGE);
			return;
		}
		else if(result == 5)
		{
			JOptionPane.showMessageDialog(null, "That route already exists in the network.",
					"ERROR -- DUPLICATE ROUTE", JOptionPane.ERROR_MESSAGE);
			return;
		}
		else if(result == 6)
		{
			JOptionPane.showMessageDialog(null, "You need at least 2 cities in the network to add a route",
			"ERROR", JOptionPane.ERROR_MESSAGE);
			return;
		}
		else
		{
			//this should be impossible because other return values should be culled before calling public addRoute()
			JOptionPane.showMessageDialog(null, "Something went wrong. There is probably a bug in this code",
					"ERROR", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	* Adds a new two-way flight to the network
	* <p>
	* Note that the network will only support one route between two destinations.
	*
	* @param cityA the ID# of the first airport to connect
	* @param cityB the ID# of the second airport to connect
	* @param distance the flight distance, in miles
	* @param price the cost of a one-way ticket from A to B or from B to A
	* @return 0 if route was successfully added, 1 if invalid distance, 2 if invalid price,
	* 3 if invalid city selected, 4 if cityA and cityB are the same, 5 for duplicate route,
	* or 6 if there aren't at least 2 cities in the network. 
	*/

	public int addRoute(int cityA, int cityB, int distance, double price)
	{
		if(v < 2)
			return 6; //network too small for routes
		if(distance <= 0)
			return 1; //invalid distance
		if(price < 0)
			return 2; //invalid price
		if(cityA <= 0 || cityB <= 0 || cityA > v || cityB > v)
			return 3; //invalid city selected
		if(cityA == cityB)
			return 4; //same city twice

		Vertex source = graph.get(cityA);
		Vertex dest = graph.get(cityB);
		for(Edge edge : source.list)
			if(edge.other(source) == dest) 
				return 5; //duplicate route
		

		Edge newRoute = new Edge(source, dest, distance, price);
		source.add(newRoute);
		dest.add(newRoute);
		edges.add(newRoute);
		dataIsAltered = true;
		e++;
		return 0;
	}

	/*
	* Removes an existing flight from the network
	* 
	* Shows interactive GUI output on the success/failure of the operation to the user
	*/
	private void deleteRoute()
	{

		Vertex source = getSource();
		if(source == null)
			return;  //cancelled by user
		Vertex dest = getDestination();
		if(dest == null)
			return;  //cancelled by user

		int result = deleteRoute(source.id, dest.id);
		if(result == 1)
		{
			JOptionPane.showMessageDialog(null, "There are no edges to delete",
				"ERROR", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(result == 2)
		{
			JOptionPane.showMessageDialog(null, "Please select two different airports.",
				"ERROR", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(result == 3)
		{
			JOptionPane.showMessageDialog(null, "Please select valid airports.",
				"ERROR", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(result == 4)
		{
			JOptionPane.showMessageDialog(null, String.format("There is no route from %s to %s to delete.", source.city, dest.city),
				"ERROR", JOptionPane.ERROR_MESSAGE);
			return;
		}
		JOptionPane.showMessageDialog(null, String.format("Removed route from %s to %s.",
				source.city, dest.city));

	}

	/**
	* Removes the route connecting the source city from the destination city from the network, if it exists
	*
	* @param source the source city's ID #
	* @param destination the destination city's ID #
	* @return 0 if route exists and is successfully removed, 1 if the graph is invalid, 
	* 2 if the source or destination are the same, and 3 if the source or destination
	* is invalid, and 4 if the route does not exist and therefore could not be removed
	*/
	public int deleteRoute(int source, int destination)
	{
		if(e < 1)			
			return 1;
		if(source == destination)
			return 2;
		if(source <= 0 || source <= 0 || destination > v || destination > v)
			return 3;

		Vertex s = graph.get(source);
		Vertex d = graph.get(destination);

		Edge target = s.locateEdge(destination);

		if(target == null)
			return 4;
		
		s.list.remove(target);
		d.list.remove(target);
		edges.remove(target);
		dataIsAltered = true;
		e--;
		return 0;
	}

	/*GUI method captures a source and destination with JOptionPane, then calls public shortestHops()*/
	private void shortestHops()
	{
		Vertex source = getSource();
		if(source == null)
			return;  //cancelled by user
		Vertex dest = getDestination();
		if(dest == null)
			return;  //cancelled by user

		int result = shortestHops(source.id, dest.id);
		if(result == 1)
		{
			JOptionPane.showMessageDialog(null, "There must be at least 2 cities in the network to use this feature",
				"ERROR", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(result == 2)
		{
			JOptionPane.showMessageDialog(null, "Please select two different airports.",
				"ERROR", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(result == 3)
		{
			JOptionPane.showMessageDialog(null, "Please select valid airports.",
				"ERROR", JOptionPane.ERROR_MESSAGE);
			return;
		}

	}

	/**
	* Finds and displays the shortest route (by the number of trips) from the source to the destination
	* <p>
	* The route is calculated using breadth-first search. BFS has an asymptotic performance of
	* Θ(v + e) on the underlying adjacency list, where v is the number of cities in the network
	* and e is the number of routes in the network.
	* @param source the source city's ID #
	* @param destination the destination city's ID #
	* @return 0 if algorithm runs without error (may or may not find a valid solution), 1 if the graph
	* is invalid, 2 if the source or destination are the same, or 3 if the source or destination
	* is invalid.
	*/
	public int shortestHops(int source, int destination)
	{
		if(v < 2)
			return 1; //invalid graph
		if(source == destination) 
			return 2; //invalid city selected
		if(source <= 0 || source <= 0 || destination > v || destination > v)
			return 3;

		Vertex s = graph.get(source);
		Vertex d = graph.get(destination);

		Queue<Vertex> q = new LinkedList<Vertex>();
		Vertex[] parent = new Vertex[v+1];
		q.offer(d);
		d.marked = true;
		//parent[destination] = graph.get(0);

		Vertex current = null;
		boolean success = false;

		while(q.peek() != null)
		{
			current = q.poll();
			for (Edge edge : current.list)
			{
				Vertex v = edge.other(current);
				if(!v.marked)
				{
					v.marked = true;
					q.offer(v);
					parent[v.id] = current;
				}
			}
			if(current == s)
			{
				success = true;
				break;
			}
		}

		if(success)
		{
			StringBuilder sb = new StringBuilder();
			int hops = 0;
			while(true)
			{
				sb.append(current.city);
				sb.append(' ');
				if(current.id == destination)
					break;
				current = parent[current.id];
				hops++;
			}
			System.out.println(String.format("\nThere is a %d hop route available\n\n%s\n", hops, sb));
		}
		else
			System.out.println("\n" + s.city + " and " + d.city + " are not connected in the network");
		unmarkAll();
		return 0;
	}

	/*GUI method gets the maximum valid trip cost from the user, then calls public showPathsCheaperThanX()*/
	private void showPathsCheaperThanX()
	{
		String input = "";
		double maxCost = -1.0;

		do
		{
			try
			{
				input = JOptionPane.showInputDialog(null, "Enter the maximum trip price");
				maxCost = Double.parseDouble(input);
			}
			catch (NullPointerException n)  //user selected "cancel"
			{
				return;
			}
			catch (Exception e)
			{
				//keep asking
			}

		} while(maxCost < 0);

		showPathsCheaperThanX(maxCost);
	}

	/**
	* Finds and displays all routes costing less than or equal to the given price
	* <p>
	* This will print each valid route twice -- once from each end city's perspective. <br>
	* The routes are calculated using depth-first search. DFS has an asymptotic performance of
	* Θ(v + e) on the underlying adjacency list, where v is the number of cities in the network
	* and e is the number of routes in the network.
	* <p>
	* Outputs an empty list if there are no valid trips priced less than or equal to maxCost, 
	* otherwise prints the valid trips one per line.
	* @param maxCost the maximum price for a valid route
	*/
	public void showPathsCheaperThanX(double maxCost)
	{
		System.out.println(String.format("\nHere are the trips costing $%.2f or less:\n", maxCost));
		for(Vertex v : graph)
		{
			v.marked = true;
			for(Edge e : v.list)
			{
				if(e.price <= maxCost)
					dfs(e.other(v), maxCost, e.price, e.price, v.city);
			}
		}
		unmarkAll();
	}

	/*depth-first seach prints valid paths (cheaper than max) as they are determined*/
	private void dfs(Vertex current, double max, double total, double ticketPriceHere, String output)
	{
		current.marked = true;
		output = String.format("%s $%.2f %s", output, ticketPriceHere, current.city);

		System.out.println(String.format("TRIP PRICE: $%.2f -- %s", total, output));

		for(Edge e : current.list)
		{
			Vertex neighbor = e.other(current);
			double newCost = (total + e.price);

			if(!neighbor.marked && newCost <= max)
				dfs(neighbor, max, newCost, e.price, output);
		}

		current.marked = false;
	}


	/**
	* Calculates the minimum spanning tree for the network by distance in miles 
	* and displays it on the console.
	* <p>
	* Uses the Lazy Prim algorithm to determine the spanning tree. This runs in 
	* Θ(elg(e)) time, where e is the number of routes in the network.
	* <p>
	* If the network is not entirely connected, prints each connected component of
	* the graph independently and labels them with integer ordinals for clarity.
	*/
	public void MST()
	{
		ArrayList<ArrayList<Edge>> mst = new ArrayList<ArrayList<Edge>>();

		for(int i = 1; i <= v; i++)
		{
			Vertex current = graph.get(i);
			if(!current.marked)
				mst.add(lazyPrim(current));
		}

		displayMST(mst);
		unmarkAll();
	}


	/*simple helper method displays results of lazy prim's MST to the console*/
	private void displayMST(ArrayList<ArrayList<Edge>> mst)
	{
		if(mst.size() > 1)
		{
			System.out.println("\nThe network is not entirely connected -- printing subcomponents:\n");
			for(int i = 0; i < mst.size(); i++)
			{
				ArrayList<Edge> cc = mst.get(i);
				System.out.println("\nThis is connected component #" + (i+1) + "\n");
				for(Edge edge : cc)
					System.out.println(String.format("%s -- %s (%d miles)", edge.v1.city, edge.v2.city, edge.distance));
			}
		}
		else
		{
			System.out.println("\nTHE MST IS:\n");
			for(Edge edge : mst.get(0))
				System.out.println(String.format("%s -- %s (%d miles)", edge.v1.city, edge.v2.city, edge.distance));
		}

	}

	/*Lazy Prim uses a Priority Queue to find the MST in the graph and returns it as an ArrayList<Edge>*/
	private ArrayList<Edge> lazyPrim(Vertex root)
	{
		PriorityQueue<Edge> pq = new PriorityQueue<Edge>(e);
		ArrayList<Edge> mst = new ArrayList<Edge>(v-1);

		root.marked = true;
		for(Edge edge : root.list)
			pq.offer(edge);

		while(pq.size() > 0)
		{
			Edge nextEdge = pq.poll();
			if(nextEdge.v1.marked && nextEdge.v2.marked) continue;  //lazy trim cycles
			mst.add(nextEdge);
			Vertex dest = nextEdge.v1.marked ? nextEdge.v2 : nextEdge.v1;
			dest.marked = true;
			for(Edge edge : dest.list)  //enqueue neighbors
				pq.offer(edge);
		}

		return mst;
	}

	/*GUI method gets a source and destination from user*/
	/*Then calls the public interface's shortestPath() to find shortest route by distance*/
	private void shortestPath()
	{
		Vertex source = getSource();
		if(source == null)
			return;  //cancelled by user
		Vertex dest = getDestination();
		if(dest == null)
			return;  //cancelled by user
		
		int result = shortestPath(source.id, dest.id);
		if(result == 1)
			JOptionPane.showMessageDialog(null, "There must be at least 2 cities in the network to use this feature",
			"ERROR", JOptionPane.ERROR_MESSAGE);
		if(result == 2)
			JOptionPane.showMessageDialog(null, "The source and destination must be valid and distinct",
			"ERROR", JOptionPane.ERROR_MESSAGE);
	}

	/**
	* Prints the shortest path (by distance in miles) from the source to the destination to the console.
	* <p>
	* If there is no such path, displays a message stating this. <br>
	* If the parameters are invalid, returns an error sentinel. <br>
	* <p>
	* This algorithm utilizes Dijkstra's shortest path approach. 
	* This runs in Θ(elgv) time, where e is the # of routes and v is the # of cities in the network.
	*
	* @param source the city of departure
	* @param destination the destination city
	* @return 0 on success, 1 if failed due to invalid graph, 2 if failed due to invalid parameters
	*/

	public int shortestPath(int source, int destination)
	{
		if(v < 2)
			return 1; //invalid graph
		if(source == destination || source <= 0 || source <= 0 || destination > v || destination > v)
			return 2; //invalid city selected
		int length = 0;

		Vertex s = graph.get(source);
		Vertex d = graph.get(destination);

		ArrayList<Edge> path = distanceDijkstra(s, d);
		if(path == null)
			System.out.println("\nThere is no available route between those two airports\n");
		else
		{
			Vertex curr = s;
			System.out.println(String.format("\nHere is the shortest path (in miles) between %s and %s:\n", s.city, d.city));
			for(Edge edge : path)
			{
				System.out.println(String.format("%s to %s\t%d miles", curr.city, edge.other(curr).city, edge.distance));
				curr = edge.other(curr);
				length += edge.distance;
			}
			System.out.println("\nThe total distance is " + length + " miles\n");
		}
		return 0;
	}

	/*GUI method gets a source and destination from user*/
	/*Then calls the public interface's cheapestPath()*/
	private void cheapestPath()
	{
		Vertex source = getSource();
		if(source == null)
			return;  //cancelled by user
		Vertex dest = getDestination();
		if(dest == null)
			return;  //cancelled by user
		
		int result = cheapestPath(source.id, dest.id);
		if(result == 1)
			JOptionPane.showMessageDialog(null, "There must be at least 2 cities in the network to use this feature",
			"ERROR", JOptionPane.ERROR_MESSAGE);
		if(result == 2)
			JOptionPane.showMessageDialog(null, "The source and destination must be valid and distinct",
			"ERROR", JOptionPane.ERROR_MESSAGE);
	}

	/**
	* Prints the cheapest path from the source to the destination to the console.
	* <p>
	* If there is no such path, displays a message stating this. <br>
	* If the parameters are invalid, returns an error sentinel. <br>
	* <p>
	* This algorithm utilizes Dijkstra's shortest path approach. 
	* This runs in Θ(elgv) time, where e is the # of routes and v is the # of cities in the network.
	*
	* @param source the city of departure
	* @param destination the destination city
	* @return 0 on success, 1 if failed due to invalid graph, 2 if failed due to invalid parameters
	*/

	public int cheapestPath(int source, int destination)
	{
		if(v < 2)
			return 1; //invalid graph
		if(source == destination || source <= 0 || source <= 0 || destination > v || destination > v)
			return 2; //invalid city selected
		int length = 0;

		Vertex s = graph.get(source);
		Vertex d = graph.get(destination);
		double cost = 0.0;
		
		ArrayList<Edge> path = priceDijkstra(s, d);
		if(path == null)
			System.out.println("\nThere is no available route between those two airports\n");
		else
		{
			Vertex curr = s;
			System.out.println(String.format("\nHere is the cheapest path between %s and %s:\n", s.city, d.city));
			for(Edge edge : path)
			{
				System.out.println(String.format("%s to %s\t$%.2f ", curr.city, edge.other(curr).city, edge.price));
				curr = edge.other(curr);
				cost += edge.price;
			}
			System.out.println(String.format("\nThe total cost is $%.2f\n", cost));
		}
		return 0;
	}

	/*
	* Performs Dijskstra's algorithm to find the shortest path from source to destination based on distance
	* Returns the path as an ArrayList<Edge> if it exists, returns otherwise null
	*/
	private ArrayList<Edge> distanceDijkstra(Vertex source, Vertex destination)
	{
		IndexMinPQ<Integer> pq = new IndexMinPQ<Integer>(v);
		int[] dist = new int[v+1];
		Edge[] parent = new Edge[v+1];
		Arrays.fill(dist, Integer.MAX_VALUE);

		//start at the destination so that result is in correct order
		dist[destination.id] = 0;
		pq.insert(destination.id, dist[destination.id]);

		Vertex curr;

		while(!pq.isEmpty())
		{
			int val = pq.delMin();
			curr = graph.get(val);
			for(Edge edge : curr.list)
				process(edge, curr, dist, pq, parent);
		}

		ArrayList<Edge> result = null;

		if(dist[source.id] < Integer.MAX_VALUE)
		{
			result = new ArrayList<Edge>();
			curr = source;
			while(parent[curr.id] != null)
			{
				result.add(parent[curr.id]);
				curr = parent[curr.id].other(curr);
			}
		}

		return result;
	}

	/*Helper method for distanceDijsktra() which updates the path and the PQ when considering new edges*/
	private void process(Edge thisEdge, Vertex source, int[] dist, IndexMinPQ<Integer> pq, Edge[] parent)
	{
		Vertex dest = thisEdge.other(source);
		
		if(dist[dest.id] > dist[source.id] + thisEdge.distance)
		{
			dist[dest.id] = dist[source.id] + thisEdge.distance;
			parent[dest.id] = thisEdge;
			if(pq.contains(dest.id))
				pq.change(dest.id, dist[dest.id]);
			else
				pq.insert(dest.id, dist[dest.id]);
		}
		
	}

	/*
	* Performs Dijskstra's algorithm to find the shortest path from source to destination based on price
	* Returns the path as an ArrayList<Edge> if it exists, returns otherwise null
	*/
	private ArrayList<Edge> priceDijkstra(Vertex source, Vertex destination)
	{
		IndexMinPQ<Double> pq = new IndexMinPQ<Double>(v);
		double[] price = new double[v+1];
		Edge[] parent = new Edge[v+1];
		Arrays.fill(price, Double.POSITIVE_INFINITY);

		/*start at the destination so that result is in correct order*/
		price[destination.id] = 0.0;
		pq.insert(destination.id, price[destination.id]);

		Vertex curr;

		while(!pq.isEmpty())
		{
			int val = pq.delMin();
			curr = graph.get(val);
			for(Edge edge : curr.list)
				process(edge, curr, price, pq, parent);
		}

		ArrayList<Edge> result = null;

		if(price[source.id] < Double.POSITIVE_INFINITY)
		{
			result = new ArrayList<Edge>();
			curr = source;
			while(parent[curr.id] != null)
			{
				result.add(parent[curr.id]);
				curr = parent[curr.id].other(curr);
			}
		}

		return result;
	}

	/*Helper method for priceDijsktra() which updates the path and the PQ when considering new edges*/
	private void process(Edge thisEdge, Vertex source, double[] price, IndexMinPQ<Double> pq, Edge[] parent)
	{
		Vertex dest = thisEdge.other(source);
		
		if(price[dest.id] > price[source.id] + thisEdge.price)
		{
			price[dest.id] = price[source.id] + thisEdge.price;
			parent[dest.id] = thisEdge;
			if(pq.contains(dest.id))
				pq.change(dest.id, price[dest.id]);
			else
				pq.insert(dest.id, price[dest.id]);
		}
	}

	/*Helper GUI method gets a valid source city from the user using JOptionPane*/
	private Vertex getSource()
	{
		Vertex selected = null;
		do
		{
			selected = (Vertex) JOptionPane.showInputDialog(null, "Select a Source City", "Select a Source City",
				 JOptionPane.DEFAULT_OPTION, null, graph.toArray(), " ");
		} while(selected == graph.get(0));

		return selected;
	}

	/*Helper GUI method gets a valid destination city from the user using JOptionPane*/
	private Vertex getDestination()
	{
		Vertex selected = null;
		do
		{
			selected = (Vertex) JOptionPane.showInputDialog(null, "Select a Destination City", "Select a Destination City",
				 JOptionPane.DEFAULT_OPTION, null, graph.toArray(), " ");
		} while(selected == graph.get(0));

		return selected;
	}

	/*Helper GUI method gets a valid distance (as an int) from the user using JOptionPane*/
	private int getDistance()
	{
		String input = "";
		int distance = -1;
		do
		{
			try
			{
				input = JOptionPane.showInputDialog(null, "Enter the new route's distance");
				distance = Integer.parseInt(input);
			}
			catch (NullPointerException n)  //user selected "cancel"
			{
				return -1;
			}
			catch (Exception e)
			{
				//keep asking
			}

		} while(distance <= 0);
		return distance;
	}

	/*Helper GUI method gets a valid price (as a float) from the user using JOptionPane*/
	private double getPrice()
	{
		String input = "";
		double price = -1.0;

		do
		{
			try
			{
				input = JOptionPane.showInputDialog(null, "Enter the new route's price");
				price = Double.parseDouble(input);
			}
			catch (NullPointerException n)  //user selected "cancel"
			{
				return -1.0;
			}
			catch (Exception e)
			{
				//keep asking
			}

		} while(price < 0);
		return price;
	}

	/* 
	*  Helper method -- sets all vertices in the graph back to unmarked
	*  Called at the end of several algorithms to clean up 
	*/
	private void unmarkAll()
	{
		for(Vertex v : graph)
			v.marked = false;
	}

	private static class Vertex
	{
		private String city;
		private List<Edge> list;
		private int id;
		private boolean marked;

		public Vertex(String name, int ordinal)
		{
			list = new LinkedList<Edge>();
			city = name;
			id = ordinal;
			marked = false;
		}

		public void add(Edge edge)
		{
			list.add(edge);
		}

		public String toString()
		{
			return city.equals("") ? "" : id + ": " + city;
		}

		public Edge locateEdge(int other)
		{
			for(Edge e : list)
				if(e.other(id) == other)
					return e;
			return null;
		}

	}

	private static class Edge implements Comparable<Edge>
	{
		private Vertex v1;
		private Vertex v2;
		private int distance;
		private double price;

		public Edge(Vertex loc1, Vertex loc2, int dist, double cost)
		{
			v1 = loc1;
			v2 = loc2;
			distance = dist;
			price = cost;
		}

		public int other(int source)
		{
			return (source == v2.id) ? v1.id : v2.id;
		}

		public Vertex other(Vertex source)
		{
			return (source == v2) ? v1 : v2;
		}

		public String toString()
		{
			String result = String.format("%s to %s is %d miles. A one-way flight costs $%.2f." , v1.city, v2.city, distance, price);
			return result;
		}

		public String reverseString()
		{
			String result = String.format("%s to %s is %d miles. A one-way flight costs $%.2f." , v2.city, v1.city, distance, price);
			return result;
		}

		public int compareTo(Edge other)
		{
			return this.distance - other.distance;
		}
	}
}
