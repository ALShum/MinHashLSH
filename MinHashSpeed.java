import java.io.IOException;

/**
 * Compares the runtime for calculating approximate jaccard similarity using MinHash matrix and
 * calculating the exact jaccard similarity.  User must specify <folder> with collection of
 * documents, <number of permutations> for use with MinHash matrix.
 * 
 * @author Alex Shum
 */
public class MinHashSpeed {
	
	/**
	 * Calculates approximate jaccard similarity and exact jaccard similarity between all 
	 * pairs of documents.  Prints time it takes to calculate exact jaccard similarity and
	 * time it takes to calculate approximate jaccard similarity.
	 * 
	 * @param args folder and number of permutations
	 * @throws NumberFormatException If number of permutations not formatted correctly.
	 * @throws IOException If files cannot be opened.
	 */
	public static void main(String[] args) throws NumberFormatException, IOException {
		if(args.length != 2) throw new IllegalArgumentException("Enter <folder> <num permutations>");
		MinHash mh = new MinHash(args[0], Integer.parseInt(args[1]));
		String[] allDocs = mh.allDocs();
		
		long startTime;
		long endTime;
		double sec;
		startTime = System.currentTimeMillis();
		for(int i = 0; i < allDocs.length; i++) {
			for(int j = i + 1; j < allDocs.length; j++) {
				mh.exactJaccard(allDocs[i], allDocs[j]);
			}
		}
		endTime = System.currentTimeMillis() - startTime;
		sec = (double) endTime / 1000;
		System.out.println("Exact jaccard total time: " + endTime + " (ms)");
		System.out.println("Exact jaccard total time: " + sec + " seconds");
		System.out.println("------------------------------");
		
		startTime = System.currentTimeMillis();
		int[][] minHashMat = mh.minHashMatrix();
		for(int i = 0; i < minHashMat.length; i++) {
			for(int j = i + 1; j < minHashMat.length; j++) {
				mh.approximateJaccard(minHashMat[i], minHashMat[j]);
			}
		}
		endTime = System.currentTimeMillis() - startTime;
		sec = (double) endTime / 1000;
		System.out.println("Approx jaccard total time: " + endTime + " (ms)");
		System.out.println("Exact jaccard total time: " + sec + " seconds");
	}
}
