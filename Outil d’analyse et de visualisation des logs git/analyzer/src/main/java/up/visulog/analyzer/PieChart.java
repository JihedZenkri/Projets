package up.visulog.analyzer;
import up.visulog.config.Configuration;
import up.visulog.gitrawdata.Commit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

public class PieChart {
	
	private final Map<String,Integer> data;
	private static int id=0;
	
	PieChart(Map<String,Integer>m){
		data=m;
		id++;
	}
	
	//nombre total de values
	public int countAll() {
		int n=0;
		for(var i: data.entrySet()) n+=(int) i.getValue();
		return n;
	}
	
	//canvasJS sous la forme d'un String
	//crée le graphique 
	
	public String drawChart() {
		double total=0;
		for(var i:data.entrySet()) total+= (double)i.getValue();
		StringBuilder chart= new StringBuilder("<script type=\"text/javascript \">");
    	chart.append("window.addEventListener(\"load\", function () { var c");
    	chart.append(Integer.toString(id));
    	chart.append("=new CanvasJS.Chart(\"");
    	chart.append(this.getClass()+Integer.toString(id));
    	chart.append("\",{title:{text: \"\"},data: [ { type:\"pie\", dataPoints:[");
    	for(Map.Entry<String, Integer> me: data.entrySet()) {
    		double n=(double) me.getValue();
    		chart.append("{ label: \" ").append(me.getKey()).append("\", y:").append(Double.toString((n*100)/total)).append("},");
    	}
    	chart.append("{label:\"\",y:0}]}]});");
    	chart.append("c"+Integer.toString(id)+".render();");
    	chart.append("}); </script><div id=\"");
    	chart.append(this.getClass()+Integer.toString(id));
    	chart.append("\" style=\"height: 370px; width: 570px;\"><p>Total:\"");
    	chart.append(Double.toString(total));
    	chart.append("</p></div>");
    	return chart.toString();
	}
	
	
	
}
