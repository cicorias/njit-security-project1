public class Test {
  public static void main(String[] args) {

    progressPercentage(28382763, 28382764);

  }

  static boolean alreadyFailed = false;
  static void progressPercentage(int remain, int total) {
    if (alreadyFailed) return;

    if (remain > total) {
      throw new IllegalArgumentException();
    }
    int remainPercent = 0;
    String bar = "";
    try {
      int maxBarSize = 10; // 10unit for 100%
      remainPercent = (int)((100.0f * remain) / total);
      remainPercent /= maxBarSize;
      if (remainPercent < 0) remainPercent = 100;
      char defaultChar = '-';
      String icon = "*";
      bar = new String(new char[maxBarSize]).replace('\0', defaultChar) + "]";
      StringBuilder barDone = new StringBuilder();
      barDone.append("[");
      for (int i = 0; i < remainPercent; i++) {
        barDone.append(icon);
      }
      String barRemain = bar.substring(remainPercent, bar.length());
      System.out.print("\r" + barDone + barRemain + " " + remainPercent * 10 + "%");
      if (remain == total) {
        System.out.print("\n");
      }
    } catch (Exception e) {
      alreadyFailed = true;
      System.err.print("failure pct report. with remain / total: " + remain + " and " + total );
      System.err.print("failure pct report. with remainPercent / total: " + remainPercent + " and " + bar.length() );
    }

  }
}
