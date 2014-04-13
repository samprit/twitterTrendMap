/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wordcloud;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserFactory;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import java.io.*;
import java.sql.Timestamp;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.mcavallo.opencloud.Cloud;
import org.mcavallo.opencloud.Tag;


/**
 *
 * @author Samprit
 */
public class WordCloudGUI extends javax.swing.JFrame {

    /**
     * Creates new form WordCloudGUI
     */
    public WordCloudGUI() {
    
        initComponents();
        initMyFunction();
        this.setExtendedState( this.getExtendedState()|JFrame.MAXIMIZED_BOTH );
        this.setMinimumSize(new Dimension(1200, 600));
        
    }
    
    Map<String, Double> words;
    
    public void extractWordfromFile(String fileName){
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(fileName));
            String line = null;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s");
                if(words.containsKey(parts[0])){
                    words.put(parts[0], Double.parseDouble(parts[1])+words.get(parts[0]));
                }else{
                    words.put(parts[0],Double.parseDouble(parts[1]));
                }
                
            }
        } catch (Exception ex) {
            Logger.getLogger(WordCloud.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    protected void initMyFunction(){
        wordPanel.removeAll();
        wordPanel.revalidate(); 
        wordPanel.repaint();
        wordPanel.setLayout(null);

        wordPanel.setLayout(new WrapLayout(FlowLayout.CENTER));
        
        mainPanel.setBackground(Color.white);
        wordPanel.setBackground(Color.white);
        inputPanel.setBackground(Color.white);
        
        words = new HashMap<String, Double>();
        extractWordfromFile("word.txt");
        
        Cloud cloud = new Cloud();
        Random random = new Random();
        
        Iterator it = words.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            //System.out.println(pairs.getKey() + " = " + pairs.getValue());
            cloud.addTag(new Tag(pairs.getKey().toString(),Double.parseDouble(pairs.getValue().toString())));
        }
        
        
        for (Tag tag : cloud.tags()) {
            final JLabel label = new JLabel(tag.getName());
            label.setOpaque(false);
            
            //label.setFont(label.getFont().deriveFont((float) tag.getWeight() * 10));
            Color c=new Color(random.nextInt(200)+40,random.nextInt(200)+40,random.nextInt(200)+40);
            label.setForeground(c);
            int fontType ;
            String fontT;
            int chooseType = random.nextInt(5)+10;
            switch(chooseType){
                case 0: fontType=Font.PLAIN; fontT = "Serif"; break;
                case 1: fontType=Font.BOLD; fontT = "Sans Serif"; break;
                case 2: fontType=Font.ITALIC; fontT = "Monospaced"; break;
                case 3: fontType=Font.BOLD|Font.ITALIC; fontT = "Serif"; break;
                default : fontType=Font.PLAIN; fontT = "Serif"; break;
            }
            label.setFont(new Font(fontT,fontType , tag.getWeightInt()*10));
            final String hashName = tag.getName();
            
            label.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    wordClick(hashName);
                }
                
                public Color savedColor;
                public int size;
                
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    Cursor cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR); 
                    setCursor(cursor);
                    
                    JLabel label = ((JLabel)evt.getSource());
                    savedColor = label.getForeground();
                    label.setForeground(Color.RED);
                    Font labelFont = label.getFont();
                    size = labelFont.getSize();
                    label.setFont(new Font(labelFont.getFontName(),labelFont.getStyle() , size+10));
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    Cursor cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR); 
                    if(getCursor()==Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)){
                        setCursor(cursor);  
                    }
                    
                    JLabel label = ((JLabel)evt.getSource());
                    label.setForeground(savedColor);
                    Font labelFont = label.getFont();
                    label.setFont(new Font(labelFont.getFontName(),labelFont.getStyle() , size));
                }
            });
            
            wordPanel.add(label);
            
        }
        wordPanel.setVisible(true);
        
        // String urlMap = "file://"+System.getProperty("user.dir")+"/map.html";
        // System.out.println(urlMap);
        // showMap("map.html");
    }
    
    public void showMap(String fileName){
        createHTML html = new createHTML(fileName);
        googleMaps(fileName);
    }
    
    public void googleMaps(String fileName){
        Browser browser = BrowserFactory.create();
        JFrame frame = new JFrame("Twitter Map");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.add(browser.getView().getComponent(), BorderLayout.CENTER);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        String urlMap;
        if(System.getProperty("os.name").startsWith("Windows")){
            urlMap = System.getProperty("user.dir")+"\\"+fileName;
        }else{
            urlMap = "file://"+System.getProperty("user.dir")+"/"+fileName;
        }
        //browser.loadURL("F:/3.%20DBMS/WordCloud/WordCloud/map.html");
        browser.loadURL(urlMap);
    }

    public void wordClick(String hashName){
        Cursor cursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR); 
        setCursor(cursor);
        int S =Integer.parseInt(SText.getText());
        int L =Integer.parseInt(LText.getText());
        int noUsers =Integer.parseInt(noUsersText.getText());
        trend _trend = new trend(hashName, noUsers, getStartTime(), getEndTime(), S,L);
        System.out.println("SUCCESS = "+_trend.isSuccessful());
        if(_trend.isSuccessful()){      
            plot("output.txt",hashName);
            showMap("map.html");
            initMyFunction();
        }else{
            JOptionPane.showMessageDialog(null, "Oops! Can't find any tweet in that time !! try again !!");
        }
        cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR); 
        setCursor(cursor);
    }
    
    public void plot(String fileName, String name){
        XYSeries series = new XYSeries("Word");
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(fileName));
            String line = null;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s");
                series.add(Integer.parseInt(parts[0]), Float.parseFloat(parts[1]));
            }
        } catch (Exception ex) {
            Logger.getLogger(WordCloud.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        XYDataset xyDataset = new XYSeriesCollection(series);
                     
        JFreeChart chart = ChartFactory.createXYLineChart(name, // Title
                "Time", // X-Axis label
                "Users", // Y-Axis label
                xyDataset // Dataset
         );
        ChartFrame frame1=new ChartFrame("Word Chart",chart);
        frame1.setVisible(true);
        frame1.setSize(600,400);
    }
    
    public Timestamp getStartTime(){
        Date date = StartCalendar.getDate();            
        Calendar cal = GregorianCalendar.getInstance();
                    
        cal.setTime(date);
        Timestamp start = new Timestamp(cal.getTimeInMillis());
        return start;
    }

    public Timestamp getEndTime(){
        Date date = EndCalendar.getDate();            
        Calendar cal = GregorianCalendar.getInstance();
                    
        cal.setTime(date);
        Timestamp end = new Timestamp(cal.getTimeInMillis());
        return end;
    }    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        wordPanel = new javax.swing.JPanel();
        inputPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        SText = new javax.swing.JTextField();
        LText = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        noUsersText = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        hashText = new javax.swing.JTextField();
        goButton = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        StartCalendar = new com.toedter.calendar.JCalendar();
        EndCalendar = new com.toedter.calendar.JCalendar();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout wordPanelLayout = new javax.swing.GroupLayout(wordPanel);
        wordPanel.setLayout(wordPanelLayout);
        wordPanelLayout.setHorizontalGroup(
            wordPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 477, Short.MAX_VALUE)
        );
        wordPanelLayout.setVerticalGroup(
            wordPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 476, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(wordPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(wordPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel1.setText("S");

        jLabel2.setText("L");

        SText.setText("10000000");

        LText.setText("30000000");

        jLabel3.setText("Num Of Users");

        noUsersText.setText("100");

        jLabel6.setText("Enter HashTag");

        goButton.setText("GO");
        goButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout inputPanelLayout = new javax.swing.GroupLayout(inputPanel);
        inputPanel.setLayout(inputPanelLayout);
        inputPanelLayout.setHorizontalGroup(
            inputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(inputPanelLayout.createSequentialGroup()
                .addGap(110, 110, 110)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(80, 80, 80))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, inputPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(inputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, inputPanelLayout.createSequentialGroup()
                        .addComponent(noUsersText, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(164, 164, 164))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, inputPanelLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(182, 182, 182))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, inputPanelLayout.createSequentialGroup()
                .addGap(83, 83, 83)
                .addComponent(SText, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(LText, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(60, 60, 60))
            .addGroup(inputPanelLayout.createSequentialGroup()
                .addGap(60, 60, 60)
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(hashText, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(goButton, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        inputPanelLayout.setVerticalGroup(
            inputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(inputPanelLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(noUsersText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(inputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(inputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(34, 34, 34)
                .addGroup(inputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(hashText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(goButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel4.setText("    Start Date");

        jLabel5.setText("    End Date");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(StartCalendar, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(EndCalendar, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(78, 78, 78)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(79, 79, 79))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(EndCalendar, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(StartCalendar, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(inputPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(inputPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void goButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_goButtonActionPerformed

        String hashName = hashText.getText();
        if(hashName==""){
            JOptionPane.showMessageDialog(null, "Oops! Enter a hashTag!! try again !!");
            return;
        }
        wordClick(hashName);
    }//GEN-LAST:event_goButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        
        try {    
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(WordCloudGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(WordCloudGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(WordCloudGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(WordCloudGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new WordCloudGUI().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JCalendar EndCalendar;
    private javax.swing.JTextField LText;
    private javax.swing.JTextField SText;
    private com.toedter.calendar.JCalendar StartCalendar;
    private javax.swing.JButton goButton;
    private javax.swing.JTextField hashText;
    private javax.swing.JPanel inputPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JTextField noUsersText;
    private javax.swing.JPanel wordPanel;
    // End of variables declaration//GEN-END:variables
}
