package analyzer.dataTable;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.ranking.NaNStrategy;
import org.apache.commons.math3.stat.ranking.NaturalRanking;
import org.apache.commons.math3.stat.ranking.TiesStrategy;

import util.Debug;

/**
 * A class to provide some static methods to utilize data table to generate some useful data or table
 * 
 * @author Zhou Xiaocong
 * @since 2015��10��19��
 * @version 1.0
 */
public class DataTableUtil {

	/**
	 * Count the number of different value in the column of dataTable. 
	 * The result data table has two column: Value, Number and Frequency
	 */
	public static DataTableManager countValueNumberDistribution(String[] valueArray) {
		if (valueArray == null || valueArray.length <= 0) return null;
		TreeMap<String, Integer> countMap = new TreeMap<String, Integer>();
		for (int i = 0; i < valueArray.length; i++) {
			String value = valueArray[i];
			Integer number = countMap.get(value);
			if (number == null) {
				countMap.put(value, new Integer(1));
			} else {
				countMap.put(value, new Integer(number.intValue()+1));
			}
		}
		
		DataTableManager result = new DataTableManager("ValueNumber");
		String[] columnNames = {"Value", "Number", "Frequency"};
		result.setColumnNames(columnNames);
		Set<String> valueSet = countMap.keySet();
		for (String value : valueSet) {
			Integer number = countMap.get(value);
			String numberString = countMap.get(value).toString();
			String[] lineArray = new String[3];
			lineArray[0] = value;
			lineArray[1] = numberString;
			double freq = (double)number.intValue() / valueArray.length;
			lineArray[2] =  Double.toString(freq);
			result.appendLine(lineArray);
		}
		return result;
	}
	
	/**
	 * Return the five numbers a double array. We will remove the NaN value in the column. 
	 * But if the column is not in the table or all values of the column are NaN, it will return NaN.
	 */
	public static double[] fiveNumberOfColumn(DataTableManager dataTable, String column) {
		double[] valueArray = dataTable.getColumnAsDoubleArray(column);
		return fiveNumberOfDoubleArray(valueArray);
	}

	/**
	 * Return the five numbers a double array. We will remove the NaN value in the column. 
	 * But if the column is not in the table or all values of the column are NaN, it will return NaN.
	 */
	public static double[] fiveNumberOfDoubleArray(double[] valueArray) {
		if (valueArray == null) return null;
		double[] result = new double[5];
		result[0] = StatUtils.min(valueArray);
		result[1] = StatUtils.percentile(valueArray, 25);
		result[2] = StatUtils.percentile(valueArray, 50);
		result[3] = StatUtils.percentile(valueArray, 75);
		result[4] = StatUtils.max(valueArray);
		return result;
	}

	/**
	 * Return the mean value of a double array. We will remove the NaN value in the column. 
	 * But if the column is not in the table or all values of the column are NaN, it will return NaN.
	 */
	public static double meanOfDoubleArray(double[] valueArray) {
		if (valueArray == null) return Double.NaN;
		double sum = 0.0;
		int counter = 0;
		for (int i = 0; i < valueArray.length; i++) {
			if (!Double.isInfinite(valueArray[i]) && !Double.isNaN(valueArray[i])) {
				sum += valueArray[i];
				counter += 1;
			}
		}
		if (counter > 0) return sum/counter;
		else return Double.NaN;
	}

	/**
	 * Return the mean value of a column in the given data table. We will remove the NaN value in the column. 
	 * But if the column is not in the table or all values of the column are NaN, it will return NaN.
	 */
	public static double meanOfColumn(DataTableManager dataTable, String column) {
		double[] valueArray = dataTable.getColumnAsDoubleArray(column);
		if (valueArray == null) return Double.NaN;
		double sum = 0.0;
		int counter = 0;
		for (int i = 0; i < valueArray.length; i++) {
			if (!Double.isInfinite(valueArray[i]) && !Double.isNaN(valueArray[i])) {
				sum += valueArray[i];
				counter += 1;
			}
		}
		if (counter > 0) return sum/counter;
		else return Double.NaN;
	}
	
	/**
	 * Return an array of index of the values in valueArray. By the returned index array, we can easily scan the 
	 * values in the valueArray according to decreasing order or increasing order of the values.   
	 * That is, if decreasingOrder == true, then the returned array [0] is the index of the maximal value in the array 
	 * and the returned array [length-1] is the index of the minimal value in the array with the minimal value. 
	 * Similarly, if decreasingOrder == false, then the returned array [0] is the index of the minimal value in the array
	 * while the returned array [length-1] is the index of the maximal value in the array.      
	 */
	public static int[] getSortedIndexOfDoubleArray(double[] valueArray, boolean decreasingOrder) {
		// Use the class NaturalRanking in org.apache.commons.math3 to get the rank of the valueArray 
		NaturalRanking naturalRanking = new NaturalRanking(NaNStrategy.MINIMAL, TiesStrategy.SEQUENTIAL);
		double[] rankArray = naturalRanking.rank(valueArray);
		int[] sortedIndexArray = new int[valueArray.length];
		for (int index = 0; index < valueArray.length; index++) {
			int sortedIndex = 0;
			if (decreasingOrder == true) {
				// Generate the index of the values in valueArray in decreasing order according to the rank array
				sortedIndex = valueArray.length - (int)rankArray[index];
				// Note that the value in valueArray at the index should be placed in sortedIndex when we 
				// sort the valueArray in decreasing order. For example, if the rankArray[index] = valueArray.length, 
				// that is, valueArray[index] is the maximal value in the array, then it will be placed in 0 (i.e. the first
				// element of the decreasing sorted array), which is equal to valueArray.length - rankArray[index]
			} else {
				// Generate the index of the values in valueArray in increasing order according to the rank array
				sortedIndex = (int)rankArray[index] - 1;
				// Note that the value in valueArray at the index should be placed in sortedIndex when we 
				// sort the valueArray in increasing order. For example, if the rankArray[index] = 1, 
				// that is, valueArray[index] is the minimal value in the array, then it will be placed in 0 (i.e. the first
				// element of the decreasing sorted array), which is equal to  rankArray[index] - 1
			}
			sortedIndexArray[sortedIndex] = index;
		}
		return sortedIndexArray;
	}
	
