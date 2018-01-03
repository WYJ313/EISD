package softwareMeasurement.metric.inheritance;

import java.util.List;

import nameTable.nameDefinition.DetailedTypeDefinition;
import softwareMeasurement.measure.SoftwareMeasure;

/**
 * A class to calculate the metric NOC, the number of children
 * 
 * @author Zhou Xiaocong
 * @since 2015��9��2��
 * @version 1.0
 */
public class NOCMetric extends SoftwareInheritanceMetric {

	@Override
	public boolean calculate(SoftwareMeasure measure) {
		if (type == null || structManager == null) return false;

		List<DetailedTypeDefinition> childList = structManager.getAllChildrenTypeList(type);
		double value = 0;
		if (childList != null) value = childList.size();
		
		measure.setValue(value);
		return true;
	}
}
