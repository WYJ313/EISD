package graph.dependenceGraph.dependenceFinder;

import java.util.List;

import nameTable.NameTableManager;
import nameTable.nameDefinition.DetailedTypeDefinition;
import nameTable.nameDefinition.MethodDefinition;
import nameTable.nameReference.NameReference;
import nameTable.nameScope.NameScope;
import nameTable.nameScope.NameScopeKind;

/**
 * @author Zhou Xiaocong
 * @since 2017��1��8��
 * @version 1.0
 *
 */
public class NameBasedMethodCallTypeFinder extends NameBasedDependenceFinder {

	public NameBasedMethodCallTypeFinder(NameTableManager tableManager) {
		super(tableManager);
	}

	@Override
	public boolean hasRelation(NameScope start, NameScope end) {
		if (start.getScopeKind() != NameScopeKind.NSK_METHOD) return false;
		if (end.getScopeKind() != NameScopeKind.NSK_DETAILED_TYPE) return false;

		DetailedTypeDefinition type = (DetailedTypeDefinition)end;
		List<MethodDefinition> methodList = type.getMethodList();
		if (methodList == null) return false;
		
		for (NameReference reference : referenceList) {
			List<NameReference> leafReferenceList = reference.getReferencesAtLeaf();
			for (NameReference leafReference : leafReferenceList) {
				for (MethodDefinition method : methodList) {
					if (leafReference.getDefinition() == method) return true;
				}
			}
		}
		return false;
	}

}
