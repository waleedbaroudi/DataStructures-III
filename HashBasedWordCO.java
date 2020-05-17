package code;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HashBasedWordCO {

	HashMapDH<String, HashCounter<String>> coMat;
	HashSet<String> words;
	int windowSize;

	String[] ignoreList;
	int minWordLen;


	public HashBasedWordCO() {
		this(5);
	}

	public HashBasedWordCO(int winSize) {
		setWindowSize(winSize);
		setIgnoredWords(new String[0]);
		setMinimumWordLength(0);
	}

	public void setIgnoredWords(String[] ignoredWords) {
		ignoreList = ignoredWords;

	}

	public void setWindowSize(int winSize) {
		windowSize = winSize;
	}

	public void setMinimumWordLength(int minLen) {
		minWordLen = minLen;
	}

	public List<String> splitSentences(String text) {
		HashSet<Character> lineEnders = new HashSet<Character>();
		lineEnders.put('.');
		lineEnders.put('?');
		lineEnders.put('!');
		return splitSentences(text, lineEnders);
	}

	public List<String> splitSentences(String text, HashSet<Character> lineEnders) {
		ArrayList<String> sentences = new ArrayList<String>();
		int i, preI = 0, n = text.length();
		for (i = 0; i < n; i++) {
			char c = text.charAt(i);
			if (lineEnders.contains(c)) {
				if (i > 0 && i < n - 1) {

					if (Character.isDigit(text.charAt(i - 1)) && Character.isDigit(text.charAt(i + 1))) {
						continue;
					} else {
						sentences.add(text.substring(preI, i).trim().replaceAll("[^A-Za-z0-9 ]+", "").toLowerCase());
						preI = i + 1;
					}
				}
			}
		}
		if (preI < i)
			sentences.add(text.substring(preI, i).trim().replaceAll("[^A-Za-z0-9 ]+", "").toLowerCase());
		return sentences;
	}

	public HashSet<String> getUniqueWords(String text) {
		HashSet<String> uWords = new HashSet<String>();
		List<String> sentences = splitSentences(text);

		for (String sentence : sentences) {
			for (String word : sentence.split("\\s+")) {
				if (word.length() < minWordLen || Arrays.asList(ignoreList).contains(word))
					continue;
				uWords.put(word.toLowerCase());
			}
		}
		return uWords;
	}

	public void fillCoMat(String text) {
		fillCoMat(text, null);
	}

	public void fillCoMat(String text, String[] keyWords) {
		if (text == null)
			return;
		coMat = new HashMapDH<String, HashCounter<String>>();

		if (keyWords != null) {
			if (keyWords.length != 0) {
				words = new HashSet<String>();
				for (String word : keyWords)
					words.put(word);
			}
		} else
			words = getUniqueWords(text);

		for (String word : words.keySet())
			coMat.put(word, new HashCounter<String>());
		List<String> sentences = splitSentences(text);

		// TODO: Implement co-occurence calculations
		for (String sentence : sentences) {
			String wordsInSentence[] = sentence.split("\\s+");
			for (int i = 0; i < wordsInSentence.length; i++) {
				if (!words.contains(wordsInSentence[i]))
					continue;
				for (String neighbor : getNeighbours(i, wordsInSentence)) {
					if ((neighbor != null) && words.contains(neighbor)) {
						coMat.get(wordsInSentence[i]).increment(neighbor.toLowerCase());
					}
				}
			}
		}
	}

	private String[] getNeighbours(int ind, String[] words) {
		String[] neighbors = new String[2 * windowSize + 1];
		for (int i = ind - windowSize, j = 0; i <= ind + windowSize; i++, j++) {
			if ((i < 0) || (i >= words.length))
				continue;
			if (i == ind) {
				neighbors[j] = null;
			} else {
				neighbors[j] = words[i];
			}
		}
		return neighbors;
	}

	public int getCoOccurrenceValue(String word1, String word2) {
		if (coMat.get(word1) == null)
			return 0;
		else
			return coMat.get(word1).getCount(word2);
	}

	public void printMatrix() {
		if (coMat == null)
			System.out.format("Empty co-occurrence matrix!");
		System.out.format("%10s", " ");
		for (String word : words.keySet()) {
			System.out.format("%10s", word);
		}
		System.out.println("");

		for (String word1 : words.keySet()) {
			System.out.format("%10s", word1);
			for (String word2 : words.keySet()) {
				String tmp = "%10s";
				System.out.format(tmp, Integer.toString(coMat.get(word1).getCount(word2)));										
			}
			System.out.println("");
		}
	}

	public void printMatrixCSV(String fileName) {
		if (coMat == null) {
			System.out.format("Empty co-occurrence matrix!");
			return;
		}

		FileWriter fileWrite = null;
		try {
			fileWrite = new FileWriter(fileName);

			int i = 0;
			for (String word : words.keySet()) {
				if (i++ > 0)
					fileWrite.append(",");
				fileWrite.append(word);
			}
			fileWrite.append("\n");

			for (String word1 : words.keySet()) {
				i = 0;
				fileWrite.append(word1 + ",");
				for (String word2 : words.keySet()) {
					if (i++ > 0)
						fileWrite.append(",");
					fileWrite.append(Integer.toString(coMat.get(word1).getCount(word2)));
				}
				fileWrite.append("\n");
			}

			fileWrite.close();
		} catch (IOException e) {
			System.out.println("Error Writing CSV file: " + fileName);
			System.exit(0);
		}
	}

}
