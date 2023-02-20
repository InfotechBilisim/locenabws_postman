package com.infotech;

public class XmlParser {
    public int posStart = 0;
    
    public XmlParser() {}
    
    public XmlParser(int posStart)
    {
      this.posStart = posStart;
    }
    
    public String getNodeDataInAll(String xml, String nodeName)
    {
      String strSearch = nodeName + ">";
      int posTail = xml.indexOf(strSearch);
      if (posTail < 0) {
        return null;
      }
      int posBegCheck = xml.lastIndexOf("<", posTail);
      if (posBegCheck < 0) {
        return null;
      }
      if (xml.charAt(posBegCheck + 1) == '/')
      {
        strSearch = nodeName + " ";
        posTail = xml.indexOf(strSearch);
        if (posTail < 0) {
          return null;
        }
        int posTailEnd = xml.indexOf(">", posTail);
        if (posTailEnd < 0) {
          return null;
        }
        strSearch = xml.substring(posTail, posTailEnd + 1);
        if (strSearch.indexOf("<") >= 0) {
          return null;
        }
      }
      int posBeg = posTail + strSearch.length();
      int pos = xml.lastIndexOf("<", posTail);
      if (pos < 0) {
        return null;
      }
      String strEnd = xml.substring(pos, pos + 1) + "/" + xml.substring(pos + 1, posBeg);
      pos = strEnd.indexOf(" ");
      if (pos > 0) {
        strEnd = strEnd.substring(0, pos) + ">";
      }
      int posEnd = xml.indexOf(strEnd, posBeg);
      if (posEnd < 0) {
        return null;
      }
      String nodeData = xml.substring(posBeg, posEnd);
      return nodeData;
    }
    
    public String getNodeData(String xml, String nodeName)
    {
      String strSearch = nodeName + ">";
      int posTail = xml.indexOf(strSearch, this.posStart);
      if (posTail < 0) {
        return null;
      }
      int posBegCheck = xml.lastIndexOf("<", posTail);
      if (posBegCheck < 0) {
        return null;
      }
      if (xml.charAt(posBegCheck + 1) == '/')
      {
        strSearch = nodeName + " ";
        posTail = xml.indexOf(strSearch);
        if (posTail < 0) {
          return null;
        }
        int posTailEnd = xml.indexOf(">", posTail);
        if (posTailEnd < 0) {
          return null;
        }
        strSearch = xml.substring(posTail, posTailEnd + 1);
        if (strSearch.indexOf("<") >= 0) {
          return null;
        }
      }
      int posBeg = posTail + strSearch.length();
      int pos = xml.lastIndexOf("<", posTail);
      if (pos < 0) {
        return null;
      }
      String strEnd = xml.substring(pos, pos + 1) + "/" + xml.substring(pos + 1, posBeg);
      pos = strEnd.indexOf(" ");
      if (pos > 0) {
        strEnd = strEnd.substring(0, pos) + ">";
      }
      int posEnd = xml.indexOf(strEnd, posBeg);
      if (posEnd < 0) {
        return null;
      }
      String nodeData = xml.substring(posBeg, posEnd);
      this.posStart = (posEnd + strEnd.length());
      
      return nodeData;
    }
    
    public int getPosStart()
    {
      return this.posStart;
    }
    
    public void setPosStart(int posStart)
    {
      this.posStart = posStart;
    }

}
