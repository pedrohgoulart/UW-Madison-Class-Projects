import java.util.List;
import java.util.ArrayList;

public class NavigationGraph implements GraphADT<Location, Path> {

	//TODO: Implement all methods of GraphADT
	private List<Location> vertices;
	private List<GraphNode<Location,Path>> graphnodes;
	private String[] properties;
	
	public NavigationGraph(String[] edgePropertyNames) {
		this.vertices = new ArrayList<Location>();
		this.graphnodes = new ArrayList<GraphNode<Location,Path>>();
		this.properties = edgePropertyNames;
	}

	/**
	 * Adds a vertex to the Graph
	 * 
	 * @param vertex
	 *            vertex to be added
	 */
	public void addVertex(Location vertex){
		if(vertices.isEmpty()){
			vertices.add(vertex);
		}
		if(!vertices.contains(getLocationByName(vertex.getName()))){
			vertices.add(vertex);
		}
	}
	
	public int getindex(Location vertex){
		int index = vertices.size();
		for(int i = 0; i < vertices.size(); i++){
			if(vertices.get(i).getName().equals(vertex.getName())){
				index = i;
				break;
			}
		}
		return index;
	}

	/**
	 * Creates a directed edge from src to dest
	 * 
	 * @param src
	 *            source vertex from where the edge is outgoing
	 * @param dest
	 *            destination vertex where the edge is incoming
	 * @param edge
	 *            edge between src and dest
	 */
	public void addEdge(Location src, Location dest, Path edge){
		int index = getindex(src);
		if(graphnodes.isEmpty() || graphnodes.size() <= index){
			GraphNode<Location,Path> gnode = new 
					             GraphNode<Location,Path>(src,index);
			gnode.addOutEdge(edge);
			graphnodes.add(gnode);
		}else{
			graphnodes.get(index).addOutEdge(edge);	
		}
	}

	/**
	 * Getter method for the vertices
	 * 
	 * @return List of vertices of type V
	 */
	public List<Location> getVertices(){
		return vertices;
	}

	/**
	 * Returns edge if there is one from src to dest vertex else null
	 * 
	 * @param src
	 *            Source vertex
	 * @param dest
	 *            Destination vertex
	 * @return Edge of type E from src to dest
	 */
	public Path getEdgeIfExists(Location src, Location dest){
		int index = getindex(src);
		if(index == vertices.size()){
			return null;
		}
		List<Path> paths = graphnodes.get(index).getOutEdges();
		for(Path path : paths){
			if(path.getDestination().equals(dest)){
				return path;
			}
		}
		return null;
	}

	/**
	 * Returns the outgoing edges from a vertex
	 * 
	 * @param src
	 *            Source vertex for which the outgoing edges need to be obtained
	 * @return List of edges of type E
	 */
	public List<Path> getOutEdges(Location src){
		int index = getindex(src);
		return graphnodes.get(index).getOutEdges();
	}

	/**
	 * Returns neighbors of a vertex
	 * 
	 * @param vertex
	 *            vertex for which the neighbors are required
	 * @return List of vertices(neighbors) of type V
	 */
	public List<Location> getNeighbors(Location vertex){
		List<Location> neighbors = new ArrayList<Location>();
		int index = getindex(vertex);
		List<Path> paths = graphnodes.get(index).getOutEdges();
		for(Path path : paths){
			neighbors.add(path.getDestination());
		}
		return neighbors;
	}

	/**
	 * Calculate the shortest route from src to dest vertex using
	 * edgePropertyName
	 * 
	 * @param src
	 *            Source vertex from which the shortest route is desired
	 * @param dest
	 *            Destination vertex to which the shortest route is desired
	 * @param edgePropertyName
	 *            edge property by which shortest route has to be calculated
	 * @return List of edges that denote the shortest route by edgePropertyName
	 */
	public List<Path> getShortestRoute(Location src, Location dest, String edgePropertyName) {
		int propertyindex = 0;
		for (int i = 0; i < properties.length; i++) {
			if (properties[i].equals(edgePropertyName)) {
				propertyindex = i;
				break;
			}
		}
		List<Boolean> visited = new ArrayList<Boolean>();
		List<Double> weight = new ArrayList<Double>();
		List<Location> pred = new ArrayList<Location>();
		int size = vertices.size();
		Location initialpred = new Location(" ");
		for (int i = 0; i < size; i++) {
			visited.add(false);
			weight.add(Double.MAX_VALUE);
			pred.add(initialpred);
		}
		int index = getindex(src);
		weight.set(index, 0.0);

		List<Location> pq = new ArrayList<Location>();
		pq.add(src);
		while (!pq.isEmpty()) {
			int minindex = 0;
			for(int i = 1; i < pq.size(); i++){
				if(weight.get(getindex(pq.get(minindex))) > 
				              weight.get(getindex(pq.get(i))))
					minindex = i;
			}
			Location C = pq.remove(minindex);
			int indexofC = getindex(C);
			visited.set(indexofC, true);
			List<Location> neighbours = getNeighbors(C);
			for (Location S : neighbours) {
				int indexofS = getindex(S);
				if (visited.get(indexofS)) {
					continue;
				}
				double totalweight = weight.get(indexofC) + getEdgeIfExists(C, S).getProperties().get(propertyindex);
				if (totalweight < weight.get(indexofS)) {
					weight.set(indexofS, totalweight);
					pred.set(indexofS, C);
					pq.add(S);
				}
			}

		}

		List<Path> shortest = new ArrayList<Path>();
		Location location = dest;
		Location predecessor = pred.get(getindex(location));
		while (predecessor.getName() != " ") {
			Path route = getEdgeIfExists(predecessor, location);
			shortest.add(route);
			location = predecessor;
			predecessor = pred.get(getindex(location));
		}
		List<Path> route = new ArrayList<Path>();
		for (int i = 0; i < shortest.size(); i++) {
			route.add(shortest.get(shortest.size() - 1 - i));
		}
		return route;
	}

	/**
	 * Getter method for edge property names
	 * 
	 * @return array of String that denotes the edge property names
	 */
	public String[] getEdgePropertyNames(){
		return properties;
	}

	/**
	 * Return a string representation of the graph
	 * 
	 * @return String representation of the graph
	 */
	public String toString(){
		String graph = "source" + " destination";
		for(String property : properties){
			graph += " " + property;
		}
		graph += "\n";
		for(Location vertex : vertices){
			int index = getindex(vertex);
			List<Path> paths = graphnodes.get(index).getOutEdges();
			for(Path path : paths){
				graph += " " + path.toString() + "\n";
			}
		}
		return graph;
	}
	
	/**
	 * Returns a Location object given its name
	 * 
	 * @param name
	 *            name of the location
	 * @return Location object
	 */
	public Location getLocationByName(String name) {
		for(Location vertex : vertices){
			if(vertex.getName().equals(name)){
				return vertex;
			}
		}
		return null; //TODO: implement correctly. 
	}

}
