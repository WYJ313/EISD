package softwareMeasurement.metric.inheritance;

import softwareMeasurement.measure.SoftwareMeasure;

/**
 * A class to calculate the metric CLD, which is the maximal length to a leaf type from this type. 
 * Note that A leaf type is a type without any children.
 * 
 * @author Zhou Xiaocong
 * @since 2015��10��14��
 * @version 1.0
 */
public class CLDMetric extends SoftwareInheritanceMetric {

	@Override
	public boolean calculate(SoftwareMeasure measure) {
		if (type == null || structManager == null) return false; 

		double value = structManager.getTypeToLeafDepth(type);
		measure.setValue(value);
		return true;
	}

}