	/**
	 *	����һ���ٷֱ�����  percentileArray, ���� double ���顡valueArray����ֵ�����·ֲ������
	 * Item    percentileArray[0]  percentileArray[1]  ...  percentileArray[n-1], percentileArray[i] ����һ��0��100֮�����
	 * Rank    ����valueArray�а��ս������е���ǰ�ٷ�֮percentileArray[i]�����ݵ����������percentileArray[i] = 0�����Ӧ���ֵ���Ӷ� Rank = 1
	 * Value   ����valueArray�������ڶ�Ӧ Rank ������ֵ
	 * Sum	       ����valueArray�д����ֵ��������Rank������֮���ֵ���ܺ�
	 * Ratio   ����valueArray�д����ֵ��������Rank������֮���ֵ���ܺ�ռvalueArray������ֵ���ܺ͵ı�
	 * 
	 * ע�⣬valueArray �з�doubleֵ��Ҳ���� Double.NaN�����ݣ�����Ϊ��Сֵ����	            
	 */
	public static DataTableManager generatePercentileDistribution(double[] valueArray, double[] percentileArray) {
		if (valueArray == null || percentileArray == null) return null;
		if (valueArray.length <= 0 || percentileArray.length <= 0) return null;
		
		DataTableManager tableManager = new DataTableManager("Percentile distribution");
		
		int[] sortedIndexArray = getSortedIndexOfDoubleArray(valueArray, true);
		// Calculate the sum of the values in valueArray
		double totalSum = 0;
		for (int index = 0; index < valueArray.length; index++) {
			if (!Double.isInfinite(valueArray[index])) totalSum += valueArray[index];
		}

		String[] columnNameArray = new String[percentileArray.length + 1];
		columnNameArray[0] = "Item";
		for (int index = 0; index < percentileArray.length; index++) {
			if (percentileArray[index] == 0) columnNameArray[index+1] = "Max";
			else columnNameArray[index+1] = Double.toString(percentileArray[index]);
		}
		tableManager.setColumnNames(columnNameArray);
		tableManager.setKeyColumnIndex(0);
		String[] itemColumn = {"Rank", "Value", "Sum", "Ratio"};
		tableManager.setColumnValue(columnNameArray[0], itemColumn);
		
		double currentSum = 0;
		int lastRank = 0;
		for (int index = 0; index < percentileArray.length; index++) {
			int rank = 1;
			if (percentileArray[index] == 0) {
				// We should get the maximal value in the valueArray, that is its rank should be 1
				rank = 1; 
			} else {
				// We compute the rank of the value that is ranked at the front of percentileArray[index]% in valueArray
				// For example, if we have 200 values in valueArray (i.e. valueArray.length = 200), and 
				// percentileArray[index] = 5, we should get the value at ranked 10
				rank = (int)((percentileArray[index] / 100) * valueArray.length);
				if (rank > valueArray.length) rank = valueArray.length;
			}
			// Note that the range of rank is from 1 to value.length, but the range of array index is from 0 to value.length-1
			// So we have to minus 1 from rank to get the index of the value.
			double value = valueArray[sortedIndexArray[rank-1]];
			// Compute the sum of the value which at ranked from 1 to rank-1. Since the currentSum has stored the sum
			// of the value which at ranked from 1 to lastRank, so here we only add the value which at ranked from
			// lastRank+1 to rank.
			for (int sumRank = lastRank+1; sumRank <= rank; sumRank++) {
				currentSum += valueArray[sortedIndexArray[sumRank-1]];
			}
			double ratio = 0;
			if (totalSum != 0) ratio = currentSum / totalSum;
			
			tableManager.setCellValue("Rank", columnNameArray[index+1], rank);
			tableManager.setCellValue("Value", columnNameArray[index+1], value);
			tableManager.setCellValue("Sum", columnNameArray[index+1], currentSum);
			tableManager.setCellValue("Ratio", columnNameArray[index+1], ratio);
			
			lastRank = rank;		// Remember the rank for computing the currentSum 
		}
		return tableManager;
	}

	public static DataTableManager findOutlierLines(DataTableManager dataManager, DataTableManager outlierIndicator) {
		return findOutlierLines(dataManager, outlierIndicator, false);
	}

	public static DataTableManager findOutlierLinesWithReason(DataTableManager dataManager, DataTableManager outlierIndicator) {
		return findOutlierLines(dataManager, outlierIndicator, true);
	}

