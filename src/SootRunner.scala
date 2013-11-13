import com.sun.org.apache.bcel.internal.classfile.Signature.MyByteArrayInputStream
import java.io.{BufferedInputStream, FileInputStream, File}
import soot.jbco.bafTransformations.FindDuplicateSequences
import soot.jimple.parser.{JimpleAST, Parse}
import soot.jimple.parser.parser.Parser
import soot._
import soot.jimple.toolkits.annotation.arraycheck.ArrayBoundsChecker
import soot.jimple.toolkits.annotation.nullcheck.NullPointerChecker
import soot.tagkit.{KeyTag, Tag}
import soot.toolkits.scalar.ForwardFlowAnalysis
import soot.toolkits.graph.UnitGraph
import soot.toolkits.graph.DirectedGraph

object SootRunner{


  val sootCp = ".:/usr/lib/jvm/java-7-openjdk-amd64/jre/lib:" +
    "/usr/local/share/scala/lib/scala-actors.jar:" +
    "/usr/local/share/scala/lib/scala-library.jar:" +
    "/usr/local/share/scala/lib/scala-swing.jar"


  def main(args: Array[String]) {
    //parser("java.io.File")
    addTransformer
    runSootCommand
    //parseJimple("JavaHello.jimp")
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

    val dup = new FindDuplicateSequences
    val arrChecker = ArrayBoundsChecker.v
    var nullChecker = NullPointerChecker.v
    PackManager.v.getPack("jtp").add(new Transform("jtp.mytransform1", arrChecker))
    PackManager.v.getPack("jtp").add(new Transform("jtp.mytransform2", nullChecker))
    PackManager.v.getPack("jtp").add(new Transform("jtp.mytransform", MyTransformer))

  }

  private object MyTransformer extends BodyTransformer
  {
    private var count = 0

    override protected def internalTransform(body : soot.Body, s : String, map : java.util.Map[_,_]) = {
      count += 1
      val units = body.getUnits.toArray(Array.empty[Unit]).filter(u => !u.branches())
      //units.foreach(u => println(u,toString))
      val defBoxes = units.flatMap(u => u.getDefBoxes.toArray(Array.empty[ValueBox]))
      val useBoxes = units.flatMap(u => u.getUseBoxes.toArray(Array.empty[ValueBox]))
      units.foreach(unit => {
        val tags = unit.getTags.toArray(Array.empty[Tag])
        val names = tags.map(t => t.getName)
        names.foreach(println)
      })

      /*units.foreach(unit => {
        println("Is fall through: " + unit.fallsThrough)
        print("Defined: ")
        unit.getDefBoxes.toArray(Array.empty[ValueBox]).foreach(v => print(v.getValue))
        println()
        print("Used: ")
        unit.getUseBoxes.toArray(Array.empty[ValueBox]).foreach(v => print(v.getValue))
        println()
      })*/
    }
  }

  private def parseJimple(path : String) =
  {
    soot.Main.main(Array("-cp", sootCp, "-src-prec", "jimple", "-app", path))
  }


  private def runSootCommand = {
    val args = Array("-cp", sootCp, "-pp", "-f", "j", "JavaHello")
    soot.Main.main(args)
  }

}		


