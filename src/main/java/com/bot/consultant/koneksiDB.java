/*    */ package com.bot.consultant;
/*    */ 
/*    */ import java.sql.Connection;
/*    */ import java.sql.DriverManager;
/*    */ import java.sql.SQLException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class koneksiDB
/*    */ {
/*    */   String url;
/*    */   String usr;
/*    */   String pwd;
/*    */   String dbn;
/*    */   Connection con;
/*    */   
/*    */   public koneksiDB(String dbn) {
/* 33 */     this.dbn = "telegram";
/* 34 */     this.url = "jdbc:mysql://localhost/" + dbn;
/* 35 */     this.usr = "root";
/* 36 */     this.pwd = "";
/*    */   }
/*    */   
/*    */   public koneksiDB(String host, String user, String pass, String dbn) {
/* 40 */     this.dbn = "telebot_karangjati";
/* 41 */     this.url = "jdbc:mysql://localhost/" + dbn;
/* 42 */     this.usr = "root";
/* 43 */     this.pwd = "";
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void panggilDriver() {
/*    */     try {
/* 55 */       String driver = "com.mysql.cj.jdbc.Driver";
/* 56 */       Class.forName(driver);
/* 57 */     } catch (ClassNotFoundException e) {
/* 58 */       System.out.println("driver tidak ada");
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public Connection getConnection() {
/* 64 */     Connection con = null;
/*    */     try {
/* 66 */       Class.forName("com.mysql.jdbc.Driver");
/* 67 */       con = DriverManager.getConnection(this.url, this.usr, this.pwd);
/* 68 */       System.out.println("koneksi berhasil dari koneksiDB");
/* 69 */     } catch (ClassNotFoundException e) {
/* 70 */       System.out.println("Error #1 : " + e.getMessage());
/* 71 */       System.exit(0);
/* 72 */     } catch (SQLException e) {
/* 73 */       System.out.println("Error #2 : " + e.getMessage());
/* 74 */       System.exit(0);
/*    */     } 
/* 76 */     return con;
/*    */   }
/*    */ }


/* Location:              /home/x0r/Documents/pbo/telebotKarangjati.jar!/telebot2/koneksiDB.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.2.1
 */