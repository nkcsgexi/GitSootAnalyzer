import java.io.File
import org.apache.commons.io.FileUtils
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.{Repository, RepositoryBuilder}
import org.eclipse.jgit.revwalk.{RevCommit, RevWalk}
import org.eclipse.jgit.treewalk.TreeWalk
import scala.Int
import scala.Predef._

object GitDownloader {

  var projectName = "junit"
  var revisonEnd = 1
  var directory = "/home/xige/Desktop/"
  var headHash = "c6ab1aff3076a4335554699ec64f5b02ff1f654c"

  def main(args : Array[String]) {
    val url = "https://github.com/junit-team/junit.git"
    val path =  directory + projectName
   //cloneRemoteRepo(url, path)
    val commitNames = collectCommitNames(path)
    println(commitNames.length)
    for(i <- 1 to 10) {
      val newPath = path + i
      FileUtils.copyDirectory(new File(path), new File(newPath))
      checkout(newPath, commitNames(i))
    }
  }

  def d (count: Int, commit: RevCommit, previousPath : String) :String = {
    if(count <= revisonEnd){
      val id = commit.toObjectId
      val name = id.getName
      val newPath = directory + projectName + count
      FileUtils.copyDirectory(new File(previousPath), new File(newPath))
      checkout(newPath, name)
      return newPath
    } else
      return previousPath
  }





  def cloneRemoteRepo(url : String, path: String) = {
    val directory = new File(path)
    if(directory.exists()) FileUtils.deleteDirectory(directory)
    Git.cloneRepository()
    .setURI(url)
    .setDirectory(directory).setBranch("remotes/origin/master")
    .call()
  }

  def collectCommitNames(path : String) : List[String] = {
    var names = List.empty[String]
    val repository = new RepositoryBuilder().findGitDir(new File(path)).build()
    val walk = new RevWalk(repository)
    walk.markStart(walk.parseCommit(repository.resolve(headHash)))
    val it = walk.iterator()

    while(it.hasNext) {
      val commit = it.next()
      names = names :+ commit.name()
      println(commit.name())
    }
    println(names.length)
    return names
  }



  def checkout(folder : String, name : String) = {
      println(name)
      val git = Git.open(new File(folder))
      val command = git.checkout.setName(name).setForce(true)
      command.call
  }
}
