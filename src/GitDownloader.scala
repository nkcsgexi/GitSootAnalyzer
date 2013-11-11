import java.io.File
import org.apache.commons.io.FileUtils
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.RepositoryBuilder


object GitDownloader {

  def main(args : Array[String]) = {
    val url = "https://github.com/nkcsgexi/JVMBinaryAnalyzer.git"
    val path = "/home/xige/Desktop/re"
    // cloneRemoteRepo(url, path)
    val repository = new RepositoryBuilder().findGitDir(new File(path)).build()
    repository.getAllRefs.keySet().toArray.foreach(s => println(s.toString))

  }


  def cloneRemoteRepo(url : String, path: String) {
    val directory = new File(path)
    if(directory.exists()) FileUtils.deleteDirectory(directory)
    Git.cloneRepository()
    .setURI(url)
    .setDirectory(directory).setBranch("master")
    .call()
  }



}
