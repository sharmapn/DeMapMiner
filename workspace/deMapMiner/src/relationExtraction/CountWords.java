package relationExtraction;

import java.io.*;
import java.util.*;

public class CountWords
{
	public static class Word implements Comparable<Word>
	{
		String word;
		public int count;

		@Override
		public int hashCode()		{
			return word.hashCode();
		}

		@Override
		public boolean equals(Object obj)		{
			return word.equals(((Word)obj).word);
		}

		@Override
		public int compareTo(Word b)		{
			return b.count - count;
		}
	}

	public static void main(String[] args) throws IOException
	{
		long time = System.currentTimeMillis();
		Map<String, Word> countMap = new HashMap<String, Word>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("C:/Users/psharma/Google Drive/PhDOtago/scripts/SelectedSentences.txt")));
		String line;
		while ((line = reader.readLine()) != null) {
			String[] words = line.split("[^A-ZÅÄÖa-zåäö]+");
			for (String word : words) {
				if ("".equals(word)) 
					continue;	
				Word wordObj = countMap.get(word);
				if (wordObj == null) {
					wordObj = new Word();					wordObj.word = word;					wordObj.count = 0;					countMap.put(word, wordObj);
				}
				wordObj.count++;
			}
		}
		reader.close();
		SortedSet<Word> sortedWords = new TreeSet<Word>(countMap.values());
		int i = 0;
		for (Word word : sortedWords) {
			if (i > 100) 
				break;			
			System.out.println(word.count + "\t" + word.word);
			i++;
		}

		time = System.currentTimeMillis() - time;
		System.out.println("in " + time + " ms");
	}
	//public Map<String, Word> countWords() throws IOException{
	public SortedSet<Word> countWords() throws IOException{
		long time = System.currentTimeMillis();
		Map<String, Word> countMap = new HashMap<String, Word>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("C:/Users/psharma/Google Drive/PhDOtago/scripts/SelectedSentences.txt")));
		String line;
		while ((line = reader.readLine()) != null) {
			String[] words = line.split("[^A-ZÅÄÖa-zåäö]+");
			for (String word : words) {
				if ("".equals(word)) 	continue;				
				Word wordObj = countMap.get(word);
				if (wordObj == null) {
					wordObj = new Word();					wordObj.word = word;					wordObj.count = 0;
					countMap.put(word, wordObj);
				}
				wordObj.count++;
			}
		}
		reader.close();			
		SortedSet<Word> sortedWords = new TreeSet<Word>(countMap.values());
		int i = 0;
		System.out.println();
		for (Word word : sortedWords) {
			if (i > 100) {
				break;
			}
			System.out.println(word.count + "\t" + word.word);			
			i++;
		}

		time = System.currentTimeMillis() - time;
		System.out.println("in " + time + " ms");		
		return sortedWords;
	}
}
