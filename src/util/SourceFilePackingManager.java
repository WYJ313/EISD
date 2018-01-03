package util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * ����һ��ϵͳ�µ�����Դ�ļ���Ŀ����Ҫ����һ����Դ��Ŀ�µ����п����ڷ������ļ���Ŀǰֻ��.javaԴ�ļ�����֧�ֽ���Щ�ļ����г��������������������Ŀ¼����
 * �Լ�Ѱ�Ҹ��õ�ϵͳ��Ŀ¼�ļ�������JavaԴ�ļ��ĳ������ӦĿ¼����ʱ�ܶ࿪Դ��Ŀ���к������Ŀ¼�ṹ������ʵ������ЩĿ¼ֻ��һ����Ŀ¼��û������Ŀ¼��
 * ����JEdit��org.gjt.sp.jedit�������ʵ��JEdit��srcĿ¼��ֻ��org��Java�ļ�����orgĿ¼��ֻ��gjt�����Ŀ¼����gjt��ֻ��sp�����Ŀ¼��sp
 * ��ֻ��jedit�����Ŀ¼�����ϵͳĿ¼��src��ʼ������ô����ʱÿ�������������"org.gjt.sp.jedit"��������ǳ��˷Ѵ洢�ռ䣬����������� jedit 
 * ��ʼ������������ǵķ���û��ʲôӰ�죬�㹻���ֲ�ͬ�ĳ������Դ�ļ������뵥Ԫ�������ɽ�ʡ������ʱ�Ĵ洢�ռ䣩
 * 
 * @author Zhou Xiaocong
 * @version 1.0
 * @since 2015/09/01
 *
 */
public class SourceFilePackingManager {
	public static final String pathSeparator = "\\";
	
	private String systemId = null;			// ÿ����Դ��ĿӦ����һ����ϵͳ����
	private String versionId = null;		// ϵͳ�İ汾�š�����ÿ����Դϵͳ��ϵͳ����+�汾�� Ӧ��Ψһ
	private String path = null;				// ϵͳ��Ŀ¼
	
	private String betterPath = null;		// ��������̽�鵽�Ŀ��Ը�����Ϊϵͳ��Ŀ¼��Ŀ¼��
	
	// ������Ǻ͹���Ŀ¼��Դ�ļ��ṹ�����ĸ�
	private SourceFilesStructure pathTreeRoot = null; 

	public SourceFilePackingManager(String systemId, String versionId, String root) {
		this.systemId = systemId;
		this.versionId = versionId;
		this.path = root;
	}

	public String getSystemId() {
		return systemId;
	}

	public String getVersionId() {
		return versionId;
	}

	public String getRootPath() {
		return path;
	}

	public String getBetterRootPath() {
		return betterPath;
	}
	
	/**
	 * �ṩһ�����߷�����ֱ�Ӹ���ĳ��ϵͳĿ¼path��̽��������Ƿ��и��õģ�����Ϊϵͳ��Ŀ¼��Ŀ¼������ null ��ʾ������Ŀ¼��û��
	 * �κ���Ҫ��Դ�ļ����������û�и��õ�Ŀ¼����᷵�� path
	 */
	public static String findBetterSystemPath(String systemPath) {
		// ����һ����ʱ����̽��Ŀ¼�Ĺ���������
		SourceFilePackingManager manager = new SourceFilePackingManager("", "", systemPath);
		manager.buildSourceFilesStructure();
		manager.findBetterRootPath();
		return manager.getBetterRootPath();
	}
	
	/**
	 * ��̽�����ϵͳԴĿ¼ sourceSystemPath ���Ƿ��и��õ�Ŀ¼betterPath֮���ٽ�betterPath���µ�����Դ�ļ�������Ŀ��Ŀ¼ destPath
	 * �÷��������ҵ��Ŀ��ܸ��õ�ϵͳĿ¼���Ա��������Ǵ��ĸ�Դ�ļ�Ŀ¼���п����ģ�
	 */
	public static String betterCopySystemSourceFiles(String sourceSystemPath, String destPath) throws IOException {
		String betterPath = findBetterSystemPath(sourceSystemPath);
		if (betterPath == null) return null;
		
		File start = new File(betterPath);
		if (start.isFile()) {
			if (!isSourceFile(start)) return betterPath;	// ��������Ҫ��Դ�ļ��Ͳ������ˣ�
			
			File dest = new File(destPath);
			if (dest.isFile()) copyBinaryFile(start, dest);
			else {
				String destFileName = destPath + start.getName();
				dest = new File(destFileName);
				copyBinaryFile(start, dest);
			}
			return betterPath;
		}
		
		File dest = new File(destPath);
		if (dest.isFile()) {
			throw new IOException("The destination [" + destPath + "] is not a path!");
		}
		if (!dest.exists()) dest.mkdirs();		// Ŀ��Ŀ¼�����ڵĻ���������Ŀ¼�����������Ҫ�����ĸ�Ŀ¼��
		copyAllSourceFiles(betterPath, destPath);	// ��Ŀ¼ betterPath�µ�����Դ�ļ�������Ŀ¼ destPath
		return betterPath;
	}
	
