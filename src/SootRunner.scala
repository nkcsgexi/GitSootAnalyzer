import java.io.File
import soot.jimple.parser.Parse
import soot.jimple.parser.parser.Parser
import soot._
import soot.toolkits.scalar.ForwardFlowAnalysis
import soot.toolkits.graph.UnitGraph
import soot.toolkits.graph.DirectedGraph

object SootRunner{

  def main(args: Array[String]) {
    //parser("java.io.File")
    runSootCommand
  }

  private def parser(name:String)
  {
    val clazz = Scene.v.loadClassAndSupport(name)
    val it = clazz.methodIterator
    while(it.hasNext) {
      val method = it.next
      println(method.getDeclaration)
      analyzeMethod(method)
    }
  }

  private def analyzeMethod(method : SootMethod)
  {
    if(method.hasActiveBody) {
      println("HAS ACTIVE BODY")
      analyzeActiveBody(method.getActiveBody)
    }
  }

  private def analyzeActiveBody(body : Body) {
    body.getDefBoxes.toArray.map(db => db.asInstanceOf[ValueBox].getValue.toString()).foreach(println)
  }


  private def runSootCommand = {
    val path = "/home/xige/Desktop/re/bin/"
    val javaCp = "/home/xige/eclipse_workspace/ByteCodeAnalyzer/libs/sootclasses-2.3.0.jar:" +
      "/home/xige/eclipse_workspace/ByteCodeAnalyzer/libs/jasminclasses-2.3.0.jar:" +
      "/home/xige/eclipse_workspace/ByteCodeAnalyzer/libs/polyglotclasses-1.3.5.jar"
    val sootCp = ".:/usr/lib/jvm/java-7-openjdk-amd64/jre/lib:" +
      "/usr/local/share/scala/lib/scala-actors.jar:" +
      "/usr/local/share/scala/lib/scala-library.jar:" +
      "/usr/local/share/scala/lib/scala-swing.jar"
    val builder = new ProcessBuilder
    builder.directory(new File(path))
    builder.command("java", "-cp", javaCp, "soot.Main", "-cp", sootCp, "-pp", "-f", "j", "ScalaHello$")
    builder.redirectError(new File(path + "log"))
    builder.redirectOutput(new File(path + "log"))
    builder.start()
  }

}		