	/**
	 * �����ݱ� dataManager �����쳣ֵ���쳣ֵ��׼�����ݱ� outlierIndicator �ṩ��outlierIndicator �������� Fields ��ֵ��Ӧ dataManager �е�������outlierIndicator
	 * �������� Sd2 ֵ��Ӧ�쳣ֵ��׼������outlierIndicator��ĳһ������Ϊ��"FLD 20.1"����ʾdataManager�е�����Ϊ FLD ���е�ֵ���� 20.1 �������о����쳣ֵ��outlierIndicator
	 * �����ж���쳣ֵ��׼����Щ��׼֮����߼���ϵ�ǡ��򡱣���ֻҪ��������һ����׼�����쳣ֵ���ҵ����쳣ֵ�н����ڷ��ص����ݱ��У��������ݱ��нṹ�ڲ��� needReason Ϊfalse ʱ��
	 * dataManager���нṹ��ȫһ�£������� needReason Ϊtrueʱ������һ��"Reason"����¼��ѡ��Ϊ�쳣ֵ�������е�ԭ�򣬼�������Щ���������쳣ֵ��׼��   
	 */
	public static DataTableManager findOutlierLines(DataTableManager dataManager, DataTableManager outlierIndicator, boolean needReason) {
		DataTableManager resultManager = new DataTableManager("Outlier");
		String[] columnNameArray = dataManager.getColumnNameArray();
		String[] resultColumnNameArray = null;
		if (needReason == true) {
			resultColumnNameArray = new String[columnNameArray.length+2];
			resultColumnNameArray[resultColumnNameArray.length-2] = "Reason";
			resultColumnNameArray[resultColumnNameArray.length-1] = "Upper";
		} else {
			resultColumnNameArray = new String[columnNameArray.length+1];
			resultColumnNameArray[resultColumnNameArray.length-1] = "Upper";
		}
		for (int i = 0; i < columnNameArray.length; i++) resultColumnNameArray[i] = columnNameArray[i];
		resultManager.setColumnNames(resultColumnNameArray);

		int keyColumnIndex = dataManager.getKeyColumnIndex();
		resultManager.setKeyColumnIndex(keyColumnIndex);

		DataLineDefaultFilter filter = new DataLineDefaultFilter();
		for (int lineIndex = 0; lineIndex < outlierIndicator.getLineNumber(); lineIndex++) {
			String columnName = outlierIndicator.getCellValueAsString(lineIndex, "Fields");
			String outlierValue = outlierIndicator.getCellValueAsString(lineIndex, "Sd2");
			if (outlierValue == null) outlierValue = outlierIndicator.getCellValueAsString(lineIndex, "OutUp");
			if (outlierValue != null) {
				DataLineFilterValueCondition condition = new DataLineFilterValueCondition(columnName, outlierValue);
				condition.setLogic(ConditionLogicKind.CLK_OR);
				condition.setType(ConditionDataTypeKind.CDTK_NUMERIC);
				condition.setRelation(ConditionRelationKind.CRK_GREATER_EQUAL);
				filter.addCondition(condition);
			}
		}
		if (needReason == true) filter.setNeedReasonOn();
		
		for (int lineIndex = 0; lineIndex < dataManager.getLineNumber(); lineIndex++) {
			if (filter.accept(dataManager, lineIndex)) {
				Debug.println("Accept line " + lineIndex);
				
				String[] lineStringArray = dataManager.getLineAsStringArray(lineIndex);
				String[] resultLineStringArray = null;
				if (needReason == true) {
					resultLineStringArray = new String[lineStringArray.length+2];
					for (int i = 0; i < lineStringArray.length; i++) resultLineStringArray[i] = lineStringArray[i];
					resultLineStringArray[resultLineStringArray.length-2] = filter.getReason();
				} else resultLineStringArray = new String[lineStringArray.length+1];
				resultLineStringArray[resultLineStringArray.length-1] = "TRUE";
				resultManager.appendLine(resultLineStringArray);
			}
		}

		filter = new DataLineDefaultFilter();
		for (int lineIndex = 0; lineIndex < outlierIndicator.getLineNumber(); lineIndex++) {
			String columnName = outlierIndicator.getCellValueAsString(lineIndex, "Fields");
			String outlierValue = outlierIndicator.getCellValueAsString(lineIndex, "SdN2");
			if (outlierValue == null) outlierValue = outlierIndicator.getCellValueAsString(lineIndex, "OutLow");
			if (outlierValue != null) {
				DataLineFilterIntervalCondition condition = new DataLineFilterIntervalCondition(columnName, "0.0", outlierValue);
				condition.setLogic(ConditionLogicKind.CLK_OR);
				condition.setType(ConditionDataTypeKind.CDTK_NUMERIC);
				condition.setRelation(ConditionRelationKind.CRK_IN_INTERVAL);
				condition.setInterval(ConditionIntervalKind.CIK_OPEN_CLOSE);
				filter.addCondition(condition);
			}
		}
		if (needReason == true) filter.setNeedReasonOn();
		
		for (int lineIndex = 0; lineIndex < dataManager.getLineNumber(); lineIndex++) {
			if (filter.accept(dataManager, lineIndex)) {
				Debug.println("Accept line " + lineIndex);
				
				String[] lineStringArray = dataManager.getLineAsStringArray(lineIndex);
				String[] resultLineStringArray = null;
				if (needReason == true) {
					resultLineStringArray = new String[lineStringArray.length+2];
					for (int i = 0; i < lineStringArray.length; i++) resultLineStringArray[i] = lineStringArray[i];
					resultLineStringArray[resultLineStringArray.length-2] = filter.getReason();
				} else resultLineStringArray = new String[lineStringArray.length+1];
				resultLineStringArray[resultLineStringArray.length-1] = "FALSE";
				resultManager.appendLine(resultLineStringArray);
			}
		}
		
		return resultManager;
	}
	
