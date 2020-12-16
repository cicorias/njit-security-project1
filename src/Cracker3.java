
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileReader;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Cracker3 {

    public static void main(String[] args) throws FileNotFoundException, NoSuchAlgorithmException, IOException {

        // Instantiating Simple Cracker Object
        Cracker3 cracker = new Cracker3();

        // Create String Arrays of shadow-simple and common-passwords files
        String[] shadow = cracker.parse_file("shadow");
        String[] passwords = cracker.parse_file("common-passwords.txt");

        // Instantiate Salt String Array
        String salt; 

        // Loop to process all users in shadow file
        int match_count = 0;
        for (int i = 0; i < shadow.length; i++) {

            //Extract salt using regex split
            salt = shadow[i].split("[$:]")[3].trim();
            for (int j = 0; j < passwords.length; j++) {

                //Use MD5Shadow crypt method to create concatenated hash
                String MD5 = MD5Shadow.crypt(passwords[j], salt);

                //conditional to check for matches between concatenated hash and user password hashes
                if (MD5.equals(shadow[i].split("[$:]")[4].trim())) {
                    System.out.println(shadow[i].split("[:]")[0].trim() + ":" + passwords[j]);
                    match_count++;
                    break;
                }
            }
        }
        if (match_count == 0) {
            //Checks for a match count and prints out No Matches Found.
            System.out.println("No Matches Found.");
        }

    }
    
    //Converts a byte array into a String that contains the hexadecimal representation of the byte array
    public static String toHex(byte[] data) {

        BigInteger bi = new BigInteger(1, data);
        return String.format("%0" + (data.length << 1) + "X", bi);
    }

    // Convert text file to an Array of strings
    public String[] parse_file(String filename) throws IOException {

        Scanner scanner = new Scanner(new FileReader(filename));
        
        List<String> file = new ArrayList<String>();

        while (scanner.hasNextLine()) {
            file.add(scanner.nextLine());
        }

        scanner.close();
        String[] array = file.toArray(new String[0]);
        return array;

    }

}