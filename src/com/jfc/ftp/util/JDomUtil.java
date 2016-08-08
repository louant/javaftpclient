package com.jfc.ftp.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
/**
 * JDom�Ĺ�����
 * @author SavageGarden
 *
 */
public class JDomUtil {

	public static Document doc;
	public static Element root;
	/**
	 * ������ת��Ϊ���
	 * @param in
	 * @param out
	 * @throws Exception
	 */
	public static final void copyInputStream(InputStream in, OutputStream out)throws Exception {
		int read;
		while ((read = in.read()) != -1) {
			out.write(read);
		}
		in.close();
		out.close();
	}
	
	/**
	 * ���ַ������浽�ļ�
	 * @param doc
	 * @param fileName
	 * @throws Exception
	 */
	public static final void saveDocToFile(String doc, String fileName)throws Exception{
		copyInputStream(new BufferedInputStream(
                new ByteArrayInputStream(doc.getBytes())),
                new BufferedOutputStream(new FileOutputStream(fileName)));
	}
	
	/**
	 * �õ�ָ��·����XML�ļ���root�ڵ�
	 * @param filePath
	 * @return
	 * @throws JDOMException
	 * @throws IOException
	 */
	public static Element getRootElement(String filePath) throws JDOMException, IOException{
    	SAXBuilder sb = new SAXBuilder();
    	doc = sb.build(filePath);
    	root = doc.getRootElement();
    	return root;
    }
	
	/**
	 * ��xml������ָ������д�뵽ָ���ļ���
	 * @param document
	 * @param file
	 * @param charsetName
	 */
	public static void outXMLToFile(Document document , File file , String charsetName){
		XMLOutputter xmlOut = null;
		if(charsetName.length() > 0){
			xmlOut = new XMLOutputter("", true, charsetName);
		}else{
			xmlOut = new XMLOutputter();
		}
    	
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			xmlOut.output(document, fos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	/**
	 * ��element��ָ��������뵽ָ���ļ�
	 * @param element
	 * @param filepath
	 * @param charsetName
	 */
	public static void addElementToFile(Element element, String filepath, String charsetName) {
		root.addContent(element);
		outXMLToFile(doc, new File(filepath), charsetName);
	}
}
