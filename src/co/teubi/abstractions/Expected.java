package co.teubi.abstractions;

import java.util.ArrayList;

public class Expected {
	private String text;

	public Expected(String text) {
		this.text = text;
	}

	public boolean isIn(ArrayList<Expectation> expectations) {
		boolean result = false;
		for (int i = 0; i < expectations.size(); i++) {
			result |= this.text.contains(expectations.get(i).text);
		}
		return result;
	}
}