package nameTable.nameReference;

import java.util.ArrayList;
import java.util.List;

import nameTable.nameDefinition.TypeDefinition;
import nameTable.nameScope.NameScope;
import sourceCodeAST.SourceCodeLocation;

/**
 * A class represents a union type reference.
 * 
 * @author Zhou Xiaocong
 * @since 2016��11��7��
 * @version 1.0
 *
 */
public class UnionTypeReference extends TypeReference {
	private List<TypeReference> typeList = null;

	public UnionTypeReference(String name, SourceCodeLocation location, NameScope scope) {
		super(name, location, scope);
		typeKind = TypeReferenceKind.TRK_UNION;
	}

	/**
	 * @param other
	 */
	public UnionTypeReference(UnionTypeReference other) {
		super(other);
		typeList = new ArrayList<TypeReference>();
		typeList.addAll(other.typeList);
		typeKind = other.typeKind;
	}

	public void addType(TypeReference type) {
		if (typeList == null) typeList = new ArrayList<TypeReference>();
		typeList.add(type);
	}
	
	public List<TypeReference> getTypeList() {
		return typeList;
	}
	
	/**
	 * Return sub-reference in a name reference
	 */
	@Override
	public List<NameReference> getSubReferenceList() {
		List<NameReference> result = new ArrayList<NameReference>();
		if (typeList != null) {
			for (TypeReference type : typeList) result.add(type);
		}
		return result;
	}

	@Override
	public boolean resolveBinding() {
		if (definition != null) return true;
		
		// Resolve all types in typeList
		if (typeList != null && typeList.size() > 0) {
			TypeDefinition firstResolvedType = null;
			for (TypeReference type : typeList) {
				type.resolveBinding();
				if (type.isResolved() && firstResolvedType == null) {
					firstResolvedType = (TypeDefinition)type.getDefinition();
				}
			}
			bindTo(firstResolvedType);
		}
		return isResolved();
	}
}
