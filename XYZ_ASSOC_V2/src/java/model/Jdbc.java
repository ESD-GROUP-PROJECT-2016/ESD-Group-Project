/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.sql.Connection;
import java.sql.Date;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import static java.sql.Types.NULL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author me-aydin
 */
public class Jdbc {
    
    Connection connection = null;
    Statement statement = null;
    ResultSet rs = null;
    //String query = null;
    
    
    public Jdbc(String query){
        //this.query = query;
    }

    public Jdbc() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void connect(Connection con){
       this.connection = con;
    }
    
    public void addMember(Member member, User user) {
        try {

          Class.forName("com.mysql.jdbc.Driver");

       String dbname = "xyz_assoc";
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+dbname.trim(), "root", "");
        
        Class.forName("com.mysql.jdbc.Driver");
        
        PreparedStatement ps = connection.prepareStatement("INSERT INTO members VALUES (?,?,?,?,?,?,?)");
        
        java.sql.Date dob = new java.sql.Date(member.getDob().getTime());
        java.sql.Date RegDate = new java.sql.Date(member.getRegDate().getTime());
        
        ps.setString(1, member.getuName());
        ps.setString(2, member.getName());
        ps.setString(3,"1 home street"); //member.getAddress());
        ps.setDate(4, dob);
        ps.setDate(5, RegDate);
        ps.setString(6, "APPLIED");
        ps.setFloat(7, member.getBalance());
        ps.executeUpdate();
        
        //Users structure ('id', 'password', 'status')

        PreparedStatement userPs = connection.prepareStatement("INSET INTO users VALUES (?,?,?)");

        
        userPs.setString(1, user.getuName());
        userPs.setString(2, user.getPassword());
        userPs.setString(3, user.getStatus());
        userPs.executeUpdate();
 

    
        
       

        
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }
    
    public boolean checkMember(String id, String password) {
        boolean st = false;
        try {
            PreparedStatement ps = connection.prepareStatement("select * from users where id=? and password=?");
            ps.setString(1, id);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            st = rs.next();

        } catch (SQLException ex) {
            Logger.getLogger(Jdbc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return st;
    }
    public Member getMember(String userName) throws SQLException, ClassNotFoundException {
        Member mem = new Member();
       try {
        Class.forName("com.mysql.jdbc.Driver");
        
        Connection con = DriverManager.getConnection("jdbc:mysql//localhost/xyz_assoc", "root", "");
        PreparedStatement statement = con.prepareStatement("SELECT * FROM members WHERE id=?");
        
        statement.setString(1, userName);
        
        ResultSet result = statement.executeQuery();
        
        while(result.next()) {
            mem.setuName(result.getString("id"));
            mem.setName(result.getString("name"));
            mem.setAddress(result.getString("address"));
            mem.setDob(result.getDate("dob"));
            mem.setRegDate(result.getDate("dor"));
            mem.setStatus(result.getString("status"));
            mem.setBalance(result.getFloat("balance"));
        }
       
       }  catch(SQLException e) {
           Logger.getLogger(Jdbc.class.getName()).log(Level.SEVERE, null, e);
       }
        return mem;
    }
    
    public List<Member> getAllMembers() throws ClassNotFoundException, SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultset = null;

        Class.forName("com.mysql.jdbc.Driver");

        connection = DriverManager.getConnection("jdbc:mysql://localhost/xyz_assoc", "root", "");

        statement = connection.createStatement();
        resultset = statement.executeQuery("SELECT * FROM members");

        List<Member> members = new ArrayList<>();

        while (resultset.next()) {
//            Member member = new Member(resultSet.getString("name"), resultSet.getString("address"), resultSet.getDate("dob"), resultSet.getDate("dor"), resultSet.getString("status"), resultSet.getFloat("balance"));
            Member member = new Member();
            member.setuName(resultset.getString("id"));
            member.setName(resultset.getString("name"));
            member.setAddress(resultset.getString("address"));
            member.setDob(resultset.getDate("dob"));
            member.setRegDate(resultset.getDate("dor"));
            member.setStatus(resultset.getString("status"));
            member.setBalance(resultset.getFloat("balance"));
            members.add(member);
        }
        return members;
    }
    
    private ArrayList rsToList() throws SQLException {
        ArrayList aList = new ArrayList();

        int cols = rs.getMetaData().getColumnCount();
        while (rs.next()) { 
          String[] s = new String[cols];
          for (int i = 1; i <= cols; i++) {
            s[i-1] = rs.getString(i);
          } 
          aList.add(s);
        } // while    
        return aList;
    } //rsToList
 
    private String makeTable(ArrayList list) {
        StringBuilder b = new StringBuilder();
        String[] row;
        b.append("<table border=\"3\">");
        for (Object s : list) {
          b.append("<tr>");
          row = (String[]) s;
            for (String row1 : row) {
                b.append("<td>");
                b.append(row1);
                b.append("</td>");
            }
          b.append("</tr>\n");
        } // for
        b.append("</table>");
        return b.toString();
    }//makeHtmlTable
    
    private void select(String query){
        //Statement statement = null;
        
        try {
            statement = connection.createStatement();
            rs = statement.executeQuery(query);
            //statement.close();
        }
        catch(SQLException e) {
            System.out.println("way way"+e);
            //results = e.toString();
        }
    }
    public String retrieve(String query) throws SQLException {
        String results="";
        select(query);
//        try {
//            
//            if (rs==null)
//                System.out.println("rs is null");
//            else
//                results = makeTable(rsToList());
//        } catch (SQLException ex) {
//            Logger.getLogger(Jdbc.class.getName()).log(Level.SEVERE, null, ex);
//        }
        return makeTable(rsToList());//results;
    }
    
    public boolean exists(String user) {
        boolean bool = false;
        try  {
            select("select username from users where username='"+user.trim()+"'");
            if(rs.next()) {
                System.out.println("TRUE");         
                bool = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Jdbc.class.getName()).log(Level.SEVERE, null, ex);
        }
        return bool;
    }
    public void insert(String[] str){
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement("INSERT INTO Users VALUES (?,?)",PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, str[0].trim()); 
            ps.setString(2, str[1]);
            ps.executeUpdate();
        
            ps.close();
            System.out.println("1 row added.");
        } catch (SQLException ex) {
            Logger.getLogger(Jdbc.class.getName()).log(Level.SEVERE, null, ex);
        }
         
    }
    
