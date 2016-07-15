package templateSeg;

import java.util.HashSet;

public class WordEntry {
	public String word;
	public HashSet<String> types;

	public WordEntry(String w, HashSet<String> t) {
		word = w;
		types = t;
	}

	public WordEntry(String w, String type) {
		types = new HashSet<String>();
		types.add(type);

		word = w;
	}

	public void addType(String type) {
		if (types == null)
			types = new HashSet<String>();
		types.add(type);
	}

	public void addType(HashSet<String> value) {
		if (types == null)
			types = new HashSet<String>();
		types.addAll(value);
	}

	public String toString() {
		return word + ":" + types;
	}

}
