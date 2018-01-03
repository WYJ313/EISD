package softwareMeasurement.metric.coupling;

import java.util.Set;

import nameTable.nameDefinition.DetailedTypeDefinition;
import softwareMeasurement.measure.SoftwareMeasure;

/**
 * A class to calculate the metric OCAIC
 * 
 * @author Li Jingsheng
 * @since 2015��9��12��
 * @version 1.0
 * @update 2015/10/12, Zhou Xiaocong
 * 
 */
public class OCAICMetric extends SoftwareCouplingMetric{
	@Override
	public boolean calculate(SoftwareMeasure measure) {
		if (type == null || structManager == null) return false;

		double value = 0;
		if (!type.isInterface()) {
			Set<DetailedTypeDefinition> otherTypeSet = structManager.getUsedOtherDetailedTypeDefinitionSet(type);
			for (DetailedTypeDefinition other : otherTypeSet) {
				value += CouplingCalculationUtil.getNumberOfCA(structManager, type, other);
			}
		}
		
		// Store the value to the measure
		measure.setValue(value);
		// The value of the measure is usable.
		measure.setUsable();
		return true;
	}
}
