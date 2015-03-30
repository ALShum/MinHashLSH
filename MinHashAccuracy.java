import java.io.IOException;

/**
 * Compares the accuracy of using the MinHash matrix for jaccard similarity with
 * various number of permutations.  User must specify <folder> with collection of documents,
 * <number of permutations> for use with MinHash matrix, and the <error parameter>.  This will
 * print out the number of document pairs where the approximate jaccard similarity and exact
 * jaccard similarity differ by more than the <error parameter>.
 * 
 * @author Alex Shum
 */
public class MinHashAccuracy {
	
	/**
	 * Calculates the approximate jaccard similarity and exact jaccard similarity for all
	 * pairs of documents and then counts the number of times the difference between
	 * the exact and approximate jaccard similarities differs by more than the user 
	 * specified error parameter.  
	 * 
	 * @param args folder, number of permutations, error parameter.
	 * @throws NumberFormatException If number of permutations not formatted correctly or 
	 * 								 error paramter not formatted correctly.
	 * @throws IOException If files cannot be opened.
	 */
	public static void main(String[] args) throws NumberFormatException, IOException {
		if(args.length != 3) throw new IllegalArgumentException("Enter <folder> <num permutations> <error parameter>");

		MinHash mh = new MinHash(args[0], Integer.parseInt(args[1]));
		double eps = Double.parseDouble(args[2]);
		String[] files = mh.allDocs();
		int[][] minHashMat = mh.minHashMatrix();
		
		int count = 0;
		int comp = 0;
		double exact;
		double approx;	
		for(int i = 0; i < files.length; i++) {
			System.out.println(files[i]);
			for(int j = i + 1; j < files.length; j++) {
				exact = mh.exactJaccard(files[i], files[j]);
				approx = mh.approximateJaccard(minHashMat[i], minHashMat[j]);
				
				if(approx < exact && approx + eps < exact) {
					count++;
				} else if(approx > exact && approx - eps > exact) {
					count++;
				}
				comp++;
			}
		}
		System.out.println("Total number of comparisons: " + comp);
		System.out.println("Number of times exact and approximate jaccard differ more than epsilon: " + count);
	}
}
