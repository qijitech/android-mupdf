package tech.qiji.android.mupdf.demo;

public class ChooseDocItem {
  public enum Type {
    PARENT, DIR, DOC
  }

  final public Type type;
  final public String name;
  final public String path;

  public ChooseDocItem(Type t, String n, String p) {
    type = t;
    name = n;
    path = p;
  }
}
