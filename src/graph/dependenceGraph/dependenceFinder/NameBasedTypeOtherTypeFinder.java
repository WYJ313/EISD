package graph.dependenceGraph.dependenceFinder;

import java.util.List;

import nameTable.NameTableManager;
import nameTable.nameDefinition.DetailedTypeDefinition;
import nameTable.nameReference.NameReference;
import nameTable.nameScope.NameScope;
import nameTable.nameScope.NameScopeKind;

/**
 * @author Zhou Xiaocong
 * @since 2017��1��8��
 * @version 1.0
 *
 */
public class NameBasedTypeOtherTypeFinder extends NameBasedDependenceFinder {

	public NameBasedTypeOtherTypeFinder(NameTableManager tableManager) {
		super(tableManager);
	}

	@Override
	public boolean hasRelation(NameScope start, NameScope end) {
		if (start.getScopeKind() != NameScopeKind.NSK_DETAILED_TYPE) return false;
		if (end.getScopeKind() != NameScopeKind.NSK_DETAILED_TYPE) return false;

		DetailedTypeDefinition type = (DetailedTypeDefinition)end;
		
		for (NameReference reference : referenceList) {
			List<NameReference> leafReferenceList = reference.getReferencesAtLeaf();
			for (NameReference leafReference : leafReferenceList) {
				if (leafReference.getDefinition() == type) return true;
			}
		}
		return false;
	}

}
