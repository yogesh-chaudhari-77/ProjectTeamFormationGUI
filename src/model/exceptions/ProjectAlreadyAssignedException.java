package model.exceptions;

public class ProjectAlreadyAssignedException extends Exception {
	public ProjectAlreadyAssignedException() {

		System.err.println("The project has already been assigned to other project. Please select a different project");
	}
}
