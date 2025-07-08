// MultipleFiles/katakunci.java
package com.bot.consultant;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.LayoutStyle;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Font;
import java.awt.Color; // Import java.awt.Color

public class katakunci extends JFrame {
    private ResultSet Rss = null;
    private Connection Con = null;
    private Object[][] dataTable = null;
    private String[] header = new String[]{"Kata", "Jawaban"};

    private JButton btnedit;
    private JButton btnhapus;
    private JButton btnreset;
    private JButton btntambah;
    private JButton jButton1; // Button for "BATAL"
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JLabel jLabel5;
    private JScrollPane jScrollPane1;
    private JScrollPane jScrollPane2;
    private JSeparator jSeparator3;
    private JSeparator jSeparator4;
    private JSeparator jSeparator5;
    private JButton refresh;
    private javax.swing.JTable tblkj;
    private javax.swing.JTextPane txtjwb;
    private javax.swing.JTextField txtkata;

    public katakunci() {
        initComponents();
        setLocationRelativeTo(null);
        baca_data(); // Load data on startup
    }
    
    public static String cariJawaban(String keyword) {
    String jawaban = null;
    MySQLConnector conn = new MySQLConnector();
    try {
        conn.connect();
        String sql = "SELECT jawaban FROM kamut WHERE kata = ?";
        java.sql.PreparedStatement pstmt = conn.getMysqlConnection().prepareStatement(sql);
        pstmt.setString(1, keyword);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            jawaban = rs.getString("jawaban");
        }
        pstmt.close();
    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        conn.closeConnection();
    }
    return jawaban;
}


    private void baca_data() {
        MySQLConnector mySqlConnector = new MySQLConnector();
        try {
            mySqlConnector.connect();
            Connection conn = mySqlConnector.getMysqlConnection();
            Statement mysqlStatement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            String sql = "SELECT kata, jawaban FROM kamut ORDER BY kata ASC";
            Rss = mysqlStatement.executeQuery(sql);

            ResultSetMetaData meta = Rss.getMetaData();
            int col = meta.getColumnCount();

            // Count rows
            Rss.last();
            int baris = Rss.getRow();
            Rss.beforeFirst();

            dataTable = new Object[baris][col];
            int x = 0;
            while (Rss.next()) {
                dataTable[x][0] = Rss.getString("kata");
                dataTable[x][1] = Rss.getString("jawaban");
                x++;
            }
            tblkj.setModel(new DefaultTableModel(dataTable, header));
            mysqlStatement.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error reading data: " + e.getMessage());
            e.printStackTrace();
        } finally {
            mySqlConnector.closeConnection();
        }
    }

    private void initComponents() {
        jScrollPane1 = new JScrollPane();
        tblkj = new javax.swing.JTable();
        jLabel1 = new JLabel();
        jSeparator3 = new JSeparator();
        jLabel2 = new JLabel();
        jLabel3 = new JLabel();
        txtkata = new javax.swing.JTextField();
        jScrollPane2 = new JScrollPane();
        txtjwb = new javax.swing.JTextPane();
        jLabel4 = new JLabel();
        btntambah = new JButton();
        btnedit = new JButton();
        btnhapus = new JButton();
        jSeparator4 = new JSeparator();
        jLabel5 = new JLabel();
        refresh = new JButton();
        jSeparator5 = new JSeparator();
        btnreset = new JButton();
        jButton1 = new JButton();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close only this window
        setTitle("Kelola Keyword & Jawaban");
        getContentPane().setBackground(new Color(255, 255, 255)); // Mengatur warna latar belakang JFrame

        tblkj.setModel(new DefaultTableModel(
        new Object[][]{},
        new String[]{"Kata", "Jawaban"}
));
tblkj.addMouseListener(new java.awt.event.MouseAdapter() {
    public void mouseClicked(MouseEvent evt) {
        tblkjMouseClicked(evt);
    }
});
jScrollPane1.setViewportView(tblkj);
jScrollPane1.getViewport().setBackground(new Color(227, 242, 253)); // sama dengan tblkj


// ---------------------- THEME RED-WHITE -----------------------
tblkj.setBackground(new Color(227, 242, 253));           // merah muda
tblkj.setForeground(Color.BLACK);                        // teks hitam
tblkj.setSelectionBackground(new Color(42, 127, 255));      // seleksi merah
tblkj.setSelectionForeground(Color.WHITE);               // teks seleksi putih
tblkj.setGridColor(new Color(42, 127, 255));                           // garis grid merah
tblkj.setRowHeight(24);                                  // tinggi baris
tblkj.getTableHeader().setBackground(new Color(42, 127, 255)); // header merah
tblkj.getTableHeader().setForeground(Color.WHITE);       // teks header putih
tblkj.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 13));
// --------------------------------------------------------------

