package softwareMeasurement.metric.inheritance;

import java.util.List;

import nameTable.nameDefinition.DetailedTypeDefinition;
import softwareMeasurement.measure.SoftwareMeasure;

/**
 * A class to calculate the metric NOD, the number of descendants
 * 
 * @author Wu Zhangshen
 * @since 2015��9��2��
 * @version 1.0
 * @update 2015/10/13
 */
public class NODMetric extends SoftwareInheritanceMetric {

	@Override
	public boolean calculate(SoftwareMeasure measure) {
		if (type == null || structManager == null) return false; 
		
		List<DetailedTypeDefinition> descendantList = structManager.getAllDescendantTypeList(type);
		double value = 0;
		if (descendantList != null) value = descendantList.size();
		
		measure.setValue(value);
		return true;
	}

}
