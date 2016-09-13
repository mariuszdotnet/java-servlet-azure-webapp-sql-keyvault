package com.eteller.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.eteller.azure.*;
import com.microsoft.azure.keyvault.KeyVaultClient;
import com.microsoft.azure.keyvault.KeyVaultClientService;
import com.microsoft.azure.keyvault.KeyVaultConfiguration;
import com.microsoft.azure.keyvault.authentication.KeyVaultCredentials;
import com.microsoft.azure.keyvault.models.Secret;
import com.microsoft.windowsazure.Configuration;
import java.sql.*;

/**
 * Servlet implementation class HelloWorld
 */
public class HelloWorld extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String myAppID = System.getenv("AppID");
		String myAppSecret = System.getenv("AppSecret");
		
		if(myAppID == null){
			myAppID = "<APP_ID>";
		}
		if(myAppSecret == null){
			myAppSecret ="<APP_SECRET>";
		}
		
		
		// Get secret from Azure
		KeyVaultCredentials kvCred = new ClientSecretKeyVaultCredential(myAppID, myAppSecret);
		Configuration config = KeyVaultConfiguration.configure(null, kvCred);
		KeyVaultClient vc = KeyVaultClientService.create(config);
		Future<Secret> result = vc.getSecretAsync("https://etellerkeyvault.vault.azure.net:443/secrets/etellerwebapppassword");
		Secret mySecret = null;
		try {
			mySecret = result.get();
			// System.out.println(mySecret.getValue());
			// response.getWriter().write("This is my secret:" + mySecret.getValue());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		// Connect to data and write out data
		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE html>"
				+ "<html>"
				+ "<head>"
				+ "<title>Adventure Works Rocks!</title>"
				+ "</head>"
				+ "<body>"
				+ "<h2>Customers</h2>"	
				+ "<table>"
				+ "<tr>"
				+ "<td><b>Title</b></td>"
				+ "</tr>");
		
		// Connect to database
		String connectionString = 
		"jdbc:sqlserver://<SQLSERVER>:1433;"  
		+ "database=eteller;"  
		+ "user=<USERNAME>;"  
		+ "password="+ mySecret.getValue() +";"  
		+ "encrypt=true;"  
		+ "trustServerCertificate=false;"  
		+ "hostNameInCertificate=*.database.windows.net;"  
		+ "loginTimeout=30;";  


		// Declare the JDBC objects.  
		Connection connection = null;  
		Statement statement = null;   
		ResultSet resultSet = null;
		          
		try {  
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
		    connection = DriverManager.getConnection(connectionString);  

		    // Create and execute a SELECT SQL statement.  
		    String selectSql = "SELECT TOP 10 Title, FirstName, LastName, EmailAddress from SalesLT.Customer";  
		    statement = connection.createStatement();  
		    resultSet = statement.executeQuery(selectSql);  

		    // Print results from select statement  
		    while (resultSet.next())   
		    {  
		        System.out.println(resultSet.getString(2) + " " + resultSet.getString(3));
		        out.println("<tr>"
		        		+ "<td>" + resultSet.getString("Title") + "</td>"
		        		+ "<td>" + resultSet.getString("FirstName") + "</td>"
		        		+ "<td>" + resultSet.getString("LastName") + "</td>"
		        		+ "<td>" + resultSet.getString("EmailAddress") + "</td>"
		        		+ "</tr>");
		    }  
		}  
		catch (Exception e) {  
		    e.printStackTrace();  
		}  
		finally {  
		    // Close the connections after the data has been handled.  
		    if (resultSet != null) try { resultSet.close(); } catch(Exception e) {}  
		    if (statement != null) try { statement.close(); } catch(Exception e) {}  
		    if (connection != null) try { connection.close(); } catch(Exception e) {}  
		}

		
		java.util.Date date= new java.util.Date();
		System.out.println(new Timestamp(date.getTime()));
		
		// Close the HTML Doc
		out.println("</table>"
				+ "<br /><br /><br /><br /><br />" + new Timestamp(date.getTime())
				+ "</body>"
				+ "</html>");
	}
}
