package softwareMeasurement.metric.inheritance;

import java.util.List;

import nameTable.nameDefinition.DetailedTypeDefinition;
import nameTable.nameDefinition.MethodDefinition;
import softwareMeasurement.measure.SoftwareMeasure;
import softwareStructure.SoftwareStructManager;

/**
 * A class to calculate the metric DPA, which is the number of class method which has the methods with the same 
 * name and the same signature in the descendants of the class. The method which has the methods with the same name 
 * and the same signature (i.e. a method override another method) in the ancestors or descendants of the class is 
 * called dynamic polymorphism. 
 * <p>Note that we do not consider the metric DPA for interface, which will be always 0
 *   
 * @author Zhou Xiaocong
 * @since 2015��10��14��
 * @version 1.0
 */
public class DPDMetric extends SoftwareInheritanceMetric {

	@Override
	public boolean calculate(SoftwareMeasure measure) {
		if (type == null || structManager == null) return false;
		
		double value = getDynamicPolymorphismInDescendants(structManager, type);
		measure.setValue(value);
		return true;
	}

	/**
	 * Calculate the number of method with static polymorphism.
	 * <p> This methods is public and static because it also be used to calculate the metric SP
	 */
	public static int getDynamicPolymorphismInDescendants(SoftwareStructManager structManager, DetailedTypeDefinition type) {
		if (type.isInterface()) return 0;
		
		// Get all methods declared in the type
		List<MethodDefinition> methodList = type.getMethodList();
		if (methodList == null) return 0;
		
		// Note that all inherited methods give the all methods declared in the ancestors of the type. 
		// Note that inherited methods do not include those private methods and constructor in the ancestors 
		List<MethodDefinition> ancestorMethodList = structManager.getAllMethodsInDescendants(type);
		if (ancestorMethodList == null) return 0;
		int result = 0;
		for (MethodDefinition method : methodList) {
			for (MethodDefinition ancestorMethod : ancestorMethodList) {
				// This method has dynamic polymorphism since is an overrided method for a method in an ancestor type.
				if (method.isOverrideMethod(ancestorMethod)) {
					result = result + 1;

					// Do not consider other ancestor methods, since we only compute the number of method rather than the number of override
					break;
				}
			}
		}
		
		return result;
	}
}
