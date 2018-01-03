package softwareMeasurement.metric.coupling;

import java.util.List;

import nameTable.nameDefinition.DetailedTypeDefinition;
import softwareMeasurement.measure.SoftwareMeasure;

/**
 * A class to calculate the metric DMMEC
 * 
 * @author Li Jingsheng
 * @since 2015��9��12��
 * @version 1.0
 * @update 2015/10/12, Zhou Xiaocong
 * 
 */
public class DMMECMetric extends SoftwareCouplingMetric{

	@Override
	public boolean calculate(SoftwareMeasure measure) {
		if (type == null || structManager == null) return false;

		double value = 0;
		List<DetailedTypeDefinition> descendantList = structManager.getAllDescendantTypeList(type);
		if (descendantList != null) {
			for(DetailedTypeDefinition descendant : descendantList){
//				Debug.println("DMMEC: Number of MM " + type.getLabel() + " used by descendant " + descendant.getLabel() + ", value = " + value);
				value += CouplingCalculationUtil.getNumberOfMM(structManager, descendant, type);
			}
		}
		
		// Store the value to the measure
		measure.setValue(value);
		// The value of the measure is usable.
		measure.setUsable();
		return true;
	}
}
