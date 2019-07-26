import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class IntervalTreeEndToEndTest {
	/**
	 * Run as a stand-alone application.
	 * 
	 * @param args
	 *            First argument is input file and second argument is the output file. It reads commands
	 *            from the in.txt and outputs results in out.txt
	 */
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Usage: IntervalTreeEndToEndTest <in.txt> <out.txt>");
			System.exit(0);
		}

		IntervalTreeEndToEndTest runner = new IntervalTreeEndToEndTest();
		String inFile = String.valueOf(args[0]);
		String outFile = String.valueOf(args[1]);
		
		try {
			runner.interactiveMode(inFile, outFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Runs the program using command code from in.txt file.
	 * 
	 * <pre>
	 * 1. Insert Schedule
	 * 2. Delete Schedule
	 * 3. Find Overlapping Schedules  // not using in ete tests
	 * 4. Schedules Containing Point
	 * 5. Print Tree Stats
	 * 6. Show Tree Viewer // not using in ete test
	 * 7. Quit Program
	 * Enter Choice:
	 * </pre>
	 * @throws IOException 
	 */
	public void interactiveMode(String inFile, String outFile) throws IOException {
		IntervalTreeADT<Integer> tree = new IntervalTree<Integer>();
		Scanner scanner = new Scanner(new File(inFile));

		// creates a FileWriter Object
		FileWriter writer = new FileWriter(new File(outFile));
		while (true) {
			int choice = scanner.nextInt();

			switch (choice) {
			case 1:
				// get the label and left, right points
				String inputData = scanner.next();
				String[] splits = inputData.split(",");
				String label = splits[0];
				int left = Integer.valueOf(splits[1]);
				int right = Integer.valueOf(splits[2]);
				IntervalADT<Integer> interval = new Interval<Integer>(left, right, label);
				tree.insert(interval);
				break;

			case 2:
				inputData = scanner.next();
				splits = inputData.split(",");
				label = splits[0];
				left = Integer.valueOf(splits[1]);
				right = Integer.valueOf(splits[2]);

				interval = new Interval<Integer>(left, right, label);
				try {
					tree.delete(interval);
				} catch (IntervalNotFoundException e) {
					System.exit(1);
				}
				break;

			case 3:
				break;

			case 4:
				// schedule containing point
				int point = scanner.nextInt();
				List<IntervalADT<Integer>> result = tree.searchPoint(point);
				// Below ASSUMPTION IS: COMPARETO IS IMPLEMENTED CORRECTLY BY STUDENTS
				result.sort(new Comparator<IntervalADT<Integer>> () {
					public int compare(IntervalADT<Integer> o1, IntervalADT<Integer> o2) {
						return o1.compareTo(o2);
					}
				});
				writer.write("Following schedules contain the input point " + point + ":\n");

				for (IntervalADT<Integer> r : result) {
					writer.write(r.toString() + " ");
				}
				writer.write("\n");
				writer.flush();
				break;

			case 5:
				writer.write("-----------------------------------------\n");
				writer.write("Height: " + tree.getHeight() + "\n");
				writer.write("Size: " + tree.getSize() + "\n");
				writer.write("-----------------------------------------\n");
				writer.write("=========================================\n");				
				writer.write("Inorder traversal of the tree: \n");
				writer.write("=========================================\n");				
				printInorderTraversal(tree.getRoot(), writer);
				writer.flush();
				break;
			case 6:
				break;

			case 7:
				writer.write("Good Bye!\n");
				writer.close();
				System.exit(0);

			}
		}
	}
	
	void printInorderTraversal(IntervalNode node, FileWriter writer) throws IOException {
		if (node != null) {
			printInorderTraversal(node.getLeftNode(), writer);
			writer.write(node.getInterval().toString() + " ");
			printInorderTraversal(node.getRightNode(), writer);
		}
	}
	
	/**
	 * Get overlapping intervals.
	 * 
	 * @param tree
	 *            the tree node.
	 * @param interval
	 *            the target interval.
	 * @return list of candidate intervals.
	 */
	public List<IntervalADT<Integer>> getOverlapping(IntervalTreeADT<Integer> tree, IntervalADT<Integer> interval) {
		List<IntervalADT<Integer>> result = tree.findOverlapping(interval);
		return result;
	}
}