	/**
	 *	�ڴ�����ʾԴ�ļ��ṹ�ĸ��ڵ�֮�󣬾Ϳ���̽���Ƿ��и��õ�ϵͳ��Ŀ¼ 
	 */
	public void findBetterRootPath() {
		betterPath = path;	// ��ԭ���� path ��ʼ
		SourceFilesStructure node = pathTreeRoot;
		while (node != null) {
			if (!node.hasChild()) {
				// ��ǰ�ڵ�û�ж��ӣ����ҵ�ǰ�ڵ�û������Ҫ��Դ�ļ����������ϵͳĿ¼�¸���û���κ���Ҫ��Դ�ļ�
				if (!node.hasSourceFile()) betterPath = null;
				
				System.out.println("Return null in !node.hasChild()!");
				return;	
			}
			ArrayList<SourceFilesStructure> children = node.getChildren();
			SourceFilesStructure hasSourceFileChild = null;
			for (int i = 0; i < children.size(); i++) {
				SourceFilesStructure child = children.get(i);
				if (child.hasSourceFile()) {
					if (hasSourceFileChild == null) hasSourceFileChild = child;
					else {
						System.out.println("Return [" + betterPath + "] in hasSourceFileChild != null!");
						return; // ��ǰ�ڵ���������Ŀ¼������Ҫ��Դ�ļ���̽������������ betterPath ��
					}
				}
			}
			if (hasSourceFileChild == null) {
				// ��ǰ�ڵ�����ж��Ӷ�û������Ҫ��Դ�ļ�����Ҳ��������ϵͳĿ¼�¸���û���κ���Ҫ��Դ�ļ�
				betterPath = null;

				System.out.println("Return null in hasSourceFileChild == null!");
				return;
			} else {
				// ��ǰ�ڵ����ҽ���һ������������Ҫ��Դ�ļ���ע������Լ��ϵͳĿ¼Ҫ�� pathSeparator ����
				betterPath = betterPath + hasSourceFileChild.getPathName() + pathSeparator;
				node = hasSourceFileChild;		// ���������������һ����̽��
			}
		}
	}
	
	/**
	 * �����õ� path ��ʼ�������ڲ��� buildSourceFilesStructure(File) ����������ʾ�ļ��ṹ���������������¼
	 * �� pathTreeRoot��ע�⣬��� path ��Ӧ�����ļ������Ҳ�����Ҫ������Դ�ļ����� pathTreeRoot �� null��
	 */
	public void buildSourceFilesStructure() {
		File start = new File(path);

		if (start.isFile()) {
			// ϵͳĿ¼ path ����һ��Ŀ¼�����ǵ����ļ�����ʱֻ�и��ļ�����Ҫ��Դ�ļ�ʱ�� pathTreeRoot �Ų��� null
			if (isSourceFile(start)) {
				// ֻ���ڸ��ļ�����Ҫ��Դ�ļ�ʱ�Ŵ����ڵ㣬ʹ���ļ���������
				pathTreeRoot = new SourceFilesStructure(start.getName());				
				pathTreeRoot.setFlag(true);
			}  else pathTreeRoot = null;
			return;
		}
		// ע��������жϣ��Լ������� buildSourceFilesStructure(File)ע������˵�ĵ�һ�����
		pathTreeRoot = buildSourceFilesStructure(start);
	}
	
	/**
	 * ��start Ϊ��㣬������ʾ�ļ��ṹ���������������������
	 * (1) ��� start ��Ӧһ���ļ�����Ŀ¼��������ļ�����Ҫ��Դ�ļ�ʱ�����ض�Ӧ�õ����ļ��Ľڵ㣨��������Ĳ��ԣ��������Ӧ��
	 * 		ֻ�е������ĸ�Ŀ¼���ǵ����ļ�ʱ�Ż���֣������򷵻� null��
	 * (2) ��� start ��һ��Ŀ¼���������ٺ�����Ŀ¼����ʱ�����Ŀ¼��������һ����Ҫ��Դ�ļ����򷵻ص�һ����ʾ����ҪԴ�ļ� ��flag��Ϊ true����
	 * 		�Ľڵ㣻�����Ŀ¼��û���κ���Ҫ��Դ�ļ����򷵻�һ����ʾû����ҪԴ�ļ���flag��Ϊfalse���Ľڵ㡣���صĽڵ�����û�ж��ӣ�children��Ϊ null��
	 * (3) ��� start ��һ��Ŀ¼���������ٺ���һ����Ŀ¼������˸��ݸ�Ŀ¼�µ��ļ����� flag ��֮�⣬��Ҫ������Ŀ¼�е�������� flag ��ֻҪ����
	 * 		һ������Ҫ��Դ�ļ�����flag����Ϊ true ����ʱ���صĽڵ����ǻ��һ���������ӽڵ㡣
	 * 
	 */
	private SourceFilesStructure buildSourceFilesStructure(File start) {
		SourceFilesStructure node = null;
		
		if (start.isFile()) {
			// start����һ��Ŀ¼������һ���ļ�
			if (isSourceFile(start)) {
				// ֻ���ڸ��ļ�����Ҫ��Դ�ļ�ʱ�Ŵ�����Ӧ�Ľڵ㣬ʹ���ļ���������
				node = new SourceFilesStructure(start.getName());				
				node.setFlag(true);
			}  // ���򲻴���
			return node;
		}
		
		node = new SourceFilesStructure(start.getName());	// ʹ�ü��������ڵ�
		File[] filesInPath = start.listFiles(new JavaSourceFileFilter());
		if (filesInPath == null) return node;		// ���Ŀ¼��û���κ���Ҫ��Դ�ļ���Ҳû����Ŀ¼
		
		for (int index = 0; index < filesInPath.length; index++) {
			File current = filesInPath[index];
			if (current.isFile()) {
				// ���ڵ����ļ����ǲ��ٵݹ���ñ�����ȥ������һ����Դ�ļ�Ŀ¼�ṹ
				if (isSourceFile(current)) node.setFlag(true);
			} else {
				// �ݹ���ñ�����������ʾ��Ŀ¼��Դ�ļ��ṹ�ڵ�
				SourceFilesStructure child = buildSourceFilesStructure(current);
				if (child != null) {
					node.addChild(child);
					if (child.hasSourceFile()) node.setFlag(true);
				}
			}
		}
		
		return node;
	}
	
