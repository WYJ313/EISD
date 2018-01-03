package gui.softwareMeasurement.structureBrowser;

import nameTable.NameTableManager;
import nameTable.creator.NameDefinitionCreator;
import nameTable.creator.NameTableCreator;
import softwareMeasurement.SystemScopeMeasurement;
import softwareStructure.SoftwareStructManager;
import sourceCodeAST.SourceCodeFileSet;

public class SystemScopeMeasureTest {

	
	public static void main(String args[]) {
		String path = "C:\\Users\\comicgirl\\Desktop\\src(20150706)";
		SourceCodeFileSet parser = new SourceCodeFileSet(path);
		NameTableCreator creator = new NameDefinitionCreator(parser);
		NameTableManager manager = creator.createNameTableManager();
		SoftwareStructManager structManager = new SoftwareStructManager(manager);
		SystemScopeMeasurement sysMeasure = new SystemScopeMeasurement(manager.getSystemScope(), structManager);
		System.out.println(sysMeasure.getMeasureByIdentifier("MTHD"));
		System.out.println(sysMeasure.getMeasureByIdentifier("BYTE"));
		System.out.println(sysMeasure.getMeasureByIdentifier("STMN"));
		System.out.println(sysMeasure.getMeasureByIdentifier("WORD"));
		System.out.println(sysMeasure.getMeasureByIdentifier("CHAR"));
//		System.out.println(sysMeasure.getMeasureList(getAvailableSoftwareSizeMeasureList()));
	}
	
/*	private static List<SoftwareMeasure> getAvailableSoftwareSizeMeasureList() {
		SoftwareMeasure[] measures = {
				new SoftwareMeasure(SoftwareMeasureIdentifier.FILE),
				new SoftwareMeasure(SoftwareMeasureIdentifier.PKG),
				new SoftwareMeasure(SoftwareMeasureIdentifier.CLS),
				new SoftwareMeasure(SoftwareMeasureIdentifier.INTF),
				new SoftwareMeasure(SoftwareMeasureIdentifier.ENUM),

				new SoftwareMeasure(SoftwareMeasureIdentifier.ELOC),
				new SoftwareMeasure(SoftwareMeasureIdentifier.BLOC),
				new SoftwareMeasure(SoftwareMeasureIdentifier.CLOC),
				new SoftwareMeasure(SoftwareMeasureIdentifier.NLOC),
				new SoftwareMeasure(SoftwareMeasureIdentifier.LOC),
				new SoftwareMeasure(SoftwareMeasureIdentifier.LOPT),
				
//				new SoftwareMeasure(SoftwareMeasureIdentifier.ALLMTHD),
				new SoftwareMeasure(SoftwareMeasureIdentifier.MTHD),
//				new SoftwareMeasure(SoftwareMeasureIdentifier.IHMTHD),
//				new SoftwareMeasure(SoftwareMeasureIdentifier.NEWMTHD),
//				new SoftwareMeasure(SoftwareMeasureIdentifier.OVMTHD),
//				new SoftwareMeasure(SoftwareMeasureIdentifier.IMPMTHD),
//				new SoftwareMeasure(SoftwareMeasureIdentifier.ALLFLD),
//				new SoftwareMeasure(SoftwareMeasureIdentifier.IHFLD),
				new SoftwareMeasure(SoftwareMeasureIdentifier.PARS),
				new SoftwareMeasure(SoftwareMeasureIdentifier.LOCV),
				new SoftwareMeasure(SoftwareMeasureIdentifier.STMN),

				new SoftwareMeasure(SoftwareMeasureIdentifier.BYTE),
				new SoftwareMeasure(SoftwareMeasureIdentifier.WORD),
				new SoftwareMeasure(SoftwareMeasureIdentifier.CHAR),
		};
		List<SoftwareMeasure> measureList = new ArrayList<SoftwareMeasure>();
		for (int index = 0; index < measures.length; index++) measureList.add(measures[index]);
		return measureList;
	}
*/	
}
