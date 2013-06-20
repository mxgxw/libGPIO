package co.teubi.abstractions.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import co.teubi.abstractions.Expectation;
import co.teubi.abstractions.Expectator;
import co.teubi.abstractions.Expected;

public class InputStreamExpectations implements Runnable {
	private ArrayList<Expectation> expectations;
	private InputStream is;
	private Expectator expectator;

	public ArrayList<Expectation> getExpectations() {
		return this.expectations;
	}

	public void setExpectator(Expectator exp) {
		this.expectator = exp;
	}

	public Expectator getExpectator() {
		return this.expectator;
	}

	public InputStreamExpectations(InputStream is) {
		expectations = new ArrayList<Expectation>();
		this.is = is;
		expectator = new Expectator() {

			@Override
			public void fulfill(Expectation exp) {

			}

			@Override
			public void onFailed() {

			}

		};
	}

	public void expects(Expectation e) {
		this.expectations.add(e);
	}

	public void expects(String text) {
		this.expectations.add(new Expectation(text));
	}

	public Expected textFrom(String text) {
		return new Expected(text);
	}

	public Expectation getExpectationFrom(String text) {
		for (int i = 0; i < expectations.size(); i++) {
			if (text.contains(expectations.get(i).text)) {
				return expectations.get(i);
			}
		}
		return null;
	}
	
	public void setExpectations(ArrayList<Expectation> exp) {
		this.expectations = exp;
	}

	@Override
	public void run() {
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line;
		try {
			while ((line = br.readLine()) != null) {
				System.out.println(line);
				if (textFrom(line).isIn(expectations)) {
					expectator.fulfill(getExpectationFrom(line));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