tblkj.getTableHeader().setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
    @Override
    public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                                                            boolean isSelected, boolean hasFocus,
                                                            int row, int column) {
        java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        c.setBackground(new java.awt.Color(42, 127, 255)); // header merah
        c.setForeground(java.awt.Color.WHITE);          // teks putih
        setHorizontalAlignment(javax.swing.SwingConstants.CENTER); // teks rata tengah
        return c;
    }
});


        
        jLabel1.setText("KELOLA KATA KUNCI & JAWABAN");
        jLabel1.setForeground(new Color(33, 33, 33)); // Warna teks label
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
jLabel1.setFont(new Font("Tahoma", Font.BOLD, 16)); // tebal & lebih besar


        jLabel2.setText("KATA");
        jLabel2.setForeground(new Color(33,33,33)); // Warna teks label
        jLabel3.setText("JAWABAN");
        jLabel3.setForeground(new Color(33,33,33)); // Warna teks label

        txtkata.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                txtkataActionPerformed(evt);
            }
        });
        txtkata.setBackground(new Color(255, 255, 255)); // Warna latar belakang text field

        jScrollPane2.setViewportView(txtjwb);
        txtjwb.setBackground(new Color(255, 255, 255)); // Warna latar belakang text pane

        jLabel4.setForeground(new java.awt.Color(255, 51, 51)); // Tetap merah terang untuk peringatan
        jLabel4.setText("*Awali dengan tanda '/ ' untuk membedakan kata kunci");

        btntambah.setText("TAMBAH");
        btntambah.setBackground(new Color(232, 245, 233)); // Warna tombol
        btntambah.setForeground(new Color(56, 142, 60)); // Warna teks tombol
        btntambah.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btntambahActionPerformed(evt);
            }
        });

        btnedit.setText("EDIT");
        btnedit.setBackground(new Color(255, 249, 196));
        btnedit.setForeground(new Color(255, 160, 0));
        btnedit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btneditActionPerformed(evt);
            }
        });

        btnhapus.setText("HAPUS");
        btnhapus.setBackground(new Color(255, 235, 238));
        btnhapus.setForeground(new Color(211, 47, 47));
        btnhapus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnhapusActionPerformed(evt);
            }
        });

        jLabel5.setFont(new Font("Tahoma", 1, 12));
        jLabel5.setText("DATA KATA KUNCI");
        jLabel5.setForeground(Color.WHITE); // Warna teks label

        refresh.setText("REFRESH");
        refresh.setBackground(new Color(227, 242, 253));
        refresh.setForeground(new Color(42, 127, 255));
        refresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                refreshActionPerformed(evt);
            }
        });

        btnreset.setText("CLEAR");
        btnreset.setBackground(new Color(232, 234, 246));
        btnreset.setForeground(new Color(63, 81, 181));
        btnreset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnresetActionPerformed(evt);
            }
        });

        jButton1.setText("KEMBALI");
        jButton1.setBackground(new Color(224, 242, 241));
        jButton1.setForeground(new Color(0, 121, 107));
        jButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        // Layout setup (using GroupLayout as in original)
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(10, 10, 10)
                                                .addComponent(jLabel1))
                                        .addComponent(jSeparator3, GroupLayout.PREFERRED_SIZE, 700, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jSeparator4, GroupLayout.PREFERRED_SIZE, 266, GroupLayout.PREFERRED_SIZE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(10, 10, 10)
                                                .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 652, GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(10, 10, 10)
                                                .addComponent(jLabel5)
                                                .addGap(427, 427, 427)
                                                .addComponent(refresh))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(221, 221, 221)
                                                .addComponent(jLabel4))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(83, 83, 83)
                                                .addComponent(txtkata, GroupLayout.PREFERRED_SIZE, 128, GroupLayout.PREFERRED_SIZE))
                                        .addComponent(jSeparator5, GroupLayout.PREFERRED_SIZE, 700, GroupLayout.PREFERRED_SIZE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(281, 281, 281)
                                                .addComponent(jButton1, GroupLayout.PREFERRED_SIZE, 106, GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(-1, 32767))
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(jLabel2, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE))
                                        .addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                .addGap(21, 21, 21)
                                                .addComponent(jLabel3)))
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane2)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                        .addComponent(btnhapus, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnedit, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btntambah, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnreset, GroupLayout.PREFERRED_SIZE, 73, GroupLayout.PREFERRED_SIZE))
                                .addGap(76, 76, 76))
        );

        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(11, 11, 11)
                                .addComponent(jLabel1)
                                .addGap(4, 4, 4)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(jSeparator3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jSeparator4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(7, 7, 7)
                                .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 158, GroupLayout.PREFERRED_SIZE)
                                .addGap(11, 11, 11)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(22, 22, 22)
                                                .addComponent(jLabel5))
                                        .addComponent(refresh))
                                .addGap(5, 5, 5)
                                .addComponent(jSeparator5, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(9, 9, 9)
                                                .addComponent(jLabel4))
                                        .addGroup(layout.createSequentialGroup()
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(txtkata, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(9, 9, 9)
                                                .addComponent(jLabel2)))
                                .addGap(16, 16, 16)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel3)
                                        .addComponent(jScrollPane2, GroupLayout.PREFERRED_SIZE, 139, GroupLayout.PREFERRED_SIZE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(btntambah)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnedit)
                                                .addGap(18, 18, 18)
                                                .addComponent(btnhapus)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnreset)))
                                .addGap(18, 18, 18)
                                .addComponent(jButton1)
                                .addContainerGap(25, 32767))
        );
        pack();
    }

    private void txtkataActionPerformed(ActionEvent evt) {
        // No specific action needed here
    }

    private void btntambahActionPerformed(ActionEvent evt) {
        MySQLConnector mySqlConnector = new MySQLConnector();
        try {
            mySqlConnector.connect();
            String sql = "INSERT INTO kamut (kata, jawaban) VALUES (?, ?)";
            java.sql.PreparedStatement pstmt = mySqlConnector.getMysqlConnection().prepareStatement(sql);
            pstmt.setString(1, txtkata.getText());
            pstmt.setString(2, txtjwb.getText());
            pstmt.executeUpdate();
            pstmt.close();
            JOptionPane.showMessageDialog(this, "Penyimpanan Data Berhasil");
            btnresetActionPerformed(null); // Clear fields
            baca_data(); // Refresh table
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding data: " + e.getMessage());
            e.printStackTrace();
        } finally {
            mySqlConnector.closeConnection();
        }
    }

    private void btnresetActionPerformed(ActionEvent evt) {
        txtkata.setText("");
        txtjwb.setText("");
    }

    private void btnhapusActionPerformed(ActionEvent evt) {
        int ok = JOptionPane.showConfirmDialog(this, "Apakah Yakin Mendelete data ini???", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            MySQLConnector mySqlConnector = new MySQLConnector();
            try {
                mySqlConnector.connect();
                String sql = "DELETE FROM kamut WHERE kata = ?";
                java.sql.PreparedStatement pstmt = mySqlConnector.getMysqlConnection().prepareStatement(sql);
                pstmt.setString(1, txtkata.getText());
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Delete Data Sukses");
                    btnresetActionPerformed(null); // Clear fields
                    baca_data(); // Refresh table
                } else {
                    JOptionPane.showMessageDialog(this, "Data tidak ditemukan.");
                }
                pstmt.close();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Delete Data Gagal: " + e.getMessage());
                e.printStackTrace();
            } finally {
                mySqlConnector.closeConnection();
            }
        }
    }

    private void btneditActionPerformed(ActionEvent evt) {
        MySQLConnector mySqlConnector = new MySQLConnector();
        try {
            mySqlConnector.connect();
            String sql = "UPDATE kamut SET jawaban = ? WHERE kata = ?";
            java.sql.PreparedStatement pstmt = mySqlConnector.getMysqlConnection().prepareStatement(sql);
            pstmt.setString(1, txtjwb.getText());
            pstmt.setString(2, txtkata.getText());
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Data berhasil di edit");
                btnresetActionPerformed(null); // Clear fields
                baca_data(); // Refresh table
            } else {
                JOptionPane.showMessageDialog(this, "Data tidak ditemukan atau tidak ada perubahan.");
            }
            pstmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Perubahan Data Gagal: " + e.getMessage());
            e.printStackTrace();
        } finally {
            mySqlConnector.closeConnection();
        }
    }

    private void tblkjMouseClicked(MouseEvent evt) {
        int baris = tblkj.rowAtPoint(evt.getPoint());
        if (baris >= 0) {
            String kata = tblkj.getValueAt(baris, 0).toString();
            String jawab = tblkj.getValueAt(baris, 1).toString();
            txtkata.setText(kata);
            txtjwb.setText(jawab);
        }
    }

    private void refreshActionPerformed(ActionEvent evt) {
        baca_data();
    }

    private void jButton1ActionPerformed(ActionEvent evt) {
        dispose(); // Close this window
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            Logger.getLogger(katakunci.class.getName()).log(Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(() -> new katakunci().setVisible(true));
    }
}