	/**
	 * �����ݱ� dataManager ������ columnName ȡ���ֵ����Сֵ�������С�����groupBy Ҳ�� dataManager �е��У������ڲ��� dataManager ����columnName ȡ
	 * ���ֵ��Сֵʱ��Ҫ���� groupBy ��һ�е�ֵ���з��飬��ʱ���� groups �ṩ���� groupBy ��һ�������ڷ���Ĳ�ͬ������ֵ���� dataManager �е��� groupBy ������ groups[i] ��
	 * ��Ϊһ�����ݣ�������һ���������� columnName ȡ���ֵ��Сֵ�������зŵ����ص����ݱ��С����� maxMin = 0 ��ʾ���ֵ����Сֵ��Ҫ��= 1 ��ʾֻҪ���ֵ��= -1 ��ʾֻҪ��Сֵ��
	 * ���� findLineNumber ��ʾҪ���Ҷ��ٸ���ֵ���������1��ʾֻҪ���ֵ��/����Сֵ������2���ʾ�������ֵ��/����Сֵ�⣬��Ҫ�δ�ֵ��/���Сֵ�ȵȡ����ص����ݱ��нṹ�� dataManager 
	 * ��ȫ��ͬ�� 
	 * <p>Note that the type of the column columnName must be double or int, and the type of the column groupBy must be String
	 * 
	 */
	public static DataTableManager findMaxMinLines(DataTableManager dataManager, String columnName, String groupBy, String[] groups, int findLineNumber, int maxMin) {
		// resultLineIndex will be the index of the line finally selected
		int[] resultLineIndexArray = null;
		if (maxMin == 0) resultLineIndexArray = new int[groups.length * findLineNumber * 2];
		else resultLineIndexArray = new int[groups.length * findLineNumber];
		for (int resultIndex = 0; resultIndex < resultLineIndexArray.length; resultIndex++) resultLineIndexArray[resultIndex] = -1;
		
		int resultIndex = 0;
		for (int groupIndex = 0; groupIndex < groups.length; groupIndex++) {
			// The data line filter is used to select the line whose value of the column groupBy equals groups[groupIndex] 
			DataLineFilterValueCondition condition = new DataLineFilterValueCondition(groupBy, groups[groupIndex]);
			condition.setRelation(ConditionRelationKind.CRK_EQUAL);
			condition.setType(ConditionDataTypeKind.CDTK_STRING);
			DataLineDefaultFilter filter = new DataLineDefaultFilter();
			filter.addCondition(condition);
			
			List<ValueIndexPair> pairList = new ArrayList<ValueIndexPair>();
			for (int dataIndex = 0; dataIndex < dataManager.getLineNumber(); dataIndex++) {
				if (filter.accept(dataManager, dataIndex)) {
					// Add the value of the column columnName and its line index to pariList
					double value = dataManager.getCellValueAsDouble(dataIndex, columnName);
					pairList.add(new ValueIndexPair(value, dataIndex));
				}
			}
			
			if (pairList.size() <= 0) {
				// There is no line whose value of the column groupBy equals groups[groupIndex]
				continue;
			}
			
			double[] lineValueArray = new double[pairList.size()];
			int[] lineIndexArray = new int[pairList.size()];
			for (int i = 0; i < pairList.size(); i++) {
				lineValueArray[i] = pairList.get(i).value;
				lineIndexArray[i] = pairList.get(i).index;
			}
			// Find the maximal and minimal value in the lineValueArray by generating its sorted index 
			int[] sortedIndex = getSortedIndexOfDoubleArray(lineValueArray, true);
			for (int i = 0; i < findLineNumber; i++) {
				int maxIndex = i;
				if (maxMin == 0 || maxMin == 1) {
					if (maxIndex >= 0 && maxIndex < lineValueArray.length) {
						// Correspondingly, the content of lineIndexArray[maxIndex] will be the index of the line in the dataManager
						// which the value of the column columnName is the i th maximal value in this group
						resultLineIndexArray[resultIndex] = lineIndexArray[sortedIndex[maxIndex]];
						resultIndex = resultIndex + 1;
					}
				}
			}
			for (int i = 0; i < findLineNumber; i++) {
				int minIndex = sortedIndex.length-i-1;
				if (maxMin == 0 || maxMin == -1) {
					if (minIndex >= 0 && minIndex < lineValueArray.length) {
						// Correspondingly, the content of lineIndexArray[minIndex] will be the index of the line in the dataManager
						// which the value of the column columnName is the i th minimal value in this group
						resultLineIndexArray[resultIndex] = lineIndexArray[sortedIndex[minIndex]];
						resultIndex = resultIndex + 1;
					}
				}
			}
		}

		DataTableManager resultManager = DataTableManager.createEmptyDataTableWithSameStructure(dataManager, "MaxMin");
		// So far, the index of the line should be selected are stored in the resultLineIndexArray
		for (int i = 0; i < resultLineIndexArray.length; i++) {
			int index = resultLineIndexArray[i];
			if (index >= 0 && index < dataManager.getLineNumber()) {
				String[] lineStringArray = dataManager.getLineAsStringArray(index);
				resultManager.appendLine(lineStringArray);
			}
		}
		return resultManager;
	}
	

