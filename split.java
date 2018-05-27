import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;

public class split {


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FileInputStream instream = null;
		PrintWriter outstream = null;
		char cnt='0';
		try {
			outstream = new PrintWriter(new BufferedWriter(new FileWriter("index"+cnt)));
			instream = new FileInputStream("/home/vinny/Desktop/IRE/merged/merged0");
		} catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(instream));
		String line;
		try {
			while((line=in.readLine())!=null)
			{
				if(line.charAt(0)!=cnt)
				{
					outstream.close();
					cnt=line.charAt(0);
					outstream = new PrintWriter(new BufferedWriter(new FileWriter("index"+cnt)));

				}
				outstream.write(line+"\n");
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}

	}

}
