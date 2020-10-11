import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Cracker: will read two files and emit user names and passwords program
 * assumes that two files exist in program directory:
 * 
 * 1. common-passwords.txt -- this is the dictionairy that is used to brute
 * force attempt to find the passwords for each users 2. shadow - file
 * containing 'user:sale:hash(salt+password)'
 * 
 * Shawn Cicoria - CS 645 - Project 1: Problem 1 sc2443@njit.edu /
 * shawn@cicoria.com
 * 
 * Octorber 12, 2020
 */

public class Cracker {

  static String s_password_file = "./shadow";
  static String s_dictionairy_file = "./common-passwords.txt";
  static MessageDigest messageHash = null;

  static String exp = "^(?=.*[a-z])[a-z]+$";
  static Pattern p = Pattern.compile(exp);

  static Path doneFile = null;

  public static void main(String[] args) throws Exception {

    if (args.length > 0) {
      s_dictionairy_file = args[0];
      System.out.println("using file for dict: " + args[0]);
    }

    var cr = new Cracker();

    cr.run();

    System.out.println("done with dict file: " + s_dictionairy_file);
  }

  public void run() throws NoSuchAlgorithmException, IOException {
    System.err.println("loading dict");
    var dict = getDictionairy();

    if (dict.size() == 0) {
      throw new InvalidParameterException("dictionairy file has no lines");
    }

    System.err.println("loading password file");
    var passwords = getPasswordLines();
    if (passwords.size() == 0) {
      throw new InvalidParameterException("password file has no lines");
    }

    var results = new ArrayList<String>(passwords.size());
    for (var item : passwords) {
      System.err.println("checking user " + item.user);

      doneFile = Paths.get(item.user + ".done");
      if (Files.exists(doneFile)) {
        System.err.println("done file for user exists: " + item.user + " done file: " + doneFile);
        continue;
      }
    
      boolean noMatch = true;
      for (var word : dict) {
        var targetHash = theHash(word, item.salt);

        if (Files.exists(doneFile)) {
          System.err.println("done file for user exists: " + item.user + " done file: " + doneFile);
          break;
        }

        if (targetHash.compareTo(item.hashValue) == 0) {
          results.add(item.user + ":" + word);
          System.out.println("\tSUCCESS: " + item.user + ":" + word);

          FileWriter myWriter = new FileWriter(item.user + ".done");
          myWriter.write(item.user + ":" + word);
          myWriter.close();

          File doneFile_output = new File(item.user + ".done");
          doneFile_output.createNewFile();
          System.err.println("wrote done file: " + doneFile_output.getName());
          noMatch = false;
          break; // jump to next password
        }
      }
      if (noMatch)
        System.err.println("did not get password for user: " + item.user);
    }

    emitMatches(results);
  }

  void emitMatches(ArrayList<String> matches) {
    for(var match:matches) {
      System.out.println(match);
    }
  }

  static String theHash(String password, String salt) throws NoSuchAlgorithmException {

    String rv = null;
    try {
      rv = MD5Shadow.crypt(password, salt);
    } catch (ArrayIndexOutOfBoundsException e) {
      System.err.println("Failed to decrypt a user - but in MD5Shadow code. password: " + password + "   Salt: " + salt);
      rv = "failed-to-decrypt";
    }
    return rv;
  }

  static String toHex(byte[] bytes) {
    BigInteger bi = new BigInteger(1, bytes);
    return String.format("%0" + (bytes.length << 1) + "X", bi);
  }

  ArrayList<String> getDictionairy() {
    var words = new ArrayList<String>();
    try {
      Scanner scanner = new Scanner(new File(s_dictionairy_file));
      while (scanner.hasNextLine()) {
        var word = scanner.nextLine().trim();
        if ( isAlphaNumeric(word) && word.length() < 16) {
          words.add(word);
        }
      }
    } catch (FileNotFoundException e) {
      System.out.println("dictionairy file: " + s_dictionairy_file + " was not found");
      e.printStackTrace();
    }
    return words;
  }

  ArrayList<LineFormat> getPasswordLines() {
    var passwords = new ArrayList<LineFormat>();
    try {
      Scanner scanner = new Scanner(new File(s_password_file));
      while (scanner.hasNextLine()) {
        String[] parts = scanner.nextLine().split(":");
        String[] pass_parts = parts[1].split("\\$");;

        if (pass_parts.length == 4) {
          passwords.add(new LineFormat(parts[0], pass_parts[2], pass_parts[3]));
        }
      }
    } catch (FileNotFoundException e) {
      System.out.println("password file: " + s_password_file + " was not found");
      e.printStackTrace();
    }
    return passwords;
  }

  ArrayList<String> getPasswordLinesString() {
    var passwords = new ArrayList<String>();
    try {
      Scanner scanner = new Scanner(new File(s_password_file));
      while (scanner.hasNextLine()) {
        passwords.add(scanner.nextLine());
      }
    } catch (FileNotFoundException e) {
      System.out.println("password file: " + s_password_file + " was not found");
      e.printStackTrace();
    }
    return passwords;
  }

  static boolean isAlphaNumeric(String str) 
  { 
      // return false 
      if (str == null) { 
          return false; 
      } 

      // Pattern class contains matcher() method 
      // to find matching between given string 
      // and regular expression. 
      Matcher m = p.matcher(str); 

      // Return if the string 
      // matched the ReGex 
      return m.matches(); 
  } 


  class LineFormat {
    public String user;
    public String salt;
    public String hashValue;

    public LineFormat(String user, String salt, String hashValue) {
      this.user = user;
      this.salt = salt;
      this.hashValue = hashValue;
    }
  }
}