	/**
	 * �����ݱ� dataManager ������ columnName ȡ���ֵ����Сֵ�������С����� maxMin = 0 ��ʾ���ֵ����Сֵ��Ҫ��= 1 ��ʾֻҪ���ֵ��= -1 ��ʾֻҪ��Сֵ��
	 * ���� findLineNumber ��ʾҪ���Ҷ��ٸ���ֵ���������1��ʾֻҪ���ֵ��/����Сֵ������2���ʾ�������ֵ��/����Сֵ�⣬��Ҫ�δ�ֵ��/���Сֵ�ȵȡ����ص����ݱ��нṹ�� dataManager 
	 * ��ȫ��ͬ�� 
	 * <p>Note that the type of the column columnName must be double or int
	 * 
	 */
	public static DataTableManager findMaxMinLines(DataTableManager dataManager, String columnName, int findLineNumber, int maxMin) {
		DataTableManager resultManager = new DataTableManager("MaxMin");

		String[] columnNameArray = dataManager.getColumnNameArray();
		resultManager.setColumnNames(columnNameArray);

		int keyColumnIndex = dataManager.getKeyColumnIndex();
		resultManager.setKeyColumnIndex(keyColumnIndex);

		double[] lineValueArray = dataManager.getColumnAsDoubleArray(columnName);
		int[] sortedIndex = getSortedIndexOfDoubleArray(lineValueArray, true);
		
		for (int i = 0; i < findLineNumber; i++) {
			int maxIndex = i;
			if (maxMin == 0 || maxMin == 1) {
				if (maxIndex >= 0 && maxIndex < lineValueArray.length) {
					// Correspondingly, sortedIndex[maxIndex] is the index of the line in the dataManager
					// which the value of the column columnName is the i th maximal value in this group
					String[] lineStringArray = dataManager.getLineAsStringArray(sortedIndex[maxIndex]);
					resultManager.appendLine(lineStringArray);
				}
			}
		}
		for (int i = 0; i < findLineNumber; i++) {
			int minIndex = sortedIndex.length-i-1;
			if (maxMin == 0 || maxMin == -1) {
				if (minIndex >= 0 && minIndex < lineValueArray.length) {
					// Correspondingly, the content of lineIndexArray[minIndex] will be the index of the line in the dataManager
					// which the value of the column columnName is the i th minimal value in this group
					String[] lineStringArray = dataManager.getLineAsStringArray(sortedIndex[minIndex]);
					resultManager.appendLine(lineStringArray);
				}
			}
		}
		return resultManager;
	}
	
	/**
	 * �����ݱ� dataManager ������ columnName ȡ���ֵ����Сֵ�������С�����groupBy Ҳ�� dataManager �е��У������ڲ��� dataManager ����columnName ȡ
	 * ���ֵ��Сֵʱ��Ҫ���� groupBy ��һ�е����в�ͬ��ֵ���з��顣���� maxMin = 0 ��ʾ���ֵ����Сֵ��Ҫ��= 1 ��ʾֻҪ���ֵ��= -1 ��ʾֻҪ��Сֵ��
	 * ���� findLineNumber ��ʾҪ���Ҷ��ٸ���ֵ���������1��ʾֻҪ���ֵ��/����Сֵ������2���ʾ�������ֵ��/����Сֵ�⣬��Ҫ�δ�ֵ��/���Сֵ�ȵȡ����ص����ݱ��нṹ�� dataManager 
	 * ��ȫ��ͬ�� 
	 * <p>Note that the type of the column columnName must be double or int, and the type of the column groupBy must be String
	 * 
	 */
	public static DataTableManager findMaxMinLines(DataTableManager dataManager, String columnName, String groupBy, int findLineNumber, int maxMin) {
		if (groupBy == null) return findMaxMinLines(dataManager, columnName, findLineNumber, maxMin);
		
		String[] groupColumnStringArray = dataManager.getColumnAsStringArray(groupBy);
		Set<String> groupValueSet = new TreeSet<String>();
		// Find the different values in the column groupBy
		for (int i = 0; i < groupColumnStringArray.length; i++) {
			String groupValue = groupColumnStringArray[i];
			if (!groupValueSet.contains(groupValue)) groupValueSet.add(groupValue);
		}
		// Transform the groupValueSet to an array
		String[] groupValueArray = new String[groupValueSet.size()];
		int index = 0;
		for (String groupValue : groupValueSet) {
			groupValueArray[index] = groupValue;
			index = index + 1;
		}
		return findMaxMinLines(dataManager, columnName, groupBy, groupValueArray, findLineNumber, maxMin);
	}
	
