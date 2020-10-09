import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Cracker: will read two files and emit user names and passwords
 * program assumes that two files exist in program directory:
 * 
 * 1. common-passwords.txt -- this is the dictionairy that is used to brute
 * force attempt to find the passwords for each users 
 * 2. shadow - file containing 'user:sale:hash(salt+password)'
 * 
 * Shawn Cicoria - CS 645 - Project 1: Problem 1
 * sc2443@njit.edu / shawn@cicoria.com
 * 
 * Octorber 12, 2020
 */

public class Cracker {

  static String s_password_file = "./shadow";
  static String s_dictionairy_file = "./common-passwords.txt";
  static MessageDigest messageHash = null;

  public static void main(String[] args) throws Exception {

    var cr = new Cracker();

    cr.run();

    System.out.println("done....");
  }

  public void run() throws NoSuchAlgorithmException {
    var dict = getDictionairy();

    if (dict.size() == 0) {
      throw new InvalidParameterException("dictionairy file has no lines");
    }

    var passwords = getPasswordLines();
    if (passwords.size() == 0) {
      throw new InvalidParameterException("password file has no lines");
    }

    var results = new ArrayList<String>(passwords.size());
    for (var item : passwords) {
      boolean noMatch = true;
      for (var word : dict) {
        var targetHash = theHash(word, item.salt);

        if (targetHash.compareTo(item.hashValue) == 0) {
          results.add(item.user + ":" + word);
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

    String rv = MD5Shadow.crypt(password, salt);
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
        if (word.length() > 0) {
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