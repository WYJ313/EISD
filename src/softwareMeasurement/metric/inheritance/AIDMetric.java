package softwareMeasurement.metric.inheritance;

import softwareMeasurement.measure.SoftwareMeasure;

/**
 * A class to calculate the metric AID, which is the average inheritance depth of a class. 
 * <p> If the class does not have extended any class and does not have implemented any interface, then its average 
 * depth is 1, otherwise, its averageDepthOfInheritance will be the average of average depth of its parent (directed 
 * extended class and directed implemented interfaces) plus 1.
 * 
 * @author Zhou Xiaocong
 * @since 2015��10��14��
 * @version 1.0
 */
public class AIDMetric extends SoftwareInheritanceMetric {

	@Override
	public boolean calculate(SoftwareMeasure measure) {
		if (type == null || structManager == null) return false; 

		double value = structManager.getAverageInheritanceDepth(type);
		measure.setValue(value);
		return true;
	}

}