	/**
	 * �����ݱ� dataManager ��ѡ��һЩ��ĳ����Χ֮����С���Χ�����ݱ� rangeIndicator ָ�������ݱ� rangeIndicator �����ٺ��� "Fields", "Min", "Max" �⼸�У�
	 * ������ "Fields"��ֵ��Ӧ���ݱ� dataManager ��������"Min"��"Max"��ֵ�� double ���͡�<b>���ݱ� rangeIndicator ��ÿһ����������ѡ�� dataManager �еĶ�Ӧ�У���
	 * rangeIndicator �� "Fields"��ָ����С�ڵ�����Сֵ����rangeIndicator�� "Min"��ָ��������ڵ������ֵ����rangeIndicator��"Max"��ָ������������</b>�����ݱ� 
	 * rangeIndicator ��������ݱ� dataManager �Ķ����ָ����Χ������ÿһ�ж�Ӧ���ݱ� dataManager ��һ�У�Ӧ�ڵķ�Χ������ rangeIndicator �Ķ������ָ���ķ�Χ 
	 * ����֮����<b>���ϵ</b>�������ݱ� dataManager �е�ĳһ�е�ֵ�� rangeIndicator ������ָ���ķ�Χ�⣬����вŻᱻѡ�С�ѡ�е��зŵ����صĽ����
	 * �ݱ����صĽ�����ݱ��� dataManager ����ȫ��ͬ�Ľṹ��ֻ������ѡ�е��ж��ѣ��� 
	 */
	public static DataTableManager selectDataLinesOutOfRange(DataTableManager dataManager, DataTableManager rangeIndicator) {
		DataLineDefaultFilter filter = new DataLineDefaultFilter();
		for (int lineIndex = 0; lineIndex < rangeIndicator.getLineNumber(); lineIndex++) {
			String columnName = rangeIndicator.getCellValueAsString(lineIndex, "Fields");
			String minValue =  rangeIndicator.getCellValueAsString(lineIndex, "Min");
			String maxValue =  rangeIndicator.getCellValueAsString(lineIndex, "Max");
			DataLineFilterIntervalCondition condition = new DataLineFilterIntervalCondition(columnName, minValue, maxValue);
			condition.setLogic(ConditionLogicKind.CLK_AND);
			condition.setType(ConditionDataTypeKind.CDTK_NUMERIC);
			condition.setRelation(ConditionRelationKind.CRK_OUT_INTERVAL);
			condition.setInterval(ConditionIntervalKind.CIK_OPEN_OPEN);
			filter.addCondition(condition);
		}
		
		DataTableManager resultManager = DataTableManager.createEmptyDataTableWithSameStructure(dataManager, "OutOfRange");
		for (int lineIndex = 0; lineIndex < dataManager.getLineNumber(); lineIndex++) {
			if (filter.accept(dataManager, lineIndex)) {
				String[] lineStringArray = dataManager.getLineAsStringArray(lineIndex);
				resultManager.appendLine(lineStringArray);
			}
		}
		return resultManager;
	}
	
	/**
	 * �����ݱ� dataManager ��ѡ��һЩ��ĳ����Χ֮����С���Χ�����ݱ� rangeIndicator ָ�������ݱ� rangeIndicator �����ٺ��� "Fields", "Min", "Max" �⼸�У�
	 * ������ "Fields"��ֵ��Ӧ���ݱ� dataManager ��������"Min"��"Max"��ֵ�� double ���͡�<b>���ݱ� rangeIndicator ��ÿһ����������ѡ�� dataManager �еĶ�Ӧ�У���
	 * rangeIndicator �� "Fields"��ָ����С�ڵ�����Сֵ����rangeIndicator�� "Min"��ָ��������ڵ������ֵ����rangeIndicator��"Max"��ָ������������</b>�����ݱ� 
	 * rangeIndicator ��������ݱ� dataManager �Ķ����ָ����Χ������ÿһ�ж�Ӧ���ݱ� dataManager ��һ�У�Ӧ�ڵķ�Χ������ rangeIndicator �Ķ������ָ���ķ�Χ 
	 * ����֮����<b>���ϵ</b>�������ݱ� dataManager �е�ĳһ�е�ֵ�� rangeIndicator ������ָ���ķ�Χ�⣬����вŻᱻѡ�С�
	 * <p>�����������ѡ�е��е��±������ɵ����� 
	 */
	public static int[] indexOfSelectedDataLinesOutOfRange(DataTableManager dataManager, DataTableManager rangeIndicator) {
		DataLineDefaultFilter filter = new DataLineDefaultFilter();
		for (int lineIndex = 0; lineIndex < rangeIndicator.getLineNumber(); lineIndex++) {
			String columnName = rangeIndicator.getCellValueAsString(lineIndex, "Fields");
			String minValue =  rangeIndicator.getCellValueAsString(lineIndex, "Min");
			String maxValue =  rangeIndicator.getCellValueAsString(lineIndex, "Max");
			DataLineFilterIntervalCondition condition = new DataLineFilterIntervalCondition(columnName, minValue, maxValue);
			condition.setLogic(ConditionLogicKind.CLK_AND);
			condition.setType(ConditionDataTypeKind.CDTK_NUMERIC);
			condition.setRelation(ConditionRelationKind.CRK_OUT_INTERVAL);
			condition.setInterval(ConditionIntervalKind.CIK_OPEN_OPEN);
			filter.addCondition(condition);
		}

		boolean[] lineSelected = new boolean[dataManager.getLineNumber()];
		int selectedCounter = 0;
		for (int lineIndex = 0; lineIndex < dataManager.getLineNumber(); lineIndex++) {
			if (filter.accept(dataManager, lineIndex)) {
				lineSelected[lineIndex] = true;
				selectedCounter++;
			}
			else lineSelected[lineIndex] = false;
		}
		if (selectedCounter > 0) {
			int[] resultIndexArray = new int[selectedCounter];
			int index = 0;
			for (int lineIndex = 0; lineIndex < dataManager.getLineNumber(); lineIndex++) {
				if (lineSelected[lineIndex] == true) {
					resultIndexArray[index] = lineIndex;
					index = index + 1;
				}
			}
			return resultIndexArray;
		} else return null;
	}

