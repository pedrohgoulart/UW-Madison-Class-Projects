import java.util.ArrayList;
import java.util.List;

public class test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		List<Location> vertices = new ArrayList<Location>();
		List<GraphNode<Location,Path>> graphnodes = new ArrayList<GraphNode<Location,Path>>();
		Location l1 = new Location("bj");
		Location l2 = new Location("sh");
		Location l3 = new Location("js");
		String[] pn = new String[2];
		pn[1] = "dist";
		pn[0] = "cost";
		NavigationGraph ng = new NavigationGraph(pn);
		ng.addVertex(l1);
		ng.addVertex(l3);
		ng.addVertex(l2);
		List<Double> pp = new ArrayList<Double>();
		pp.add(1.2);pp.add(3.2);
		Path edge = new Path(l1,l3,pp);
		Path e2 = new Path(l3,l2,pp);
		Path e3 = new Path(l1,l2,pp);
		
		ng.addEdge(l1, l3, edge);
		ng.addEdge(l1, l2, e3);
		ng.addEdge(l3, l2, e2);
		List<Location> pq = new ArrayList<Location>();
		pq = vertices;
		List<Location> lcs = ng.getNeighbors(l1);
		System.out.println(lcs);
		for(Location lc : lcs){
			System.out.println(pq.indexOf(lc));
		}
		
		
		
		
		
	}

}
