/*
 * Copyright 2015-2016 Liam Williams <liam.williams@zoho.com>.
 *
 * This file is part of opper.
 *
 * opper is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * opper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with opper.  If not, see <http://www.gnu.org/licenses/>.
 */
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

	public Location end() {
		return location(endLine, endCharacter, endLine, endCharacter);
	}

	public static Location between(Location start, Location end) {
		return location(start.startLine, start.startCharacter, end.endLine, end.endCharacter);
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

	@Override
	public String toString() {
		return "Location{" +
			"startLine=" + startLine +
			", startCharacter=" + startCharacter +
			", endLine=" + endLine +
			", endCharacter=" + endCharacter +
			'}';
	}
}
