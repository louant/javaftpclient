package com.jfc.ftp.event;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.jfc.ftp.gui.FTPMainFrame;
import com.jfc.ftp.model.table.ClientTableModel;
import com.jfc.ftp.tools.SystemTools;

/**
 * ���������ı���ļ����¼�
 * <br>��Ҫ�ǿͻ��˺ͷ���˵�Ŀ¼��仯ʱ��Ӧ�س���
 * @author SavageGarden
 *
 */
public class FTPTextFieldEvent extends FTPEvent {

	private static int keyPressedChar;
	private static int keyTypedChar;
	private static int keyReleasedChar;
	
	private static String oldPath = "";
	private static String newPath = "";
	private static boolean pressedFlag = false;
	
	public static void clientAddActionListener(final JTextField textField) {
		textField.addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e) {
				keyPressedChar = e.getKeyChar();
				if(!pressedFlag) {
					oldPath = textField.getText();
					pressedFlag = true;
				}
			}

			public void keyReleased(KeyEvent e) {
				keyReleasedChar = e.getKeyChar();
				if(keyReleasedChar == e.VK_ENTER) {
					mainFrame.clientTableModel.showFileList(newPath);
					pressedFlag = false;
				}
			}

			public void keyTyped(KeyEvent e) {
				keyTypedChar = e.getKeyChar();
			}
			
		});
		textField.getDocument().addDocumentListener(new DocumentListener() {

			public void changedUpdate(DocumentEvent e) {
				
			}

			public void insertUpdate(DocumentEvent e) {
				newPath = textField.getText();
			}

			public void removeUpdate(DocumentEvent e) {
				newPath = textField.getText();
			}
			
		});
	}
}
