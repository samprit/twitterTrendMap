package wordcloud;
import java.io.File;
import java.io.IOException;
//import org.apache.hadoop.conf.Configuration;
//import org.apache.hadoop.fs.FileSystem;
//import org.apache.hadoop.fs.FSDataInputStream;
//import org.apache.hadoop.fs.FSDataOutputStream;
//import org.apache.hadoop.fs.Path;
import java.awt.List; 
import java.io.BufferedReader; 
import java.io.BufferedWriter; 
import java.io.DataInputStream;
import java.io.FileInputStream; 
import java.io.FileNotFoundException; 
import java.io.FileReader; 
import java.io.FileWriter; 
import java.io.IOException; 
import java.io.InputStreamReader;

import java.sql.Timestamp;
import java.util.ArrayList; 
import java.util.Dictionary; 
import java.util.Enumeration; 
import java.util.HashSet; 
import java.util.Hashtable; 
import java.util.Iterator; 
import java.util.Scanner; 
import java.util.Set;

  public class database{
  String[] Filename={"follow.out","politics.out","sports.out","meme.out","music.out"};

  public int month(String mon)
  {
  int mm=0;
  switch(mon){
  case "Jan": mm=1;
             break;
  case "Feb": mm=2;
             break;
  case "Mar": mm=3;
             break;
  case "Apr": mm=4;
             break;
  case "May": mm=5;
             break;
  case "Jun": mm=6;
             break;
  case "Jul": mm=7;
             break;
  case "Aug": mm=8;
             break;
  case "Sep": mm=9;
             break;
  case "Oct": mm=10;
             break;
  case "Nov": mm=11;
             break;
  case "Dec": mm=12;
             break;

  }
  return mm;
  }
  public long getFreq(String hashtag, Timestamp start_time, Timestamp end_time){

        //returns number of tweets with given hashtag within specified time  
        long count=0;
        int j=0;
        for(j=0;j<5;j++){
        try{
 // Open the file that is the first 
 // command line parameter
 FileInputStream fstream = new FileInputStream(Filename[j]);
 // Get the object of DataInputStream
 DataInputStream in = new DataInputStream(fstream);
 BufferedReader br = new BufferedReader(new InputStreamReader(in));
 String strLine;
 int i=0;
 String time="";
 int mm;
 //Read File Line By Line
 while ((strLine = br.readLine()) != null)   {
 // Print the content on the console
 //System.out.println (strLine);
 // ArrayList <String> list= new Arraylist<String>();
    //       Scanner sc=new Scanner(strLine);
    //       while(sc.hasNext())
    //       {
    //       list.add(sc.next)
 	String [] list = strLine.split(" ");
 	//System.out.println(list[7]);
 	if(hashtag.equalsIgnoreCase(list[7]))
 	{
 		        time="";
                time=time+list[6]+"-";
                mm=month(list[2]);
                time=time+mm+"-"+list[3]+" "+list[4];
                Timestamp timestamp=Timestamp.valueOf(time);
                //System.out.println(timestamp.getTime());
                if((start_time.before(timestamp) && end_time.after(timestamp)) || start_time.equals(timestamp) || end_time.equals(timestamp))
                count++;
 	}
         }
 	
 //Close the input stream
 in.close();
   }catch (Exception e){//Catch exception if any
 System.err.println("Error: " + e.getMessage());
 }
 }	
        return count;
    }
    public long getuserinfo_A(String hashtag, long userid,  Timestamp start_time, Timestamp end_time){
     //returns number of tweets with given hashtag within specified time  
        long count=0;
        int j=0;
        for(j=0;j<5;j++){
        try{
 // Open the file that is the first 
 // command line parameter
 FileInputStream fstream = new FileInputStream(Filename[j]);
 //System.out.println(Filename[j]);
 // Get the object of DataInputStream
 DataInputStream in = new DataInputStream(fstream);
 BufferedReader br = new BufferedReader(new InputStreamReader(in));
 String strLine;
 int i=0;
 String time="";
 int mm;
 //Read File Line By Line
 while ((strLine = br.readLine()) != null)   {
 // Print the content on the console
 //System.out.println (strLine);
 // ArrayList <String> list= new Arraylist<String>();
    //       Scanner sc=new Scanner(strLine);
    //       while(sc.hasNext())
    //       {
    //       list.add(sc.next)
 	String [] list = strLine.split(" ");
 	long usr=Long.parseLong(list[0]);
 	if(hashtag.equalsIgnoreCase(list[7]) && usr==userid)
 	{
 		        time="";
                time=time+list[6]+"-";
                mm=month(list[2]);
                time=time+mm+"-"+list[3]+" "+list[4];
                Timestamp timestamp=Timestamp.valueOf(time);
                //System.out.println(timestamp.getTime());
                if((start_time.before(timestamp) && end_time.after(timestamp)) || start_time.equals(timestamp) || end_time.equals(timestamp))
                count++;
 	}
         }
 	
 //Close the input stream
 in.close();
   }catch (Exception e){//Catch exception if any
 System.err.println("Error: " + e.getMessage());
 }
 }	
        return count;
            
  }
   public long getuserinfo_B(String hashtag, long userid,  Timestamp start_time, Timestamp end_time){

            //returns number of tweets made by user outside this time with hashtag  
            long count=0;
            int j=0;
            for(j=0;j<5;j++){
           try{
 // Open the file that is the first 
 // command line parameter
 FileInputStream fstream = new FileInputStream(Filename[j]);
 // Get the object of DataInputStream
 DataInputStream in = new DataInputStream(fstream);
 BufferedReader br = new BufferedReader(new InputStreamReader(in));
 String strLine;
 int i=0;
 String time="";
 int mm;
 //Read File Line By Line
 while ((strLine = br.readLine()) != null)   {
 // Print the content on the console
 //System.out.println (strLine);
 // ArrayList <String> list= new Arraylist<String>();
    //       Scanner sc=new Scanner(strLine);
    //       while(sc.hasNext())
    //       {
    //       list.add(sc.next)
 	String [] list = strLine.split(" ");
 	long usr=Long.parseLong(list[0]);
 	if(hashtag.equalsIgnoreCase(list[7]) && usr==userid)
 	{
 		        time="";
                time=time+list[6]+"-";
                mm=month(list[2]);
                time=time+mm+"-"+list[3]+" "+list[4];
                Timestamp timestamp=Timestamp.valueOf(time);
                //System.out.println(timestamp.getTime());
                if(start_time.after(timestamp) || end_time.before(timestamp))
                count++;
 	}
         }
 	
 //Close the input stream
 in.close();
   }catch (Exception e){//Catch exception if any
 System.err.println("Error: " + e.getMessage());
 }
 }	
            return count;
  }
  public long getuserinfo_C(String hashtag, long userid,  Timestamp start_time, Timestamp end_time){

            //returns number of tweets made by user inside this time but not with hashtag  
           long count=0;
        int j=0;
        for(j=0;j<5;j++){
        try{
 // Open the file that is the first 
 // command line parameter
 FileInputStream fstream = new FileInputStream(Filename[j]);
 // Get the object of DataInputStream
 DataInputStream in = new DataInputStream(fstream);
 BufferedReader br = new BufferedReader(new InputStreamReader(in));
 String strLine;
 int i=0;
 String time="";
 int mm;
 //Read File Line By Line
 while ((strLine = br.readLine()) != null)   {
 // Print the content on the console
 //System.out.println (strLine);
 // ArrayList <String> list= new Arraylist<String>();
    //       Scanner sc=new Scanner(strLine);
    //       while(sc.hasNext())
    //       {
    //       list.add(sc.next)
 	String [] list = strLine.split(" ");
 	long usr=Long.parseLong(list[0]);
 	if((!(hashtag.equalsIgnoreCase(list[7]))) && usr==userid)
 	{
 		        time="";
                time=time+list[6]+"-";
                mm=month(list[2]);
                time=time+mm+"-"+list[3]+" "+list[4];
                Timestamp timestamp=Timestamp.valueOf(time);
                //System.out.println(timestamp.getTime());
                if((start_time.before(timestamp) && end_time.after(timestamp)) || start_time.equals(timestamp) || end_time.equals(timestamp))
                count++;
 	}
         }
 	
 //Close the input stream
 in.close();
   }catch (Exception e){//Catch exception if any
 System.err.println("Error: " + e.getMessage());
 }
 }	
        return count;
  }
 public long getuserinfo_D(String hashtag, long userid,  Timestamp start_time, Timestamp end_time){

            //returns number of tweets made by user outside this time and not  with hashtag  
           long count=0;
            int j=0;
            for(j=0;j<5;j++){
           try{
 // Open the file that is the first 
 // command line parameter
 FileInputStream fstream = new FileInputStream(Filename[j]);
 // Get the object of DataInputStream
 DataInputStream in = new DataInputStream(fstream);
 BufferedReader br = new BufferedReader(new InputStreamReader(in));
 String strLine;
 int i=0;
 String time="";
 int mm;
 //Read File Line By Line
 while ((strLine = br.readLine()) != null)   {
 // Print the content on the console
 //System.out.println (strLine);
 // ArrayList <String> list= new Arraylist<String>();
    //       Scanner sc=new Scanner(strLine);
    //       while(sc.hasNext())
    //       {
    //       list.add(sc.next)
 	String [] list = strLine.split(" ");
 	long usr=Long.parseLong(list[0]);
 	if((!(hashtag.equalsIgnoreCase(list[7]))) && usr==userid)
 	{
 		        time="";
                time=time+list[6]+"-";
                mm=month(list[2]);
                time=time+mm+"-"+list[3]+" "+list[4];
                Timestamp timestamp=Timestamp.valueOf(time);
                //System.out.println(timestamp.getTime());
                if(start_time.after(timestamp) || end_time.before(timestamp))
                count++;
 	}
         }
 	
 //Close the input stream
 in.close();
   }catch (Exception e){//Catch exception if any
 System.err.println("Error: " + e.getMessage());
 }
 }	
            return count;
 }
  }
