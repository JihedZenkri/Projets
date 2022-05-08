package up.visulog.analyzer;

import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.List;
import java.util.Map; 
import java.util.Set; 
import java.util.Iterator;
import java.util.Date;
import java.util.Calendar;
import java.util.TreeMap;
import java.util.Collections;
public class DateChart {
	private Map<String,Integer> data;
	private static int id=0;
	
	DateChart(LinkedList<String>l){
		data=transform(l);
		id++;
	}
	
	DateChart(Map<String,Integer>m){
		data=m;
		id++;
	}
	
/*	private static int count(LinkedList<String>l) {
		int n=0;
		for(int i=0;i<l.size();i++) {
			
		}
	}
*/
	
	private static Map<String,Integer> transform(LinkedList<String>l){
		Map<String,Integer> map=new HashMap<>();
		for(String a : l) {
		  int n=map.getOrDefault(a, 0);
		  map.put(a, n+1);
		}
		return map;
	}
	
	/*
	private static Map<String,Integer> transform(LinkedList<String>l){
		Map<String,Integer> map=new HashMap<>();
		for(int i=l.size()-1;i>=0;i--) {
			String a=l.get(i);
			if(map.containsKey(a)) map.put(a, map.get(a)+1);
			else map.put(a, 1); 
		}
		return map;
	}
	*/
	
	
	
	public String drawChart() {
		this.data=new TreeMap<String,Integer>(data);
		int max = Collections.max(data.values());
		max = (max > 5 ? max : 5);
		StringBuilder chart= new StringBuilder("<script type=\"text/javascript \">");
		chart.append("window.addEventListener(\"load\", function () { var c");
    	chart.append(Integer.toString(id));
    	chart.append("=new CanvasJS.Chart(\"");
    	chart.append(this.getClass()+Integer.toString(id));
    	chart.append("\",{title:{text: \"\"},axisX:{title:\"Dates\",gridThickness:2},axisY:{title:\"Commits\",maximum:").append(Integer.toString(max+1));
    	chart.append("},data: [ { type:\"area\", dataPoints:[");
		for(var i: data.entrySet()) {
			chart.append("{x: new Date(").append(i.getKey().substring(0, 4)).append(",");
			//System.out.println(i.getKey()+" "+i.getValue());
			String s=i.getKey().substring(5,7);
			int n=Integer.parseInt(s)-1;
			chart.append(Integer.toString(n));
			chart.append(",").append(i.getKey().substring(8,10)).append("),y:").append(i.getValue().toString()).append("},");
		}
		Date today = Calendar.getInstance().getTime();
		chart.append("{x:new Date(").append(Integer.toString(today.getYear()+1900)).append(",").append(Integer.toString(today.getMonth())).append(",").append(Integer.toString(today.getDate())).append("),");
		chart.append("y:0}]}]});");
		chart.append("c"+Integer.toString(id)+".render();");
    	chart.append("}); </script><div id=\"");
    	chart.append(this.getClass()+Integer.toString(id));
    	chart.append("\" style=\"height: 400px; width: 90%;\">");
    	chart.append("</div>");
    	return chart.toString();
	}
}

