package model.exceptions;

/**
 * Exception implementation
 * Thrown when an attempt is made to add members to a project that has already been assigned to another team
 * In this case, you are required to first dissolve the team, update the project's team reference and then form team for this project
 */
public class ProjectAlreadyAssignedException extends Exception {

	public ProjectAlreadyAssignedException(String msg) {
		super(msg);
		System.err.println(msg);
	}

	public ProjectAlreadyAssignedException() {
		super("The project has already been assigned to other project. Please select a different project");
		System.err.println("The project has already been assigned to other project. Please select a different project");
	}
}