    public List<Claims> getAllClaims() throws ClassNotFoundException, SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        Class.forName("com.mysql.jdbc.Driver");

        connection = DriverManager.getConnection("jdbc:mysql://localhost/xyz_assoc", "root", "");

        statement = connection.createStatement();
        resultSet = statement.executeQuery("SELECT * FROM claims");

        List<Claims> claims = new ArrayList<>();

        while (resultSet.next()) {
            Claims claim = new Claims();
            claim.setuName(resultSet.getString("mem_id"));
            claim.setClaimDate(resultSet.getDate("date"));
            claim.setReason(resultSet.getString("rationale"));
            claim.setStatus(resultSet.getString("status"));
            claim.setAmount(resultSet.getFloat("amount"));
            claim.setClaimId(resultSet.getInt("id"));
            claims.add(claim);
        }
        return claims;
    }
    
    public void update(String[] str) {
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement("Update Users Set password=? where username=?",PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, str[1].trim()); 
            ps.setString(2, str[0].trim());
            ps.executeUpdate();
        
            ps.close();
            System.out.println("1 rows updated.");
        } catch (SQLException ex) {
            Logger.getLogger(Jdbc.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void delete(String user){
       
      String query = "DELETE FROM Users " +
                   "WHERE username = '"+user.trim()+"'";
      
        try {
            statement = connection.createStatement();
            statement.executeUpdate(query);
        }
        catch(SQLException e) {
            System.out.println("way way"+e);
            //results = e.toString();
        }
    }
    public void closeAll(){
        try {
            rs.close();
            statement.close(); 		
            //connection.close();                                         
        }
        catch(SQLException e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) throws SQLException {

//        String str = "select * from users";
//        String insert = "INSERT INTO `Users` (`username`, `password`) VALUES ('meaydin', 'meaydin')";
//        String update = "UPDATE `Users` SET `password`='eaydin' WHERE `username`='eaydin' ";
 //       String db = "MyDB";
 //       
 //       Jdbc jdbc = new Jdbc(str);
 //       Connection conn = null;
 //               try {
 //           Class.forName("com.mysql.jdbc.Driver");
 //           conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+db.trim(), "root", "");
 //       }
 //       catch(ClassNotFoundException | SQLException e){
 //           
 //       }
 //       jdbc.connect(conn);
 //       String [] users = {"birgul12","han","han"};
 //       System.out.println(jdbc.retrieve(str));
 //       if (!jdbc.exists(users[0]))
 //           jdbc.insert(users);            
 //       else {
 //               jdbc.update(users);
 //               System.out.println("user name exists, change to another");
  //      }
  //      jdbc.delete("aydinme");
        
 //       System.out.println(jdbc.retrieve(str));
   //     jdbc.closeAll();
      
    }
}
