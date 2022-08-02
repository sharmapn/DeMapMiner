package readRepository.postReadingUpdates;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class JSoupDownload {
	public static void main( String[] args )
	{
	    String url = "http://en.wikipedia.org/wiki/Big_data";
	    Document document;
	    try {
	        document = Jsoup.connect(url).get();
	        Elements paragraphs = document.select("p");
	        Element firstParagraph = paragraphs.first();
	        Element lastParagraph = paragraphs.last();
	        Element p;
	        int i=1;
	        p=firstParagraph;
	        System.out.println("*  " +p.text());
	        int paragraphCounter=0;
	        while (p!=lastParagraph){
	            p=paragraphs.get(i);
	            System.out.println(paragraphCounter+ " " +p.text());
	            i++;
	            paragraphCounter++;
	        } 
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	}
}
