package softwareMeasurement;

import nameTable.nameDefinition.PackageDefinition;
import softwareStructure.SoftwareStructManager;

/**
 * @author Zhou Xiaocong
 * @since 2015��9��17��
 * @version 1.0
 */
public class PackageMeasurement extends NameScopeMeasurement {
	
	public PackageMeasurement(PackageDefinition scope, SoftwareStructManager manager) {
		super(scope, manager);
	}
	
	public PackageDefinition getPackageDefinition() {
		return (PackageDefinition)scope;
	}
}
