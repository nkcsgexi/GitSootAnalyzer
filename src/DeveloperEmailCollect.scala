import java.io.File
import org.apache.commons.io.FileUtils
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.RepositoryBuilder
import org.eclipse.jgit.revwalk.RevWalk
import scala.Predef._

object DeveloperEmailCollect {

  // The remote git repo url
  val url = "https://gerrit.googlesource.com/gerrit"

  // The directory where the git repo shall be cloned.
  var directory = "/home/xige/Desktop/Untitled Folder/gerrit/"

  def main(args : Array[String]) {
    //cloneRemoteRepo(url, directory)
    val developers = collectCommitNames(directory)
    val sb = new StringBuilder
    developers.foreach(d => {
      sb.append(d.toString()).append('\n')
    })
    val data = sb.toString()
    println(data)
    FileUtils.write(new File(directory + "developers.csv"), data)
  }

  class developerInfo(email: String, firstName: String, lastName: String) {
    def getEmail() : String = email
    def getFirstName() : String = firstName
    def getLastName() : String = lastName
    override def toString(): String = email + ", " + firstName;
    override def hashCode = email.hashCode
    override def equals(other: Any) : Boolean = {
      val o:developerInfo = other.asInstanceOf[developerInfo]
      return o.getEmail().equals(this.getEmail())
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

  def collectCommitNames(path : String) : List[developerInfo] = {
    var developers = List.empty[developerInfo]
    val repository = new RepositoryBuilder().findGitDir(new File(path)).build()
    val walk = new RevWalk(repository)
    walk.markStart(walk.parseCommit(repository.resolve("HEAD")))
    val it = walk.iterator()

    while(it.hasNext) {
      val commit = it.next()
      val email = commit.getAuthorIdent.getEmailAddress
      val name = commit.getAuthorIdent.getName
      val subNames = name.split("\\s+")
      developers = developers :+ new developerInfo(email, subNames.head, subNames.last)
    }
    return developers.distinct
  }
}
