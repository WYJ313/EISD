package gui.softwareMeasurement.structureBrowser;

public class ProjectInformation {

	private String rootPath;
	private String projectName;

	public ProjectInformation(String rootPath, String projectName) {
		this.rootPath = rootPath;
		this.projectName = projectName;
	}

	public String getLinkedDirPath() {
		return rootPath;
	}

	public String getProjectName() {
		return projectName;
	}

}
