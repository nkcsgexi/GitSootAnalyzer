import java.io.File
import org.apache.commons.io.FileUtils
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.{Repository, RepositoryBuilder}
import org.eclipse.jgit.revwalk.{RevCommit, RevWalk}
import org.eclipse.jgit.treewalk.TreeWalk


object GitDownloader {

  def main(args : Array[String]) = {
    val url = "https://github.com/nkcsgexi/JVMBinaryAnalyzer.git"
    val path = "/home/xige/Desktop/re"
    // cloneRemoteRepo(url, path)
    traverseCommitFromHead(path, (Int, commit) => {
        val id = commit.toObjectId
        val name = id.getName
        FileUtils.copyDirectory(new File(path), new File("/home/xige/Desktop/" + name))
        checkout("/home/xige/Desktop/" + name, name)
    })
  }


  def cloneRemoteRepo(url : String, path: String) = {
    val directory = new File(path)
    if(directory.exists()) FileUtils.deleteDirectory(directory)
    Git.cloneRepository()
    .setURI(url)
    .setDirectory(directory).setBranch("master")
    .call()
  }


  def traverseCommitFromHead(path : String, handle : (Int, RevCommit) => Unit) = {
    val repository = new RepositoryBuilder().findGitDir(new File(path)).build()
    val walk = new RevWalk(repository)
    walk.markStart(walk.parseCommit(repository.resolve("HEAD")))
    val it = walk.iterator()
    var count = 0
    while(it.hasNext) {
      val commit = it.next()
      handle(count, commit)
      count = count + 1
    }
  }

  def checkout(folder : String, name : String) = {
    val git = Git.open(new File(folder))
    val command = git.checkout.setName(name)
    command.call
  }


}