	/**
	 * �����ݱ� dataManager ��ѡ��һЩ��ĳ����Χ֮�ڵ��С���Χ�����ݱ� rangeIndicator ָ�������ݱ� rangeIndicator �����ٺ��� "Fields", "Min", "Max" �⼸�У�
	 * ������ "Fields"��ֵ��Ӧ���ݱ� dataManager ��������"Min"��"Max"��ֵ�� double ���͡�<b>���ݱ� rangeIndicator ��ÿһ����������ѡ�� dataManager �еĶ�Ӧ�У���
	 * rangeIndicator �� "Fields"��ָ�������ڵ�����Сֵ����rangeIndicator�� "Min"��ָ������С�ڵ������ֵ����rangeIndicator��"Max"��ָ������������</b>�����ݱ� 
	 * rangeIndicator ��������ݱ� dataManager �Ķ����ָ����Χ������ÿһ�ж�Ӧ���ݱ� dataManager ��һ�У�Ӧ�ڵķ�Χ������ rangeIndicator �Ķ������ָ���ķ�Χ 
	 * ����֮����<b>���ϵ</b>�����ǻ��ϵ�������ݱ� dataManager ��ĳһ����Ӧ���ж����� rangeIndicator ָ���ķ�Χ�ڣ����вŻᱻѡ�С�ѡ�е��зŵ����صĽ����
	 * �ݱ����صĽ�����ݱ��� dataManager ����ȫ��ͬ�Ľṹ��ֻ������ѡ�е��ж��ѣ��� 
	 */
	public static DataTableManager selectDataLinesInRange(DataTableManager dataManager, DataTableManager rangeIndicator) {
		DataLineDefaultFilter filter = new DataLineDefaultFilter();
		for (int lineIndex = 0; lineIndex < rangeIndicator.getLineNumber(); lineIndex++) {
			String columnName = rangeIndicator.getCellValueAsString(lineIndex, "Fields");
			String minValue =  rangeIndicator.getCellValueAsString(lineIndex, "Min");
			String maxValue =  rangeIndicator.getCellValueAsString(lineIndex, "Max");
			DataLineFilterIntervalCondition condition = new DataLineFilterIntervalCondition(columnName, minValue, maxValue);
			condition.setLogic(ConditionLogicKind.CLK_AND);
			condition.setType(ConditionDataTypeKind.CDTK_NUMERIC);
			condition.setRelation(ConditionRelationKind.CRK_IN_INTERVAL);
			condition.setInterval(ConditionIntervalKind.CIK_CLOSE_CLOSE);
			filter.addCondition(condition);
		}
		
		DataTableManager resultManager = DataTableManager.createEmptyDataTableWithSameStructure(dataManager, "InRange");
		for (int lineIndex = 0; lineIndex < dataManager.getLineNumber(); lineIndex++) {
			if (filter.accept(dataManager, lineIndex)) {
				String[] lineStringArray = dataManager.getLineAsStringArray(lineIndex);
				resultManager.appendLine(lineStringArray);
			}
		}
		return resultManager;
	}
	
	/**
	 * �����ݱ� dataManager ��ѡ��һЩ��ĳ����Χ֮�ڵ��С���Χ�����ݱ� rangeIndicator ָ�������ݱ� rangeIndicator �����ٺ��� "Fields", "Min", "Max" �⼸�У�
	 * ������ "Fields"��ֵ��Ӧ���ݱ� dataManager ��������"Min"��"Max"��ֵ�� double ���͡�<b>���ݱ� rangeIndicator ��ÿһ����������ѡ�� dataManager �еĶ�Ӧ�У���
	 * rangeIndicator �� "Fields"��ָ�������ڵ�����Сֵ����rangeIndicator�� "Min"��ָ������С�ڵ������ֵ����rangeIndicator��"Max"��ָ������������</b>�����ݱ� 
	 * rangeIndicator ��������ݱ� dataManager �Ķ����ָ����Χ������ÿһ�ж�Ӧ���ݱ� dataManager ��һ�У�Ӧ�ڵķ�Χ������ rangeIndicator �Ķ������ָ���ķ�Χ 
	 * ����֮����<b>���ϵ</b>�����ǻ��ϵ�������ݱ� dataManager ��ĳһ����Ӧ���ж����� rangeIndicator ָ���ķ�Χ�ڣ����вŻᱻѡ�С�ѡ�е��зŵ����صĽ����
	 * �ݱ����صĽ�����ݱ��� dataManager ����ȫ��ͬ�Ľṹ��ֻ������ѡ�е��ж��ѣ��� 
	 * <p>�����������ѡ�е��е��±������ɵ����� 
	 */
	public static int[] indexOfSelectedDataLinesInRange(DataTableManager dataManager, DataTableManager rangeIndicator) {
		DataLineDefaultFilter filter = new DataLineDefaultFilter();
		for (int lineIndex = 0; lineIndex < rangeIndicator.getLineNumber(); lineIndex++) {
			String columnName = rangeIndicator.getCellValueAsString(lineIndex, "Fields");
			String minValue =  rangeIndicator.getCellValueAsString(lineIndex, "Min");
			String maxValue =  rangeIndicator.getCellValueAsString(lineIndex, "Max");
			DataLineFilterIntervalCondition condition = new DataLineFilterIntervalCondition(columnName, minValue, maxValue);
			condition.setLogic(ConditionLogicKind.CLK_AND);
			condition.setType(ConditionDataTypeKind.CDTK_NUMERIC);
			condition.setRelation(ConditionRelationKind.CRK_IN_INTERVAL);
			condition.setInterval(ConditionIntervalKind.CIK_CLOSE_CLOSE);
			filter.addCondition(condition);
		}

		boolean[] lineSelected = new boolean[dataManager.getLineNumber()];
		int selectedCounter = 0;
		for (int lineIndex = 0; lineIndex < dataManager.getLineNumber(); lineIndex++) {
			if (filter.accept(dataManager, lineIndex)) {
				lineSelected[lineIndex] = true;
				selectedCounter++;
			}
			else lineSelected[lineIndex] = false;
		}
		if (selectedCounter > 0) {
			int[] resultIndexArray = new int[selectedCounter];
			int index = 0;
			for (int lineIndex = 0; lineIndex < dataManager.getLineNumber(); lineIndex++) {
				if (lineSelected[lineIndex] == true) {
					resultIndexArray[index] = lineIndex;
					index = index + 1;
				}
			}
			return resultIndexArray;
		} else return null;
	}

