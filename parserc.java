
import java.io.File;
import java.io.FileOutputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
public class parserc {

	/**
	 * @param args
	 */
	public static String indexfile=null;
	public static void main(String[] args) 
	{	
		 try
		 {
		 	 String wikifile="/home/vinny/Desktop/20162029/sem3/IRE/project1/wiki-search-small.xml";
	         indexfile="";
			 File inputFile = new File(wikifile);
	         SAXParserFactory factory = SAXParserFactory.newInstance();
	         SAXParser saxParser = factory.newSAXParser();
	         UserHandler userhandler = new UserHandler();
	         saxParser.parse(inputFile, userhandler);     
	      } catch (Exception e) {
	         e.printStackTrace();
	      }

	}

}
