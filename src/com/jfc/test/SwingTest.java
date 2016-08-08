package com.jfc.test;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.jfc.ftp.tools.SystemTools;
/**
 * ������
 * @author SavageGarden
 *
 */
public class SwingTest {

	public static void main(String args[]) {
		FrameTest frame = new FrameTest();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
/**
 * ����壬��ʾ���Ͱ�ť
 * @author SavageGarden
 *
 */
class FrameTest extends JFrame {
	private static int WIDTH;
	private static int HEIGHT;
	public JButton addButton;
	public JScrollPane statusScrollPane1;
	public static JTable statusTable;
	public static StatusTableModel statusTableModel;
	public static String showValue = "0:0";
	public static ProgressBarObservable progressBarObservable;
	public static ProgressBarObserver progressBarObserver; 
	public FrameTest() {
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		WIDTH = screenSize.width/2;
		HEIGHT = screenSize.height/2;
		setSize(WIDTH, HEIGHT);
		setLocationRelativeTo(null);
		setResizable(false);
		setLayout(null);
		setTitle("FrameTest");
		setVisible(false);
		createStatusPanel();
		statusScrollPane1.setBounds(0, 0, WIDTH, 330);
		getContentPane().add(statusScrollPane1);
		addButton = new JButton("���");
		addButton.setBounds(WIDTH/2 -40, 350 , 80, 20);
		addButton.addActionListener(new AddButtonActionListener());
		getContentPane().add(addButton);
		
	    progressBarObservable = new ProgressBarObservable();
	    progressBarObserver = new ProgressBarObserver();
	    progressBarObservable.addObserver(progressBarObserver);
	}
	/**
	 * ����״̬��
	 *
	 */
	private void createStatusPanel() {
		statusTableModel = new StatusTableModel();
		statusTable = new JTable(statusTableModel);
		//����"Status"���ɶ��Ƶ�ProgressBarRenderer��Ⱦ
		TableColumn statusColumn  = statusTable.getColumn("Status");
		statusColumn .setCellRenderer(new ProgressBarRenderer());
		statusScrollPane1 = new JScrollPane(statusTable);
		
	}
	/**
	 * ״̬�����ģ����
	 * @author SavageGarden
	 *
	 */
	class StatusTableModel extends DefaultTableModel {
		public Object[] rowData = {"..", 0, "", "", ""};
		public StatusTableModel() {
			super();
			addColumn("Name");
			addColumn("Status");
			addColumn("Size");
			addColumn("Speed");
		}
		/**
		 * ����Ϊ���ɱ༭
		 */
		public boolean isCellEditable(int row, int column) {
	        return false;
	    }
		/**
		 * ��״̬�����һ��������
		 *
		 */
		public void addProgressBar() {
			rowData[0] = "�ļ�" + (this.getRowCount() + 1);
			addRow(rowData);
		}
	}
	/**
	 * ��ť���¼���Ӧ��
	 * @author SavageGarden
	 *
	 */
	class AddButtonActionListener implements ActionListener {

		public void actionPerformed(ActionEvent event) {
			statusTableModel.addProgressBar();
			Thread t = new ProgressBarThread(); 
			t.start();
		}
		
	}
	/**
	 * ����������Ⱦ��
	 * @author SavageGarden
	 *
	 */
	class ProgressBarRenderer extends DefaultTableCellRenderer{
		private static final long serialVersionUID = 1L;
	    private final JProgressBar b;
	    public ProgressBarRenderer(){
	    	super();
	        setOpaque(true);
	        b = new JProgressBar();
			//�Ƿ���ʾ�����ַ���
			b.setStringPainted(true);
			b.setMinimum(0);
			b.setMaximum(100);
			//�Ƿ���Ʊ߿�
			b.setBorderPainted(true);
	        b.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
	    }
		public Component getTableCellRendererComponent(JTable table, Object value,
	            boolean isSelected, boolean hasFocus, int row, int column) {
	        Integer i = (Integer) value;
	        b.setValue(i);
	        return b;
	    }
	}
	/**
	 * �������߳�
	 * @author SavageGarden
	 *
	 */
	class ProgressBarThread extends Thread {
		int price = 0;
		int rowIndex = statusTableModel.getRowCount()-1;
		public void run() {
			
			while (price < 100) {  
	            try {  
	                Thread.sleep(100);  
	            } catch (InterruptedException e) {  
	                e.printStackTrace();  
	            }  
	            price ++;
	            SwingUtilities.invokeLater(new Runnable(){
        	        public void run() {  
        	        	//statusTableModel.setValueAt(price, index, 1);  
        	        	progressBarObservable.setShowValue(rowIndex + ":" + price);
        	        }  
        	    });
	        }  
		}
	}
	
	/**
	 * ����ʵ�ֹ۲��߹۲�Ķ���---����������ʾֵ
	 * @author SavageGarden
	 *
	 */
	class ProgressBarObservable extends Observable {

		private String showValue;
	    public String getShowValue() {
	        return showValue;
	    }
	    public void setShowValue(String showValue) {
	        this.showValue = showValue;
	        setChanged();
	        notifyObservers(showValue);
	    }
	}
	/**
	 * ����ʵ�ֹ۲��� ---����������ʾֵ�仯ʱ����statusTableModel
	 * @author SavageGarden
	 *
	 */
	class ProgressBarObserver implements Observer {
		public void update(Observable o, Object arg) {
			String showValue = (String)arg;
            statusTableModel.setValueAt(Integer.parseInt(showValue.split(":")[1]), Integer.parseInt(showValue.split(":")[0]), 1);
		}
	}
}