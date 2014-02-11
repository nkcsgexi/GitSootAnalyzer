import java.io.File
import org.apache.commons.io.FileUtils
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.{Repository, RepositoryBuilder}
import org.eclipse.jgit.revwalk.{RevCommit, RevWalk}
import org.eclipse.jgit.treewalk.TreeWalk
import scala.Int
import scala.Predef._

object GitDownloader {

  val index = 1
  val projectName = List("junit", "fbreader")(index)
  val directory = List("/home/xige/Desktop/junit-study/", "/media/xige/My Passport/fbreaders/")(index)
  val headHash = List("1c6c16160c572c6d6f38a7b2b11cb23bb1dd2575", "740e672a83c84e03cd02caeabbac7c1777eabee9")(index)
  val url = List("https://github.com/junit-team/junit.git", "https://github.com/geometer/FBReaderJ.git")(index)

  def main(args : Array[String]) {
    val path =  directory + projectName
    cloneRemoteRepo(url, path)
 //   return
    val commitNames = collectCommitNames(path)
    println(commitNames.length)
    for(i <- 0 to 300) {
      val newPath = path + i
      checkout(path, commitNames(i))
      FileUtils.copyDirectory(new File(path), new File(newPath))
    }
  }


  def cloneRemoteRepo(url : String, path: String) = {
    val directory = new File(path + "/")
    if(directory.exists()) FileUtils.deleteDirectory(directory)
    Git.cloneRepository()
    .setURI(url)
    .setDirectory(directory).setBranch("master")
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
    return names.reverse
  }



  def checkout(folder : String, name : String) = {
      println(name)
      val git = Git.open(new File(folder))
      git.checkout.setName(name).call()
  }
}
