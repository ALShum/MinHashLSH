import java.io.IOException;
import java.util.List;

/**
 * Calculates the number of false positives that were hashed together into the same bucket in LSH.
 * User specifies <folder> with collection of documents, <number of permutations> for MinHash Matrix,
 * <number of bands> for LSH, <simularity threshold> and <file> to find near duplicates for.  
 * Afterwards this will print out the number of false positives; documents that were hashed together 
 * in the same bucket for LSH.
 * 
 * @author Alex Shum
 */
public class NearDuplicates {

	/**
	 * Calculates the MinHash Matrix and hashes each band.  Documents that are hashed to same bucket 
	 * for any of the bands is considered a near duplicate.  After this LSH procedure, it will remove
	 * false posistives by calculating the exact jaccard similarities and consider documents duplicates 
	 * if the jaccard similarity between the documents is greater than the user specified simularity
	 * threshold.
	 * 
	 * @param args Folder, number of permutations, number of bands, simularity threshold, file.
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	public static void main(String[] args) throws NumberFormatException, IOException {
		if(args.length < 5) throw new IllegalArgumentException(
				"Enter <folder> <num permutations> <num bands> <similarity threshold> <doc name>");
		MinHash mh = new MinHash(args[0], Integer.parseInt(args[1]));
		int[][] hashMtx = mh.minHashMatrix();
		String[] docNames = mh.allDocs();
		LSH lsh = new LSH(hashMtx, docNames, Integer.parseInt(args[2]));
		List<String> nearDuplicates = lsh.nearDuplicatesOf(args[4]);
		
		int FP = 0;
		for(String s : nearDuplicates) {
			
			double sim = mh.exactJaccard(args[4], s);
			if(sim > Double.parseDouble(args[3])) {
				System.out.println(s);
			} else {
				FP++;
			}
		}
		
		System.out.println("Number of false positives: " + FP);
	}

}