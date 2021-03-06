/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package kml;

import Utilidades.GeneraXML;
import Utilidades.GeoNames;
import Utilidades.Punto;
import java.io.*;
import java.sql.*;
import java.util.Vector;

public class Ejemplo2
{

   public static void main(String[] args)  {
	try { 
	    PrintWriter escribir = new PrintWriter(new BufferedWriter(new FileWriter("Ejemplo2.kml")));
		
            escribir.println(GeneraXML.Inicio());
            escribir.println(procesa());
            escribir.println(GeneraXML.Fin());

            escribir.close();
            System.out.println("Listo!!");
	} catch (Exception e) { 
	    System.out.println(e);
	}
   }

   public static String procesa() {
       String Salida="";
       
       try {
           String db = "Demo.accdb";
           //String url = "jdbc:odbc:MS Access Database;DBQ=" + db;
           String url = "jdbc:ucanaccess://" + db; 

           //Class.forName ("sun.jdbc.odbc.JdbcOdbcDriver");
           Connection con = DriverManager.getConnection (url, "", "");

           Statement select = con.createStatement();
           ResultSet consulta = select.executeQuery("SELECT * FROM f_ciudades");
           boolean seguir = consulta.next();
           while (seguir) {
               String ciudad=consulta.getString("Ciudad");
               String pais=consulta.getString("cve_pais");
               if (pais == null) {
                   pais="";
               }
               Punto p = new Punto(GeoNames.BuscaUbicacion(ciudad,pais));
               if (!p.toString().equalsIgnoreCase("0.0,0.0,0.0 ")) {
                   //System.out.println(ciudad+" "+p);
                   if (pais.equalsIgnoreCase("mx")) {
                       Salida=Salida+GeneraXML.addPunto(ciudad,ciudad+" en "+pais,p,"rosa");
                   } else {
                       Salida=Salida+GeneraXML.addPunto(ciudad,ciudad+" en "+pais,p);
                   }
               } else {
                   System.err.println("No se encotro a "+ciudad+" en "+pais);
               }
       
               seguir = consulta.next(); 
           }
           consulta.close();

           select.close();
           con.close();

         } catch (Exception ex) {
             System.out.println(ex.toString());
         }       
       
       return Salida;
   }
   
   private static Vector Leer(Connection c, String SQL) {
       Vector v = new Vector();
 
       try {
           Statement select2 = c.createStatement();
           ResultSet consulta2 = select2.executeQuery(SQL);

               boolean seguir = consulta2.next();
               while (seguir) {
                   Punto p = new Punto(consulta2.getDouble("Lat"),consulta2.getDouble("Lon"),consulta2.getDouble("Alt"));
                   v.add(p);
                   seguir = consulta2.next(); 
               }
               consulta2.close();
       } catch (Exception e) {
           System.out.println(SQL+"\n"+e);
       }
       
       return v;
   }
}