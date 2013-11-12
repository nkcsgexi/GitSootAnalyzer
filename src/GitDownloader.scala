import java.io.File
import org.apache.commons.io.FileUtils
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.RepositoryBuilder
import org.eclipse.jgit.revwalk.{RevCommit, RevWalk}


object GitDownloader {

  def main(args : Array[String]) = {
    val url = "https://github.com/nkcsgexi/JVMBinaryAnalyzer.git"
    val path = "/home/xige/Desktop/re"
    // cloneRemoteRepo(url, path)
    traverseCommitFromHead(path, commit => {
       var id = commit.toObjectId

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


  def traverseCommitFromHead(path : String, handle : RevCommit => Unit) = {
    val repository = new RepositoryBuilder().findGitDir(new File(path)).build()
    val walk = new RevWalk(repository)
    walk.markStart(walk.parseCommit(repository.resolve("HEAD")))
    val it = walk.iterator()
    while(it.hasNext) {
      val commit = it.next()
      handle(commit)
    }
  }

  def checkout(commit : RevCommit, destination : String) = {

  }


}
