import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Miscellaneous functions used to process text documents
 * @author Alex Shum
 */
public class ProcessingFunctions {
	static final List<String> stopWords = new ArrayList<String>(Arrays.asList("the"));
	
	/**
	 * Finds the next prime number larger than a starting integer.
	 * @param n Starting integer.
	 * @return The next prime number larger than starting integer.
	 */
	public static int nextPrime(int n) {
		boolean isPrime = false;
		
		int m = n;
		while(!isPrime) {
			isPrime = isPrime(++m);
		}	
		return(m);
	}
	
	/**
	 * Checks if integer is prime or not.
	 * This is based on the sieve of eratosthenes.
	 * This particular implementation is is based off information from:
	 * http://en.wikipedia.org/wiki/Primality_test
	 * 
	 * @param n Integer to check for primality.
	 * @return true if n is prime otherwise false.
	 */
	public static boolean isPrime(int n) {
		if(n == 1) return(false);
		else if(n == 2 || n == 3) return(true);
		else if(n % 2 == 0 || n % 3 == 0) return(false);
		else {
			for(int i = 5; i*i < n + 1; i += 6) {
				if(n % i == 0 || n % (i + 2) == 0) {
					return(false);
				}
			}
			return(true);
		}
	}
	
	/**
	 * Checks if a string is a stop word or not.  
	 * Stop words include 'the' and words less than length 3.
	 * 
	 * @param s String to check
	 * @return true if the word is in the list of stop words.  
	 */
	public static boolean isStopWord(String s) {
		if(stopWords.contains(s) || s.length() < 3) return(true);
		return(false);
	}
	
	//number of unique words in all text files in a folder
	/**
	 * Counts the number of unique words in a collection of text documents.
	 * Does minimal processing to remove words less than 3 characters and 
	 * 'the'.
	 * @param folder with the collection of text documents
	 * @return number of unique words in all documents
	 * @throws IOException if folder cannot be opened
	 */
	public static int numUnique(File folder) throws IOException {
		Set<String> s = new HashSet<String>();
		File[] contents = folder.listFiles();
		
		FileReader fr;
		BufferedReader b;
		for(int i = 0; i < contents.length; i++) { //iterate through the documents
			if(contents[i].isFile()) {
				fr = new FileReader(contents[i]);
				b = new BufferedReader(fr);
				
				String line;
				String[] words;
				while((line = b.readLine()) != null) { //iterate through lines
					words = line.replaceAll("[.,:;']", "").toLowerCase().split("\\s+"); //remove punctuation
					for(int j = 0; j < words.length; j++) { //iterate through words
						if(!isStopWord(words[j])) s.add(words[j]);
					}
				}
				b.close();
			}
		}
		return(s.size());
	}
	
	/**
	 * Returns a set of unique words in a text file.  Does minimal processing
	 * to remove words less than 3 characters and 'the'.  
	 * @param fileName The file name of the text document.
	 * @return Set of unique words in a text file.
	 * @throws IOException if file cannot be opened
	 */
	public static Set<String> UniqueWordList(String fileName) throws IOException {
		FileReader fr = new FileReader(fileName);
		BufferedReader b = new BufferedReader(fr);
		Set<String> s = new HashSet<String>();
		
		String line;
		String[] words;
		while((line = b.readLine()) != null) {
			words = line.replaceAll("[.,:;']", "").toLowerCase().split("\\s+");
			for(int i = 0; i < words.length; i++) {
				if(!isStopWord(words[i])) s.add(words[i]);
			}
		}
		b.close();
		
		return(s);
	}
}
