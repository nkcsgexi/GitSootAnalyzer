import com.sun.org.apache.bcel.internal.classfile.Signature.MyByteArrayInputStream
import java.io.{BufferedInputStream, FileInputStream, File}
import soot.jimple.parser.{JimpleAST, Parse}
import soot.jimple.parser.parser.Parser
import soot._
import soot.toolkits.scalar.ForwardFlowAnalysis
import soot.toolkits.graph.UnitGraph
import soot.toolkits.graph.DirectedGraph

object SootRunner{

  def main(args: Array[String]) {
    //parser("java.io.File")
    addTransformer
    runSootCommand
    val path = "/home/xige/Desktop/re/bin/sootOutput/JavaHello.jimp"
    //parseJimple(path)
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


  private def addTransformer()
  {
    PackManager.v.getPack("jtp").add(new Transform("jtp.mytransform", MyTransformer))

  }

  private object MyTransformer extends BodyTransformer
  {
    override protected def internalTransform(body : soot.Body, s : String, m : java.util.Map[_,_]) = {
      val units = body.getUnits.toArray(Array.empty[Unit]).filter(u => !u.branches())
      units.foreach(u => println(u,toString))
      val defBoxes = units.flatMap(u => u.getDefBoxes.toArray(Array.empty[ValueBox]))
      val useBoxes = units.flatMap(u => u.getUseBoxes.toArray(Array.empty[ValueBox]))
      units.foreach(unit => {
        print("Defined: ")
        unit.getDefBoxes.toArray(Array.empty[ValueBox]).foreach(v => print(v.getValue))
        println()
        print("Used: ")
        unit.getUseBoxes.toArray(Array.empty[ValueBox]).foreach(v => print(v.getValue))
        println()
      })
    }

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

    var args = Array("-cp", sootCp, "-pp", "-f", "j", "ScalaHello$")
    soot.Main.main(args)
  }

}		


