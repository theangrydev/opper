package io.github.theangrydev.opper.scanner;

public class Location {

	private final int startLine;
	private final int startCharacter;
	private final int endLine;
	private final int endCharacter;

	private Location(int startLine, int startCharacter, int endLine, int endCharacter) {
		this.startLine = startLine;
		this.startCharacter = startCharacter;
		this.endLine = endLine;
		this.endCharacter = endCharacter;
	}

	public static Location location(int startLine, int startCharacter, int endLine, int endCharacter) {
		return new Location(startLine, startCharacter, endLine, endCharacter);
	}

	public int startLine() {
		return startLine;
	}

	public int startCharacter() {
		return startCharacter;
	}

	public int endLine() {
		return endLine;
	}

	public int endCharacter() {
		return endCharacter;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || getClass() != other.getClass()) {
			return false;
		}

		Location location = (Location) other;
		return startLine == location.startLine && startCharacter == location.startCharacter && endLine == location.endLine && endCharacter == location.endCharacter;
	}

	@Override
	public int hashCode() {
		int result = startLine;
		result = 31 * result + startCharacter;
		result = 31 * result + endLine;
		result = 31 * result + endCharacter;
		return result;
	}
}
