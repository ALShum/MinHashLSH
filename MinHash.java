import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Generates minhash for a collection of documents.  
 * 
 * The permutations are represented by randomized hash functions: ax + b % p.
 * p is a prime such that p >= n where n is the number of terms in the collection.
 * a and b are chosen uniformly at random from {1,2,...,p-1}.
 * 
 * MinHash matrix generated will by M * N.  M = number documents and N = number of permutations.
 * Each element in the MinHash matrix will be the MinHash value of the document.
 * @note: MinHash matrix has documents as rows and permutations as columns.
 * 
 * There is minimal preprocessing 
 * @author Alex Shum
 */
public class MinHash {
	File folder;
	int numPermutations;
	int numTerms;
	int mod; //p: ax + b % p
	List<Pair> AB; //a, b: ax + b % p

	/**
	 * Constructor that takes a folder and number of permutations.
	 * @param folder Folder with documents.
	 * @param numPermutations Number of permutations for MinHash.
	 * @throws IOException If folder cannot be opened.
	 */
	public MinHash(String folder, int numPermutations) throws IOException {
		this.folder = new File(folder);
		this.numPermutations = numPermutations;
		
		numTerms = ProcessingFunctions.numUnique(this.folder);
		mod = ProcessingFunctions.nextPrime(numTerms);
		AB = generateCoefficients(mod);
	}
	
	/**
	 * Returns names of documents in collection.
	 * @return String array of document names.
	 */
	public String[] allDocs() {
		return(folder.list());
	}
	
	/**
	 * Calculates the exact jaccard simularity between two documents.
	 * @param file1 Filename of first document.
	 * @param file2 Filename of second document.
	 * @return Jaccard simularity
	 * @throws IOException If files cannot be opened.
	 */
	public double exactJaccard(String file1, String file2) throws IOException {
		Set<String> words1 = ProcessingFunctions.UniqueWordList(folder + File.separator + file1);
		Set<String> words2 = ProcessingFunctions.UniqueWordList(folder + File.separator + file2);
		
		int a = words1.size();
		int b = words2.size();
		
		words1.retainAll(words2);
		int intersect = words1.size();
		
		return((double) intersect / (a + b - intersect));
	}
	
	/**
	 * Calculates the MinHash signature.
	 * @param fileName Filename of document.
	 * @return MinHash signature as int array.
	 * @throws IOException If file cannot be opened.
	 */
	public int[] minHashSig(String fileName) throws IOException {
		FileReader fr = new FileReader(folder + File.separator + fileName);
		BufferedReader b = new BufferedReader(fr);
		
		String line;
		String[] words;
		int hashVal;
		int[] minHashVals = new int[numPermutations];
		Arrays.fill(minHashVals, Integer.MAX_VALUE);
		while((line = b.readLine()) != null) { //iterate through lines
			words = line.replaceAll("[.,:;']", "").toLowerCase().split("\\s+"); //remove punctuation
	
			for(int j = 0; j < words.length; j++) { //iterate through words
				if(!ProcessingFunctions.isStopWord(words[j])) {
					for(int i = 0; i < numPermutations; i++) { //hash through k-functions
						hashVal = word2int(words[j], AB.get(i).a, AB.get(i).b, mod);
						if(hashVal < minHashVals[i]) minHashVals[i] = hashVal;
					}
				}
			}
		}
		b.close();
		
		return(minHashVals);
	}
	
	/**
	 * Computes the approximate jaccard simularity by using the MinHash signatures.
	 * @param file1 Filename of first document.
	 * @param file2 Filename of second document.
	 * @return Approximate jaccard simularity.
	 * @throws IOException If files cannot be opened.
	 */
	public double approximateJaccard(String file1, String file2) throws IOException {
		int[] hash1 = minHashSig(file1);
		int[] hash2 = minHashSig(file2);
			
		return(approximateJaccard(hash1, hash2));
	}
	