	/**
	 * �����ݱ������ת����ԭ����������Ϊ��һ�У���ԭ���ĵ�һ����Ϊ���� 
	 */
	public static DataTableManager exchangeRowAndColumn(DataTableManager manager) {
		DataTableManager result = new DataTableManager("result");
		
		String[] columnNames = manager.getColumnNameArray();
		String[] columnValueArray = manager.getColumnAsStringArray(columnNames[0]);
		String[] resultColumnNames = new String[columnValueArray.length+1];
		resultColumnNames[0] = columnNames[0];
		for (int i = 1; i < resultColumnNames.length; i++) resultColumnNames[i] = columnValueArray[i-1];
		result.setColumnNames(resultColumnNames);
		
		for (int i = 1; i < columnNames.length; i++) {
			columnValueArray = manager.getColumnAsStringArray(columnNames[i]);
			String[] lineArray = new String[columnValueArray.length+1];
			lineArray[0] = columnNames[i];
			for (int j = 1; j < lineArray.length; j++) lineArray[j] = columnValueArray[j-1];
			result.appendLine(lineArray);
		}
		
		return result;
	}
	
	/**
	 * �����ݱ� table ������д�ɿ����� LaTex ��Ϊ�����ʾ����ʽ������ columnNames ����ѡ�� table �е��С�mergeLine ��ʾ table �еļ������ݺϲ�Ϊ LaTex �б���һ�н��д�ӡ����
	 * ѡ��� table ���бȽ��ٵ�ʱ����Լ������ݴ�ӡ�� LaTex����е�һ�С� 
	 */
	public static void writeDataLinesAsLatexTableLines(PrintWriter writer, DataTableManager table, String[] columnNames, int mergeLine) throws IOException {
		if (columnNames == null) columnNames = table.getColumnNameArray();
		if (mergeLine <= 0) mergeLine = 1;
		
		writer.print("\\begin{tabular}{");
		for (int i = 0; i < mergeLine; i++) {
			for (int colIndex = 0; colIndex < columnNames.length; colIndex++) {
				if (colIndex == 0 && i == 0) writer.print("c");
				else writer.print("|c");
			}
		}
		writer.println("}");
		
		writer.print("\\hline");
		for (int i = 0; i < mergeLine; i++) {
			for (int colIndex = 0; colIndex < columnNames.length; colIndex++) {
				if (colIndex == 0 && i == 0) writer.print("{\\heiti " + columnNames[colIndex] + "} ");
				else writer.print(" & " + "{\\heiti " + columnNames[colIndex] + "} ");
			}
		}
		writer.println(" \\\\");
		
		int index = 0;
		while (index < table.getLineNumber()) {
			writer.print("\\hline ");
			for (int i = 0; i < mergeLine; i++) {
				String[] lineStringArray = null;
				if (index < table.getLineNumber()) {
					lineStringArray = table.getLineAsStringArray(index, columnNames);
				}
				for (int colIndex = 0; colIndex < columnNames.length; colIndex++) {
					String columnString = " ";
					if (lineStringArray != null) columnString = lineStringArray[colIndex];
					
/*					if (colIndex == 0) {
						if (i == 0) writer.print("\\pname{" + columnString + "} ");
						else writer.print(" & \\pname{" + columnString + "} ");
					} else writer.print(" & " + columnString);*/
					if (colIndex == 0) {
					if (i == 0) writer.print(columnString);
					else writer.print(" & " + columnString);
				} else writer.print(" & " + columnString);
				}
				index = index + 1;
			}
			writer.println(" \\\\");
		}
		
		writer.println("\\hline");
		writer.println("\\end{tabular}");
	}
	
	public static void writeDataLinesAsLatexTableLines(PrintWriter writer, DataTableManager table, String[] columnNames) throws IOException {
		writeDataLinesAsLatexTableLines(writer, table, columnNames, 1);
	}


}

class ValueIndexPair {
	double value;
	int index;
	
	public ValueIndexPair(double value, int index) {
		this.value = value;
		this.index = index;
	}
}
