import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Uses LSH to find near duplicates for documents.  This is done by splitting the 
 * MinHash matrix into bands and hashing each band.  Any documents where a band
 * is hashed to the same bucket are considered near duplicates. 
 * 
 * @author Alex Shum
 */
public class LSH {
	private int n; //number of documents
	int rows; //number of rows per band
	private int[][] minHashMatrix; //min hash mtx
	private String[] docNames; //docnames
	private Map<Pair, String> hashTable; //Key = <Band, string hash value>
	
	int p; //hash table modulous
	int a; //hash function ax + b % p
	int b; //hash function ax + b % p
	String name;
	
	/**
	 * Creates a new LSH object.
	 * @param minHashMatrix MinHash matrix where rows are documents, columns are the hash functions.
	 * @param docNames Array of document names in collection.
	 * @param bands Number of bands to split minHash matrix into.
	 */
	public LSH(int[][] minHashMatrix, String[] docNames, int bands) {
		Random r = new Random();
		
		n = minHashMatrix.length;
		rows = minHashMatrix[0].length / bands;
		this.minHashMatrix = minHashMatrix;
		this.docNames = docNames;
		
		p = ProcessingFunctions.nextPrime(5 * n);
		a = r.nextInt(p);
		b = r.nextInt(p);
		hashTable = new HashMap<Pair, String>();
		
		int currBand = 0;
		int currProd = 1;
		for(int i = 0; i < n; i++) { //all documents
			for(int j = 0; j < minHashMatrix[0].length; j++) { //rows in document
				currBand = j / rows;
				currProd = currProd + (a * minHashMatrix[i][j] + b);
				currProd = currProd % p;

				if((j + 1) % rows == 0 || (j + 1) == n) {
					Pair pa = new Pair(currBand, currProd);
					String names = hashTable.get(pa);
					names = names == null ? docNames[i] : names + "~::~" + docNames[i];
					
					hashTable.put(pa, names);
					currProd = 1;
				}	
			}
		}
	}
	
	/**
	 * Computes a list of near duplicate documents.  Near duplicate documents are
	 * documents where at least one of the bands hash to the same bucket.
	 * @param docName The document to find near duplicates for.
	 * @return List of near duplicates for docName.
	 */
	public ArrayList<String> nearDuplicatesOf(String docName) {
		Set<String> setDuplicates = new HashSet<String>();
		ArrayList<String> nearDuplicates = new ArrayList<String>();
		
		int docIndex = 0;
		for(int i = 0; i < n; i++) {
			if(docNames[i].equals(docName)) {
				docIndex = i;
			}
		}
		
		int currBand = 0;
		int currProd = 1;
		String[] currString;
		for(int i = 0; i < minHashMatrix[docIndex].length; i++) {
			currBand = i / rows;
			currProd = currProd + (a * minHashMatrix[docIndex][i] + b);
			currProd = currProd % p;
			
			if((i + 1) % rows == 0 || (i + 1) == minHashMatrix[docIndex].length) {
				Pair pa = new Pair(currBand, currProd);
				String names = hashTable.get(pa);
				names = names == null ? "" : names;
				
				if(!names.equals("")) {
					currString = names.split("~::~");
					setDuplicates.addAll(Arrays.asList(currString));
				}
				currProd = 1;
			}	
		}
			
		nearDuplicates.addAll(setDuplicates);
		return(nearDuplicates);
	}
	
	/**
	 * Container object to store the band number and hash value.
	 * In LSH, the MinHash matrix is split into B bands: 1,2,...,B
	 * and each band is R-rows.  The R-rows in a band are hashed to
	 * get a hash value for that band.  This object stores the band
	 * Number and the hash value for that band.
	 * 
	 * @author Alex Shum
	 */
	public class Pair {
		int band;
		int hashVal;
		
		/**
		 * Creates a new container object.
		 * @param band Band integer.
		 * @param hashVal Hash value for that band.
		 */
		public Pair(int band, int hashVal) {
			this.band = band;
			this.hashVal = hashVal;
		}
		
		
		/**
		 * Returns the hash code of container for HashMaps.
		 * @return HashCode of Pair container.
		 */
		@Override 
		public int hashCode() {
			int hash = 5;
			hash = hash * band * hashVal;
			
			return(hash);
		}
		
		
		/**
		 * Compares equality against other Pair containers.
		 * @param The other object to check for equality
		 * @return True if band and hashvalue are equal, otherwise false.
		 */
		@Override
		public boolean equals(Object other) {
			if(other == null) return(false);
			if(other == this) return(true);
			if(!(other instanceof Pair)) return(false);
			
			Pair p = (Pair) other;
			return(band == p.band && hashVal == p.hashVal);
		}
		
		/**
		 * Gives a string representation of this object.
		 * @return String representation with band and hashvalue.
		 */
		@Override
		public String toString() {
			String s = "(" + band + "," + hashVal + ")";
			
			return(s);
		}
	}
}