	/**
	 * Computes the approximate jaccard simularity by using the MinHash signatures.
	 * @param d1 MinHash signature of first document.
	 * @param d2 MinHash signature of second document.
	 * @return Approximate jaccard simularity.
	 */
	public double approximateJaccard(int[] d1, int[] d2) {
		double numMatch = 0.0;
		for(int i = 0; i < numPermutations; i++) {
			if(d1[i] == d2[i]) numMatch++;
		}
		
		return(numMatch / numPermutations);
	}
	
	/**
	 * Computes the MinHash signature for all documents in the collection.
	 * @note The rows of the matrix are the documents and columns are the permutations (hash functions).
	 * @return MinHash signatures as a 2d int array.
	 * @throws IOException If files cannot be read.
	 */
	public int[][] minHashMatrix() throws IOException {
		File[] contents = folder.listFiles();
		int[][] minHashMatrix = new int[contents.length][numPermutations]; //documents are rows
		
		int[] doc;
		for(int i = 0; i < contents.length; i++) {
			if(contents[i].isFile()) {
				doc = minHashSig(contents[i].getName()); 
				
				for(int j = 0; j < numPermutations; j++) {
					minHashMatrix[i][j] = doc[j]; //documents are rows
				}
			} 
		}
		
		return(minHashMatrix);
	}
	
	/**
	 * Gives the total number of unique terms in the collection of documents after basic preprocessing.
	 * See the isStopWord function in PreprocessingFunctions.java for more details.
	 * @return Number of terms in the collection of documents.
	 */
	public int numTerms() {
		return(numTerms);
	}
	
	/**
	 * Gives the number of permutations used for MinHash matrix.
	 * @return Number of permutations
	 */
	public int numPermutations() {
		return(numPermutations);
	}
	
	/**
	 * Hashes a word into an integer using ax + b % p hash function.
	 * @param s Word to hash.
	 * @param a First coefficient in hash function.
	 * @param b Second coefficient in hash function.
	 * @param mod Modulus of hash function.
	 * @return Hash value of word.
	 */
	private int word2int(String s, int a, int b, int mod) {
		int hashed = 0;
		
		for(int i = 0; i < s.length(); i++) {
			hashed ^= s.charAt(i);
			hashed = a + b * hashed;
			hashed = hashed % mod;
		}
		
		return(hashed);
	}
	
	/**
	 * Container object for a pair of coefficients to be used as part of the hash function.
	 * Hash functions of form ax + b % p, this object will store coefficients a and b.
	 * 
	 * This is mainly used as a quick way to check if any of the k-hash functions used for 
	 * the MinHash matrix are duplicated.
	 * @author Alex Shum
	 */
	public class Pair {
		int a, b;
		
		/**
		 * Creates a new coefficient pair container.
		 * @param a The first coefficient.
		 * @param b The second coefficient.
		 */
		public Pair(int a, int b) {
			this.a = a;
			this.b = b;
		}
		
		/**
		 * Checks if another pair container is equal to this one.
		 * @param other The other pair to check for equality.
		 * @return true if both coefficients are equal.  Otherwise false.
		 */
		@Override
		public boolean equals(Object other) {
			if(other == null) return(false);
			if(other == this) return(true);
			if(!(other instanceof Pair)) return(false);
			
			Pair p = (Pair) other;
			return(a == p.a && b == p.b);
		}
	}
	
	/**
	 * Generates k-random hash functions.  k is equal to the number of permutations.
	 * @param mod The modulus for the hash function.
	 * @return List of pairs of coefficients for hash functions.
	 */
	private List<Pair> generateCoefficients(int mod) {
		Random r = new Random();
		List<Pair> coef = new ArrayList<Pair>();
		
		Pair p = new Pair(r.nextInt(mod), r.nextInt(mod));
		for(int i = 0; i < numPermutations; i++) {
			while(coef.contains(p)) {
				p = new Pair(r.nextInt(mod), r.nextInt(mod));
			}
			coef.add(p);
		}
		
		return(coef);
	}
	
}
