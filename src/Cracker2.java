import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Cracker2 {

	public static void main(String[] a){
		int i;
		MD5Shadow shadow = new MD5Shadow();
		List<String> commonpasswords = new ArrayList<String>();
		List<String> saltedhashentire= new ArrayList<String>();
		List<String> saltedhash= new ArrayList<String>();
		List<String> returnfromshadow = new ArrayList<String>();
		
		Scanner commonpass=null;
		Scanner salt=null;
		
		try {
			commonpass = new Scanner(new File("common-passwords.txt"));
			while(commonpass.hasNext()){
				commonpasswords.add(commonpass.next());
			}
			salt = new Scanner(new File("shadow-test2")).useDelimiter("\n");
			while(salt.hasNext()){
				saltedhashentire.add(salt.next());
			}
			String returnval=null;
			for(int j=0;j<saltedhashentire.size();j++){
				saltedhash.add(saltedhashentire.get(j).substring(9,17));
			}
			for(i=0;i<commonpasswords.size();i++)
			{
				for(int j1=0;j1<saltedhash.size();j1++){
			returnval =shadow.crypt(commonpasswords.get(i).toString(), saltedhash.get(j1));
			returnfromshadow.add(returnval);
				}
				}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		
		for(int j=0;j<saltedhashentire.size();j++){
		for(int fromshadow=0;fromshadow<returnfromshadow.size();fromshadow++){		
			if(saltedhashentire.get(j).substring(18, 40).contains(returnfromshadow.get(fromshadow))){
				
				for(i=0;i<commonpasswords.size();i++)
				{
					for(int j1=0;j1<saltedhash.size();j1++){
				String returnval =shadow.crypt(commonpasswords.get(i).toString(),saltedhash.get(j1));
					if(returnval.contains(saltedhashentire.get(j).substring(18, 40)) ){
				System.out.println(saltedhashentire.get(j).substring(0,5)+":"+commonpasswords.get(i));	
			
					}
					}
				}
			}
		}
		}
	}
}