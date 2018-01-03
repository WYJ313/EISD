package softwareMeasurement;

import nameTable.nameScope.CompilationUnitScope;
import softwareStructure.SoftwareStructManager;

/**
 * @author Zhou Xiaocong
 * @since 2015��7��11��
 * @version 1.0
 */
public class CompilationUnitMeasurement extends NameScopeMeasurement {

	public CompilationUnitMeasurement(CompilationUnitScope scope, SoftwareStructManager manager) {
		super(scope, manager);
	}

	public CompilationUnitScope getCopmilationUnit() {
		return (CompilationUnitScope)scope;
	}
}