	static class JavaSourceFileFilter implements FileFilter {
		@Override
		public boolean accept(File pathname) {
			if (pathname.isDirectory()) return true;
			if (isSourceFile(pathname)) return true;
			return false;
		}
	}
	
	private static boolean isSourceFile(File file) {
		if (file.isFile() && file.getName().endsWith(".java")) return true;
		return false;
	}
	
	/**
	 * ʹ�������ֽڵ��ļ��������������ļ�
	 */
	public static void copyBinaryFile(File inFile, File outFile) throws IOException {
		System.out.println("Copy file [" + inFile.getAbsolutePath() + "] to [" + outFile.getAbsolutePath() + "] ....");
		
		FileInputStream in = new FileInputStream(inFile);
		FileOutputStream out = new FileOutputStream(outFile);
		
		final int bufferLength = 4096;
		byte[] buffer = new byte[bufferLength];
		while (true) {
			int readBytes = in.read(buffer);
			if (readBytes == -1) break;
			out.write(buffer, 0, readBytes);
		}
		in.close();
		out.close();

		System.out.println("Copy end ....");
	}

	/**
	 * ����ĳ��Ŀ¼�µ�����Դ�ļ���Ŀ��Ŀ¼
	 */
	private static boolean copyAllSourceFiles(String sourcePath, String destPath) throws IOException {
		boolean indeedCopyFile = false;
		
		File source = new File(sourcePath);
		File[] sourceFiles = source.listFiles(new JavaSourceFileFilter());
		if (sourceFiles == null) return indeedCopyFile;
		
		for (int i = 0; i < sourceFiles.length; i++) {
			File sourceFile = sourceFiles[i];
			if (sourceFile.isFile()) {
				String sourceName = sourcePath + sourceFile.getName();
				String destName = destPath + sourceFile.getName();
				copyBinaryFile(new File(sourceName), new File(destName));
				indeedCopyFile = true;
			} else {
				String sourceChildPath = sourcePath + sourceFile.getName() + pathSeparator;
				String destChildPath = destPath +  sourceFile.getName() + pathSeparator;
				File dest = new File(destChildPath);
				if (!dest.exists()) dest.mkdir();
				
				boolean childCopyFile = copyAllSourceFiles(sourceChildPath, destChildPath);
				if (!childCopyFile) {
					// ����Ŀ¼ʵ����û�п����κ�Դ�ļ���ɾ������Ŀ¼����������̫�����Ŀ¼��
					dest.delete();
				} else indeedCopyFile = true;
			}
		}
		return indeedCopyFile;
	}

	
}

/**
 * �������Դ�ļ�Ŀ¼�ṹ���Ƿ�����Ҫ��Դ�ļ�
 * 
 * @author Zhou Xiaocong
 * @version 1.0
 * @since 2015/09/01
 *
 */
class SourceFilesStructure {
	String pathName = null;								// ��Ӧ��Ŀ¼����������
	boolean flag = false;								// ��Ƕ�Ӧ��Ŀ¼�Ƿ�����Ҫ��Դ�ļ�
	ArrayList<SourceFilesStructure> children = null;	// ��Ŀ¼�Ľṹ
	
	public SourceFilesStructure(String pathName) {
		this.pathName = pathName;
		children = new ArrayList<SourceFilesStructure>();
	}

	public String getPathName() {
		return pathName;
	}

	public boolean hasSourceFile() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	
	public ArrayList<SourceFilesStructure> getChildren() {
		return children;
	}
	
	public void addChild(SourceFilesStructure child) {
		children.add(child);
	}
	
	public boolean hasChild() {
		if (children == null) return false;
		if (children.size() <= 0) return false;
		return true;
	}
}
