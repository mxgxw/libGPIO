package co.teubi.abstractions;

public interface Expectator {
	public void fulfill(Expectation exp);
	public void onFailed();
}
