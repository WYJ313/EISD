package softwareMeasurement.measure;

import java.io.PrintWriter;
import java.util.List;

import nameTable.nameDefinition.MethodDefinition;
import nameTable.nameScope.NameScope;

/**
 * @author Zhou Xiaocong
 * @since 2015��7��11��
 * @version 1.0
 */
public class MethodMeasureDistribution extends NameScopeMeasureDistribution {

	public MethodMeasureDistribution(String identifier) {
		super(identifier);
	}

	public MethodMeasureDistribution(SoftwareMeasure measure) {
		super(measure);
	}

	public MethodDefinition[] getMethodArray() {
		return (MethodDefinition[])scopeArray;
	}
	
	public MethodDefinition getMethod(int index) {
		return (MethodDefinition)getNameScope(index);
	}
	
	/**
	 * Get the index of the give system scope. If we can not find the scope, return -1.
	 */
	public int getMethodIndex(MethodDefinition scope) {
		return getNameScopeIndex(scope);
	}
	
	/**
	 * Set a system scope array as the name scope array
	 */
	public void setMethodArray(MethodDefinition[] array) {
		scopeArray = array;
	}

	/**
	 * Set a system scope list as the name scope array
	 */
	public void setMethodList(List<MethodDefinition> scopes) {
		scopeArray = new NameScope[scopes.size()];
		for (int index = 0; index < scopes.size(); index++) {
			scopeArray[index] = scopes.get(index);
		}
	}

	
	/**
	 * Set the value for the given system scope (i.e. the parameter scope), and set its value to be usable
	 */
	public boolean setValue(MethodDefinition scope, double value) {
		int index = getNameScopeIndex(scope);
		if (index < 0) return false;
		return setValue(index, value);
	}
	
	/**
	 * Print the distribution to one or two rows of table. If the boolean parameter printScope is false, only print the value,
	 * otherwise, print the name scope in a row, and the value in another row. Anyway, the measure identifier will be
	 * in the first column of the value row. The table symbol is used to as the splitter of the columns. 
	 */
	public void printToRow(PrintWriter writer, boolean printScope) {
		if (printScope == true) {
			writer.print("Method\t");
			for (int index = 0; index < scopeArray.length; index++) {
				writer.print(scopeArray[index].getScopeName() + "\t");
			}
			writer.println();
		}
		writer.print(identifier + "\t");
		for (int index = 0; index < scopeArray.length; index++) {
			if (valueUsableArray[index] == true) writer.print(valueArray[index] + "\t");
			else writer.print("N.A.\t");
		}
		writer.println();
	}

	/**
	 * Print the distribution to one or two columns of table. If the boolean parameter printScope is false, only print the value,
	 * otherwise, print the name scope in a column, and the value in another column. Anyway, the measure identifier will be
	 * in the first row of the value column. The table symbol is used to as the splitter of the columns. 
	 */
	public void printToColumn(PrintWriter writer, boolean printScope) {
		if (printScope == true) {
			writer.println("Method\t" + identifier);
		} else writer.println(identifier);
		for (int index = 0; index < scopeArray.length; index++) {
			if (printScope == true) writer.print(scopeArray[index].getScopeName() + "\t");
			if (valueUsableArray[index] == true) writer.println(valueArray[index]);
			else writer.println("N.A.");
		}
	}

}
