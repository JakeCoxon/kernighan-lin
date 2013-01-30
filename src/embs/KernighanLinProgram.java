package embs;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.MatchResult;

public class KernighanLinProgram {
  static public void main(String[] args) {
    
    try {
      new KernighanLinProgram();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
  }
  
  public KernighanLinProgram() throws IOException {

    FileReader fileReader = new FileReader("graph.txt");
    BufferedReader bufferedReader = new BufferedReader(fileReader);
    
    Graph g = fromReadable(bufferedReader);
    bufferedReader.close();
    
    KernighanLin k = KernighanLin.process(g);

    System.out.print("Group A: ");
    for (Vertex x : k.getGroupA())
      System.out.print(x);
    System.out.print("\nGroup B: ");
    for (Vertex x : k.getGroupB())
      System.out.print(x);
    System.out.println("");
    System.out.println("Cut cost: "+k.getCutCost());
  }
  
  public Graph fromReadable(Readable readable) {
    Graph graph = new Graph();
    HashMap<String, Vertex> names = new HashMap<String, Vertex>();
    
    Scanner s = new Scanner(readable);

    while(s.hasNext("\r|\n")) s.next("\r|\n");
    
    s.skip("vertices:");
    while (s.findInLine("([A-Z])") != null) {
      MatchResult match = s.match();
      
      String name = match.group(1);
      Vertex v = new Vertex(name);
      graph.addVertex(v);
      names.put(name, v);
    }

    s.skip("\nedges:");
    while (s.findInLine("([A-Z])([A-Z])\\(([0-9]+)\\)") != null) {
      MatchResult match = s.match();
      
      Vertex first = names.get(match.group(1));
      Vertex second = names.get(match.group(2));
      Integer weight = Integer.parseInt(match.group(3));
      graph.addEdge(new Edge(weight), first, second);
    }
    return graph;
  }
  
}
