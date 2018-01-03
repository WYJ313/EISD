package gui.softwareMeasurement.codeBrowser;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JTextPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import gui.softwareMeasurement.mainFrame.JsMetricFrame;

/**
 * ����eclipse��Ĭ��ϰ��չʾԴ������ı���
 * 
 * @author Administrator
 */
public class JSrcViewer extends JTextPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1651982513552805115L;
	private File file;
	private String keywords[] = { "abstract", "assert", "boolean", "break",
			"byte", "case", "catch", "char", "class", "const", "continue",
			"default", "do", "double", "else", "enum", "extends", "final",
			"finally", "float", "for", "goto", "if", "implements", "import",
			"instanceof", "int", "interface", "long", "native", "new",
			"package", "private", "protected", "public", "return", "strictfp",
			"short", "static", "super", "switch", "synchronized", "this",
			"throw", "throws", "transient", "try", "void", "volatile", "while",
			"null" };

	boolean blockComment = false;
	boolean docComment = false;
	boolean isEnd = false;
	int lineCount = 1;
	int col = -1;
	int row = -1;
	// ����Code Browserʹ��ͬһ�������Ի���
	public static SearchDialog searcher;
		
	
	boolean isFocused = false;// ����Ƿ�Ϊ��꽹��
	
	public JSrcViewer(File file) {
		super();
		setEditable(false);
		this.file = file;
		init();
		setCaretPosition(0);
//		this.addKeyListener(new KeyAdapter() {
//			public void keyPressed(KeyEvent e) {
//				if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_F) {
//					if (searcher == null) {
//						searcher = new SearchDialog(JSrcViewer.this);
//					} else {
//						searcher.setVisible(true);
//					}
//				}
//			}
//		});
	}

	public JSrcViewer(String filePath) {
		this(new File(filePath));
	}

	private void init() {
		// ��������
		setFont(new Font("Courier New", Font.PLAIN, 15));
		initFileLineCount();
		try {
			StyledDocument doc = getStyledDocument();
			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				int lastOffset = doc.getLength();
				// �Ȳ�������
				doc.insertString(lastOffset, line + "\r\n", null);
				// �����޸���ʽ
				modifyStyle(doc, line, lastOffset);
			}
			scanner.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		addFocusListener(new FocusAdapter() {

			@Override
			public void focusGained(FocusEvent e) {
				setCursor(new Cursor(Cursor.TEXT_CURSOR));
				getCaret().setVisible(true);
				showRowAndCol();
			}
		});
		
		addCaretListener(new CaretListener() {

			@Override
			public void caretUpdate(CaretEvent e) {
				int caretPosition = getCaretPosition();
				Element root = getDocument().getDefaultRootElement();

				row = root.getElementIndex(caretPosition) + 1;

				int line = root.getElementIndex(caretPosition);
				int lineStart = root.getElement(line).getStartOffset();

				col = caretPosition - lineStart + 1;
				try {
					// ���лس����������кż��㵱��
					int caretPosition1 = getCaretPosition() - 1;
					if (caretPosition1 >= 0) {
						String str = getText(caretPosition1, 1);
						if ("\r".equals(str) || "\n".equals(str)) {
							col = col - 1;
							col = col > 0 ? col : 1;
						}
					}
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}
				showRowAndCol();
			}

		});

		addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				hideRowAndCol();
				// �жϱ�����Ƿ�����꽹�㣬�������ɱ���崫�ݵ��������ʱ����Ϊ�Ǳ����
				// ���ڽ���
				if(!(isSearcher(e.getOppositeComponent()))) {
					isFocused = false;
				}
			}
			@Override
			public void focusGained(FocusEvent e) {
				
			}
		});
		
		System.gc();
	}

	// ���������ж�������꽹�����
	private boolean isSearcher(Component component) {
		if(component == null) return false;
		if(component instanceof SearchDialog) return true;
		
		Container container = component.getParent();
		while(container != null) {
			if(container instanceof SearchDialog) return true;
			container = container.getParent();
		}
		return false;
	}

	private void modifyStyle(StyledDocument doc, String line, int lastOffset) {
		Style keyWordStyle = addStyle("Red", null);
		StyleConstants.setForeground(keyWordStyle, new Color(130, 2, 145));
		StyleConstants.setBold(keyWordStyle, true);

		Style stringStyle = addStyle("Blue", null);
		StyleConstants.setForeground(stringStyle, new Color(58, 58, 255));
		StyleConstants.setBold(stringStyle, false);

		Style commentStyle = addStyle("Green", null);
		StyleConstants.setForeground(commentStyle, new Color(63, 127, 95));
		StyleConstants.setBold(commentStyle, false);

		Style docCommentStyle = addStyle("docBlue", null);
		StyleConstants.setForeground(docCommentStyle, new Color(63, 95, 191));
		StyleConstants.setBold(commentStyle, false);

		List<DocInfo> infos = parseLine(lastOffset, line);
		for (DocInfo info : infos) {
			if (info.type == DocInfo.KEY_WORD) {
				doc.setCharacterAttributes(info.offset, info.length,
						keyWordStyle, false);
			}
			if (info.type == DocInfo.STRING) {
				doc.setCharacterAttributes(info.offset, info.length,
						stringStyle, false);
			}
			if (info.type == DocInfo.LINE_COMMENT) {
				doc.setCharacterAttributes(info.offset, info.length,
						commentStyle, false);
			}
			if (info.type == DocInfo.BLOCK_COMMENT) {
				doc.setCharacterAttributes(info.offset, info.length,
						commentStyle, false);
			}
			if (info.type == DocInfo.DOC_COMMENT) {
				doc.setCharacterAttributes(info.offset, info.length,
						docCommentStyle, false);
			}
		}
	}

	private List<DocInfo> parseLine(int currentOffset, final String line) {
		List<DocInfo> list = new ArrayList<DocInfo>();
		if (docComment || blockComment) {
			if (docComment) {
				DocInfo docComment = new DocInfo();
				docComment.type = DocInfo.DOC_COMMENT;
				docComment.length = line.length();
				docComment.offset = currentOffset;
				list.add(docComment);
				getDocCommentInfo(currentOffset, line.toCharArray());
			} else if (blockComment) {// ��ע�ͱ�־�򿪵�ʱ��˵����һ�����ڿ�ע���ڲ�
				DocInfo blockComment = new DocInfo();
				blockComment.type = DocInfo.BLOCK_COMMENT;
				blockComment.length = line.length();
				blockComment.offset = currentOffset;
				list.add(blockComment);
				getBlockCommentInfo(currentOffset, line.toCharArray());
			}
		} else {

			// ����ؼ���
			for (String keyword : keywords) {
				int subbedLength = 0;// ���ҵ���һ���ؼ���֮�������һ�������ʱ��Ҫ�ѵ�һ���ؼ��ֲü���
										// �����ֵ������¼�ü������ַ����ĳ��ȣ��Ա�֤����Ĺؼ���λ�õ���ȷ��
				String tmp = line;// ԭʼ���ݲ��ܸı�
				int offset = tmp.indexOf(keyword);
				char[] chs = tmp.toCharArray();
				int wordLength = keyword.length();
				while (offset != -1) {// һ��Ҳ���ܳ��ֶ���ؼ���
					// �ؼ�����Ϊһ�����ʳ��ֲ�����һ���ؼ���
					// ����ǰ��Ϊ�ո��ַ��Һ���Ϊ�ո��ַ�����Ϊ'[', '.' '('
					// ('.','('�����������ֻ����super��this)
					if ((offset == 0 || (chs[offset - 1] == '\t'
							|| chs[offset - 1] == '\n'
							|| chs[offset - 1] == '\r'
							|| chs[offset - 1] == ' ' || chs[offset - 1] == '(' || chs[offset - 1] == ','))
							&& (offset + wordLength < chs.length && (chs[offset
									+ wordLength] == '\t'
									|| chs[offset + wordLength] == '\n'
									|| chs[offset + wordLength] == '\r'
									|| chs[offset + wordLength] == ' '
									|| chs[offset + wordLength] == ')'
									|| chs[offset + wordLength] == ';'
									|| chs[offset + wordLength] == '['
									|| chs[offset + wordLength] == '.' || chs[offset
									+ wordLength] == '('))) {
						DocInfo info = new DocInfo();
						info.type = DocInfo.KEY_WORD;
						info.offset = currentOffset + offset + subbedLength;
						info.length = keyword.length();
						list.add(info);
					}
					tmp = tmp.substring(offset + wordLength);
					subbedLength += (wordLength + offset);
					chs = tmp.toCharArray();
					offset = tmp.indexOf(keyword);
				}
			}

			// ��ȡһ�����ַ�������Ϣ
			char[] lineChars = line.toCharArray();
			List<DocInfo> stringInfoList = getStringInfo(currentOffset,
					lineChars);
			for (DocInfo info : stringInfoList) {
				list.add(info);
			}

			// ��ȡ��ע�͵���Ϣ
			DocInfo lineCommentInfo = getLineCommentInfo(currentOffset,
					lineChars);
			list.add(lineCommentInfo);

			// �ĵ�ע��
			List<DocInfo> docCommentInfoList = getDocCommentInfo(currentOffset,
					lineChars);
			for (DocInfo info : docCommentInfoList) {
				list.add(info);
			}

			if (!docComment) {// ������ĵ�ע�ͣ��ǾͲ��ٶ�������ע�͵Ĵ���
				// ��ע��
				List<DocInfo> blockCommentInfoList = getBlockCommentInfo(
						currentOffset, lineChars);
				for (DocInfo info : blockCommentInfoList) {
					list.add(info);
				}
			}

		}

		return list;
	}

	private List<DocInfo> getBlockCommentInfo(int currentOffset, char[] line) {
		List<DocInfo> res = new ArrayList<DocInfo>();
		DocInfo info = null;
		for (int i = 0; i < line.length; i++) {
			if (line[i] == '/' && i + 1 < line.length && line[i + 1] == '*') {
				blockComment = true;
				info = new DocInfo();
				info.type = DocInfo.BLOCK_COMMENT;
				info.offset = currentOffset + i;
				info.length = line.length - i;
				res.add(info);
			}
			if (line[i] == '*' && i + 1 < line.length && line[i + 1] == '/') {
				blockComment = false;
				info = new DocInfo();
				info.type = DocInfo.BLOCK_COMMENT;
				info.offset = currentOffset;
				info.length = i + 1;
				res.add(info);
			}
		}
		return res;
	}

	private DocInfo getLineCommentInfo(int currentOffset, char[] line) {
		DocInfo res = new DocInfo();
		res.length = 0;
		for (int i = 0; i < line.length; i++) {
			if (line[i] == '/' && i + 1 < line.length && line[i + 1] == '/') {
				res.type = DocInfo.LINE_COMMENT;
				res.offset = currentOffset + i;
				res.length = line.length - i;
				break;
			}
		}
		return res;
	}

	// java���ַ������������õĲ��ֱ�����ͬһ�У�Ҳ����˵���ܿ�ʼ�����ڵ�һ�У���������ȴ�ڵڶ���
	private List<DocInfo> getStringInfo(int currentOffset, char[] line) {
		List<DocInfo> res = new ArrayList<DocInfo>();
		boolean isStringStart = false;
		boolean isCharStart = false;
		int length = 1;
		int charLength = 1;
		DocInfo info = null;
		for (int i = 0; i < line.length; i++) {
			if (!isCharStart && line[i] == '\"') {
				if (!isStringStart) {
					isStringStart = true;
					info = new DocInfo();
					info.type = DocInfo.STRING;
					info.offset = currentOffset + i;
				} else if (line[i - 1] != '\\'
						|| (i - 2 >= 0 && line[i - 2] == '\\')) {// ɨ�赽�����Ų����Ǳ������ַ����е�ת��Ϊ���ŵ�����
					isStringStart = false;
					info.length = length;
					res.add(info);
					length = 1;
				}
			} else if (!isStringStart && line[i] == '\'') {
				if (!isCharStart) {
					isCharStart = true;
					info = new DocInfo();
					info.type = DocInfo.STRING;
					info.offset = currentOffset + i;
				} else if (line[i - 1] != '\\'
						|| (i - 2 >= 0 && line[i - 2] == '\\')) {// ɨ�赽�����Ų����Ǳ������ַ����е�ת��Ϊ���ŵ�����
					isCharStart = false;
					info.length = charLength;
					res.add(info);
					charLength = 1;
				}
			}
			if (isStringStart) {
				length++;
			}

			if (isCharStart) {
				charLength++;
			}
		}
		return res;
	}

	private List<DocInfo> getDocCommentInfo(int currentOffset, char[] line) {
		List<DocInfo> res = new ArrayList<DocInfo>();
		DocInfo info = null;
		for (int i = 0; i < line.length; i++) {
			if (line[i] == '/' && (i + 1 < line.length && line[i + 1] == '*')
					&& (i + 2 < line.length && line[i + 2] == '*')) {
				docComment = true;
				info = new DocInfo();
				info.type = DocInfo.DOC_COMMENT;
				info.offset = currentOffset + i;
				info.length = line.length - i;
				res.add(info);
			}
			if (line[i] == '*' && i + 1 < line.length && line[i + 1] == '/') {
				docComment = false;
				info = new DocInfo();
				info.type = DocInfo.DOC_COMMENT;
				info.offset = currentOffset;
				info.length = i + 1;
				res.add(info);
			}
		}
		return res;
	}

	private void initFileLineCount() {
		try {
			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine()) {
				scanner.nextLine();
				lineCount++;
			}
			scanner.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public int getLineCount() {
		return lineCount;
	}

	// ��ȡ������ڵ���
	public int getCaretRow() {
		return row;
	}

	// ��ȡ������ڵ���
	public int getCaretCol() {
		return col;
	}

	/**
	 * �Ӹ������п�ʼ���ҷ������ڵ�λ��
	 * @param row
	 * @param methodName
	 */
	public void setSelectedMethod(int row, String methodName) {
		Element root = getDocument().getDefaultRootElement();
		do {
			int lineStart = root.getElement(row - 1).getStartOffset();
			int lineEnd = root.getElement(row - 1).getEndOffset();
			int lineLength = lineEnd - lineStart;
			try {
				String lineStr = getText(lineStart, lineLength);
				if (lineStr.contains(methodName) && !lineStr.contains("*")) {
					int selectionStart = lineStr.indexOf(methodName);
					int selectionEnd = methodName.length();
					requestFocusInWindow();
					setSelectionStart(lineStart + selectionStart);
					setSelectionEnd(lineStart + selectionStart + selectionEnd);
					break;
				}
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			row++;
		} while (row <= lineCount);
	}

	/**
	 * �����ַ�����λ��
	 * 
	 * @param offset
	 * @param contentStr
	 * @return ���ҵ����ַ��������һ���ַ���ƫ���������û���ҵ��򷵻�-1
	 */
	public int setSelectedString(int offset, String contentStr,
			boolean caseSensitive) {
		if(!isFocused) {// �������岻����Ϊ���뽹�㣬���ȡ����
			requestFocus();
			isFocused = true;
		}
		Element root = getDocument().getDefaultRootElement();
		int row = getLineNumByOffset(offset);
		if (row == -1)
			return -1;
		do {
			int lineEnd = root.getElement(row - 1).getEndOffset();
			int lineLength = lineEnd - offset;
			try {
				String lineStr = getText(offset, lineLength);
				if (!caseSensitive) {
					lineStr = lineStr.toLowerCase();
					contentStr = contentStr.toLowerCase();
				}
				if (lineStr.contains(contentStr)) {
					int selectionStart = lineStr.indexOf(contentStr);
					int selectionEnd = contentStr.length();
					requestFocusInWindow();
					setSelectionStart(offset + selectionStart);
					setSelectionEnd(offset + selectionStart + selectionEnd);
					return offset + selectionStart + selectionEnd;
				}
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			row++;
		} while (row <= lineCount);

		return -1;
	}
	
	private void showRowAndCol() {
		Container container = getParent();
		while(container != null && !(container instanceof JsMetricFrame)) {
			container = container.getParent();
		}
		if(container != null) {
			JsMetricFrame mainFrame = (JsMetricFrame)container;
			mainFrame.getStatusBar().setRowAndCol(row, col);
		}
	}
	
	private void hideRowAndCol() {
		Container container = getParent();
		while(container != null && !(container instanceof JsMetricFrame)) {
			container = container.getParent();
		}
		if(container != null) {
			JsMetricFrame mainFrame = (JsMetricFrame)container;
			mainFrame.getStatusBar().hideRowAndCol();
		}
	}

	/**
	 * ����ƫ���������λ�õ�����
	 * 
	 * @param offset
	 * @return ����ƫ������Ӧ�����������û�ж�Ӧ�������򷵻�-1
	 */
	public int getLineNumByOffset(int offset) {
		Element root = getDocument().getDefaultRootElement();
		int row = 1;
		do {
			int lineStart = root.getElement(row - 1).getStartOffset();
			int lineEnd = root.getElement(row - 1).getEndOffset();
			if (lineStart == offset || lineEnd == offset)
				return row;
			if (lineStart < offset && lineEnd > offset)
				return row;
			row++;
		} while (row <= lineCount);
		return -1;
	}

	public long getFileEndOffset() {
		return getDocument().getLength();
	}

	public File getFile() {
		return file;
	}

	public String getFilePath() {
		return file.getAbsolutePath();
	}
}

class DocInfo {
	final static int KEY_WORD = 1;
	final static int BLOCK_COMMENT = 2;
	final static int LINE_COMMENT = 3;
	final static int STRING = 4;
	final static int DOC_COMMENT = 5;
	int offset;
	int length;
	int type;
}