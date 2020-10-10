public class Test {
  public static void main(String[] args) {
    for(var s:args){
      System.out.println(s);
    }

    for(int i = 0; i < args.length; i++){
      System.out.println(i + ":" + args[i]);
    }
  }
}
