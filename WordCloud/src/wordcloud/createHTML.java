/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wordcloud;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Samprit
 */
public class createHTML {

    public String startHTML, endHTML,HTML,locations;
    
    public createHTML(String fileName) {
        initHTML();
        //finishHTMLLatLong();
        finishHTMLGeoCode();
        createLocations("locations.txt");
        HTML = startHTML+locations+endHTML;
        
        writeToFile(fileName);
    }
    
    public void writeToFile(String fileName){
        FileWriter fWriter = null;
        BufferedWriter writer = null;
        try {
            fWriter = new FileWriter(fileName);
            writer = new BufferedWriter(fWriter);            
            writer.write(HTML);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void initHTML(){
        startHTML = "<!DOCTYPE html>\n"
                    +"<html>\n <head>\n"
                + "<meta name=\"viewport\" content=\"initial-scale=1.0, user-scalable=no\">"
                +"\n<meta charset=\"utf-8\">\n<title>Twitter Map</title>"
                +"\n<style>html, body, #map-canvas {height: 100%; margin: 0px;padding: 0px}</style>"
                +"\n<script src=\"https://maps.googleapis.com/maps/api/js?v=3.exp&sensor=false\"></script>"
                +"\n<script>"
                +"\nfunction initialize() {\nvar locations = [\n";
    }
    public void finishHTMLLatLong(){
        endHTML = "\n];\nvar mapOptions = {zoom: 2,center: new google.maps.LatLng(41.926979, 12.517385)"
                  +"} \nvar map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);"
                  +"\nvar infowindow = new google.maps.InfoWindow();"  
                  +"\nfor (i = 0; i < locations.length; i++) {"
                  +"\nmarker = new google.maps.Marker({"
                 +"position: new google.maps.LatLng(locations[i][1], locations[i][2]),map: map});"
                 +"google.maps.event.addListener(marker, 'click', (function (marker, i) {"
                +"return function () {"
                +"infowindow.setContent(locations[i][0]);"
                +"infowindow.open(map, marker);"
                +"}})(marker, i));}}"
                +"\ngoogle.maps.event.addDomListener(window, 'load', initialize);"
               +"\n</script>\n</head>\n<body>\n <div id=\"map-canvas\"></div>\n </body>\n</html>";
    }
    
    public void finishHTMLGeoCode(){
        endHTML = "\n];var mapOptions = {zoom: 2,center: new google.maps.LatLng(41.926979, 12.517385)} "
                 +"\nvar map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);"
                +"\nvar infowindow = new google.maps.InfoWindow();"
                +"\nvar geocoder =  new google.maps.Geocoder();"
                +"\nvar address=\"\";"
                +"\nfor (i = 0; i < locations.length; i++) {"
                +"\naddress = locations[i][0];"
                +"\ngeocoder.geocode( { 'address': address}, function(results, status) {"
                +"\nif (status == google.maps.GeocoderStatus.OK) {"
                +"\nvar marker = new google.maps.Marker({"
                +"\nmap: map,"
                +"\nposition: results[0].geometry.location"
                +"\n});"
                +"\n}"
                +"\n});}}"
                +"\ngoogle.maps.event.addDomListener(window, 'load', initialize);"
                +"\n</script>"
                +"\n</head>"
                +"\n<body>"
                +"\n<div id=\"map-canvas\"></div>"
                +"\n</body>"
                +"\n</html>";
    }
    
    public void createLocations(String fileName){
        BufferedReader reader;
        locations="";
        try {
            reader = new BufferedReader(new FileReader(fileName));
            String line = null;
            while ((line = reader.readLine()) != null) {
                //String[] parts = line.split("\\s");
                //locations += "\n['"+parts[0]+"',"+ parts[1]+","+ parts[2]+"],";
                locations += "\n['"+line+"',"+ 1+","+ 2+"],";
            }
            locations = locations.substring(0, locations.length()-1);
            //System.out.println(locations);
        } catch (Exception ex) {
            Logger.getLogger(WordCloud.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
